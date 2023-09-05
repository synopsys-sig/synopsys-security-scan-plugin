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
import com.synopsys.integration.jenkins.scan.service.bridge.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.ScanParametersService;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.Map;
import java.util.Set;

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

    public int runScanner(Map<String, Object> scanParameters) throws ScannerJenkinsException {
        if (!scanParameters.containsKey(ApplicationConstants.SCAN_TYPE_KEY)) {
            throw new ScannerJenkinsException(LogMessages.NO_SCAN_TYPE_SELECTED);
        }

        logger.println("**************************** START EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");

        ScanParametersService scanParametersService = new ScanParametersService(listener);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParameters, bridgeDownloadParameters);

        logMessagesForParameters(scanParameters, scanParametersService.getScanTypes(scanParameters));

        int exitCode = -1;

        if (scanParametersService.isValidScanParameters(scanParameters) &&
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
                exitCode = scanner.runScanner(scanParameters, bridgeInstallationPath);
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

    public void logMessagesForParameters(Map<String, Object> scanParameters, Set<String> scanTypes) {
        logger.println("-------------------------- Parameter Validation Initiated --------------------------");

        logger.info(" --- " + ApplicationConstants.SCAN_TYPE_KEY + " = " + scanTypes.toString());

        for (String type : scanTypes) {
            String scanType = type.toLowerCase();
            logger.info("Parameters for %s:", scanType);

            for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
                String key = entry.getKey();
                if(key.contains(scanType)) {
                    Object value = entry.getValue();
                    if(key.equals(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY) || key.equals(ApplicationConstants.BRIDGE_POLARIS_ACCESS_TOKEN_KEY) || key.equals(ApplicationConstants.BRIDGE_COVERITY_CONNECT_USER_PASSWORD_KEY)) {
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
            if(key.equals(ApplicationConstants.BRIDGE_DOWNLOAD_URL) || key.equals(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION) || key.equals(ApplicationConstants.BRIDGE_INSTALLATION_PATH) || key.equals(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY)) {
                Object value = entry.getValue();
                logger.info(" --- " + key + " = " + value.toString());
            }
        }
    }

}
