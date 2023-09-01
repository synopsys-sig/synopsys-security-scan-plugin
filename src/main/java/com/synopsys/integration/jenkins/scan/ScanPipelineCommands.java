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
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
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

        int exitCode = -1;


        logger.println("-------------------------- Parameter Validation Initiated --------------------------");

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

        if (exitCode != 0) {
            throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
        }

        return exitCode;
    }

}
