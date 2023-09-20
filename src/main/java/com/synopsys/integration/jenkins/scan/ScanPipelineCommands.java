/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadManager;
import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadParameters;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.global.enums.SecurityProduct;
import com.synopsys.integration.jenkins.scan.service.bridge.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.ScanParametersService;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;
    private final FilePath workspace;
    private final TaskListener listener;
    private final EnvVars envVars;
    private final LoggerWrapper logger;
    private final Run<?, ?> run;

    public ScanPipelineCommands(SecurityScanner scanner, FilePath workspace, EnvVars envVars, TaskListener listener, Run<?, ?> run) {
        this.scanner = scanner;
        this.workspace = workspace;
        this.listener = listener;
        this.envVars = envVars;
        this.logger = new LoggerWrapper(listener);
        this.run = run;
    }

    public int initializeScanner(Map<String, Object> scanParameters) throws ScannerJenkinsException {
        logger.println("**************************** START EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");

        ScanParametersService scanParametersService = new ScanParametersService(listener);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParameters, bridgeDownloadParameters);

        logMessagesForParameters(scanParameters, scanParametersService.getSynopsysSecurityProducts(scanParameters));

        validateSecurityProduct(scanParameters);

        int exitCode = -1;
        Map<Integer, String> exitCodeToMessage = ExceptionMessages.bridgeErrorMessages();

        if (scanParametersService.isValidScanParameters(scanParameters) &&
                bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {
            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listener, envVars);

            boolean isNetworkAirgap = scanParameters.containsKey(ApplicationConstants.NETWORK_AIRGAP_KEY) &&
                ((Boolean)scanParameters.get(ApplicationConstants.NETWORK_AIRGAP_KEY)).equals(true);
            boolean isBridgeInstalled = bridgeDownloadManager.checkIfBridgeInstalled(bridgeDownloadParameters.getBridgeInstallationPath());

            if (isNetworkAirgap) {
                logger.info("Network Air Gap mode is enabled");

                if (!bridgeDownloadParams.getBridgeDownloadUrl().contains(".zip") &&
                    !isBridgeInstalled) {
                    logger.error("Synopsys Bridge could not be found in " + bridgeDownloadParams.getBridgeInstallationPath());
                    throw new ScannerJenkinsException("Synopsys Bridge could not be found in " + bridgeDownloadParams.getBridgeInstallationPath());
                }
            }

            boolean isBridgeDownloadRequired = true;
            if (isBridgeInstalled) {
                isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);
            }

            if (isBridgeDownloadRequired && bridgeDownloadParams.getBridgeDownloadUrl().contains(".zip")) {
                if (isNetworkAirgap) {
                    logger.warn("Synopsys-Bridge will be downloaded from provided custom url. Make sure network is reachable");
                }

                bridgeDownloadManager.initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                logger.info("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
                logger.println(LogMessages.DASHES);
            }
            FilePath bridgeInstallationPath = new FilePath(workspace.getChannel(), bridgeDownloadParams.getBridgeInstallationPath());

            try {
                exitCode = scanner.runScanner(scanParameters, bridgeInstallationPath);
            } catch (Exception e) {
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
            }
        }

        try {
            if (exitCodeToMessage.containsKey(exitCode)) {
                logger.error(exitCodeToMessage.get(exitCode));
                run.setResult(Result.FAILURE);
            } else if(!exitCodeToMessage.containsKey(exitCode) && exitCode != 0){
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
            }
        }
        finally {
            logger.println("**************************** END EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");
        }

        return exitCode;
    }

    private void validateSecurityProduct(Map<String, Object> scanParameters) throws ScannerJenkinsException {
        String securityProduct = scanParameters.get(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY).toString();
        if (securityProduct.isBlank() ||
            !(securityProduct.contains(SecurityProduct.BLACKDUCK.name()) ||
            securityProduct.contains(SecurityProduct.POLARIS.name()) ||
            securityProduct.contains(SecurityProduct.COVERITY.name()))) {
            logger.error(LogMessages.INVALID_SYNOPSYS_SECURITY_PRODUCT);
            logger.info("Supported Synopsys Security Products: " + Arrays.toString(SecurityProduct.values()));
            throw new ScannerJenkinsException(LogMessages.INVALID_SYNOPSYS_SECURITY_PRODUCT);
        }
    }

    public void logMessagesForParameters(Map<String, Object> scanParameters, Set<String> securityProducts) {
        logger.println("-------------------------- Parameter Validation Initiated --------------------------");

        logger.info(" --- " + ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY + " = " + securityProducts.toString());

        for (String product : securityProducts) {
            String securityProduct = product.toLowerCase();
            logger.info("Parameters for %s:", securityProduct);

            for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
                String key = entry.getKey();
                if(key.contains(securityProduct)) {
                    Object value = entry.getValue();
                    if(key.equals(ApplicationConstants.BLACKDUCK_TOKEN_KEY) || key.equals(ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY)
                            || key.equals(ApplicationConstants.COVERITY_PASSPHRASE_KEY)) {
                        value = LogMessages.ASTERISKS;
                    }
                    logger.info(" --- " + key + " = " + value.toString());
                }
            }

            logger.println(LogMessages.DASHES);
        }

        logger.info("Parameters for bridge:");

        for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
            String key = entry.getKey();
            if(key.equals(ApplicationConstants.SYNOPSYS_BRIDGE_DOWNLOAD_URL) || key.equals(ApplicationConstants.SYNOPSYS_BRIDGE_DOWNLOAD_VERSION)
                    || key.equals(ApplicationConstants.SYNOPSYS_BRIDGE_INSTALL_DIRECTORY) || key.equals(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY)) {
                Object value = entry.getValue();
                logger.info(" --- " + key + " = " + value.toString());
            }
        }
    }

}
