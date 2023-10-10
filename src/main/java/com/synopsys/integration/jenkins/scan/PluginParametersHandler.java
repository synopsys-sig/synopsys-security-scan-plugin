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
import com.synopsys.integration.jenkins.scan.exception.PluginExceptionHandler;
import com.synopsys.integration.jenkins.scan.exception.ScannerException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.service.bridge.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.ScanParametersService;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.util.Map;
import java.util.Set;

public class PluginParametersHandler {
    private final SecurityScanner scanner;
    private final FilePath workspace;
    private final TaskListener listener;
    private final EnvVars envVars;
    private final LoggerWrapper logger;

    public PluginParametersHandler(SecurityScanner scanner, FilePath workspace, EnvVars envVars, TaskListener listener) {
        this.scanner = scanner;
        this.workspace = workspace;
        this.listener = listener;
        this.envVars = envVars;
        this.logger = new LoggerWrapper(listener);
    }

    public int initializeScanner(Map<String, Object> scanParameters) throws PluginExceptionHandler, ScannerException {
        ScanParametersService scanParametersService = new ScanParametersService(listener);
        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParameters, bridgeDownloadParameters);

        logMessagesForParameters(scanParameters, scanParametersService.getSynopsysSecurityProducts(scanParameters));

        int exitCode = -1;

        if (isValidScanParametersAndBridgeDownload(bridgeDownloadParams, scanParametersService, bridgeDownloadParametersService, scanParameters)) {
            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listener, envVars);
            boolean isNetworkAirGap = checkNetworkAirgap(scanParameters);
            boolean isBridgeInstalled = bridgeDownloadManager.checkIfBridgeInstalled(bridgeDownloadParams.getBridgeInstallationPath());
            boolean isBridgeDownloadRequired = true;

            handleNetworkAirgap(isNetworkAirGap, bridgeDownloadParams, isBridgeInstalled);

            if (isBridgeInstalled) {
                isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);
            }

            handleBridgeDownload(isBridgeDownloadRequired, isNetworkAirGap, bridgeDownloadParams, bridgeDownloadManager);

            FilePath bridgeInstallationPath = new FilePath(workspace.getChannel(), bridgeDownloadParams.getBridgeInstallationPath());

            exitCode = runScanner(scanParameters, bridgeInstallationPath);
        }

        handleExitCode(exitCode);
        return exitCode;
    }

    private boolean isValidScanParametersAndBridgeDownload(BridgeDownloadParameters bridgeDownloadParams, ScanParametersService scanParametersService, BridgeDownloadParametersService bridgeDownloadParametersService,Map<String, Object> scanParameters) {
        return scanParametersService.isValidScanParameters(scanParameters) &&
                bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams);
    }

    private boolean checkNetworkAirgap(Map<String, Object> scanParameters) {
        return scanParameters.containsKey(ApplicationConstants.NETWORK_AIRGAP_KEY) &&
                ((Boolean) scanParameters.get(ApplicationConstants.NETWORK_AIRGAP_KEY)).equals(true);
    }

    private void handleNetworkAirgap(boolean isNetworkAirgap, BridgeDownloadParameters bridgeDownloadParams, boolean isBridgeInstalled) throws PluginExceptionHandler {
        if (isNetworkAirgap && !bridgeDownloadParams.getBridgeDownloadUrl().contains(".zip") && !isBridgeInstalled) {
            logger.error("Synopsys Bridge could not be found in " + bridgeDownloadParams.getBridgeInstallationPath());
            throw new PluginExceptionHandler("Synopsys Bridge could not be found in " + bridgeDownloadParams.getBridgeInstallationPath());
        }

        if (isNetworkAirgap) {
            logger.info("Network Air Gap mode is enabled");
        }
    }

    private void handleBridgeDownload(boolean isBridgeDownloadRequired, boolean isNetworkAirgap, BridgeDownloadParameters bridgeDownloadParams, BridgeDownloadManager bridgeDownloadManager) throws PluginExceptionHandler {
        if (isBridgeDownloadRequired && bridgeDownloadParams.getBridgeDownloadUrl().contains(".zip")) {
            if (isNetworkAirgap) {
                logger.warn("Synopsys-Bridge will be downloaded from the provided custom URL. Make sure the network is reachable");
            }
            bridgeDownloadManager.initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
        } else {
            logger.info("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
            logger.println(LogMessages.DASHES);
        }
    }

    private int runScanner(Map<String, Object> scanParameters, FilePath bridgeInstallationPath) throws PluginExceptionHandler, ScannerException {
        try {
            return scanner.runScanner(scanParameters, bridgeInstallationPath);
        } catch (Exception e) {
            if (e instanceof PluginExceptionHandler) {
                throw new PluginExceptionHandler("Workflow failed! " + e.getMessage());
            } else {
                throw new ScannerException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
            }
        }
    }

    private void handleExitCode(int exitCode) throws PluginExceptionHandler {
        if (exitCode != 0) {
            Map<Integer, String> exitCodeToMessage = ExceptionMessages.bridgeErrorMessages();
            logger.error(exitCodeToMessage.getOrDefault(exitCode, ExceptionMessages.scannerFailedWithExitCode(exitCode)));
            throw new PluginExceptionHandler("Workflow failed!");
        }
    }


    public void logMessagesForParameters(Map<String, Object> scanParameters, Set<String> securityProducts) {
        logger.println("-------------------------- Parameter Validation Initiated --------------------------");

        logger.info(" --- " + ApplicationConstants.PRODUCT_KEY + " = " + securityProducts.toString());

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