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
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.bridge.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategyFactory;
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategy;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.Map;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;
    private final FilePath workspace;
    private final TaskListener listener;
    private final LoggerWrapper logger;

    public ScanPipelineCommands(SecurityScanner scanner, FilePath workspace, TaskListener listener) {
        this.scanner = scanner;
        this.workspace = workspace;
        this.listener = listener;
        this.logger = new LoggerWrapper(listener);
    }

    public int runScanner(Map<String, Object> scanParameters, ScanStrategyFactory scanStrategyFactory) throws ScannerJenkinsException {

        logger.println("**************************** START EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");

        ScanStrategy scanStrategy = scanStrategyFactory.getParametersService(scanParameters);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParameters, bridgeDownloadParameters);

        logMessagesForParameters(scanParameters);

        int exitCode = -1;

        if (scanStrategy.isValidScanParameters(scanParameters) &&
            bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {
            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listener);
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);

            if (isBridgeDownloadRequired) {
                bridgeDownloadManager.initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                logger.info("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
                logger.println(LogMessages.DASHES);
            }
            FilePath bridgeInstallationPath = new FilePath(workspace.getChannel(), bridgeDownloadParams.getBridgeInstallationPath());

            try {
                exitCode = scanner.runScanner(scanParameters, scanStrategy, bridgeInstallationPath);
            } catch (Exception e) {
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
            }
        }

        try {
            if (exitCode != 0) {
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
            }
        }
        finally {
            logger.println("**************************** END EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");
        }

        return exitCode;
    }

    public void logMessagesForParameters(Map<String, Object> scanParameters) {
        logger.println("-------------------------- Parameter Validation Initiated --------------------------");

        String scanType = ((ScanType) scanParameters.get(ApplicationConstants.SCAN_TYPE_KEY)).toString().toLowerCase();

        logger.info(" --- " + ApplicationConstants.SCAN_TYPE_KEY + " = " + scanType);
        logger.info("parameters for %s:", scanType);

        for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
            String key = entry.getKey();
            if(key.contains(scanType)) {
                if(key.equals(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY) || key.equals(ApplicationConstants.BRIDGE_POLARIS_ACCESS_TOKEN_KEY) || key.equals(ApplicationConstants.BRIDGE_COVERITY_CONNECT_USER_PASSWORD_KEY)) {
                    entry.setValue(LogMessages.ASTERISKS);
                }
                Object value = entry.getValue();
                logger.info(" --- " + key + " = " + value.toString());
            }

        }

        logger.println(LogMessages.DASHES);

        logger.info("parameters for bridge:");

        for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
            String key = entry.getKey();
            if(key.equals(ApplicationConstants.BRIDGE_DOWNLOAD_URL) || key.equals(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION) || key.equals(ApplicationConstants.BRIDGE_INSTALLATION_PATH) || key.equals(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY)) {
                Object value = entry.getValue();
                logger.info(" --- " + key + " = " + value.toString());
            }
        }
    }

}
