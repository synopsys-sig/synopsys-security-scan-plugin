package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadManager;
import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadParameters;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.service.bridge.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.strategy.ScanStrategyFactory;
import com.synopsys.integration.jenkins.scan.service.scan.strategy.ScanStrategy;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.util.Map;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;
    private final FilePath workspace;
    private final TaskListener listener;

    public ScanPipelineCommands(SecurityScanner scanner, FilePath workspace, TaskListener listener) {
        this.scanner = scanner;
        this.workspace = workspace;
        this.listener = listener;
    }

    public int runScanner(Map<String, Object> scanParameters, ScanStrategyFactory scanStrategyFactory) throws ScannerJenkinsException {
        ScanStrategy scanStrategy = scanStrategyFactory.getParametersService(scanParameters);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParameters, bridgeDownloadParameters);

        int exitCode = -1;

        if (scanStrategy.isValidScanParameters(scanParameters) &&
            bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {
            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listener);
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);

            if (isBridgeDownloadRequired) {
                bridgeDownloadManager.initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                listener.getLogger().println("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
            }
            FilePath bridgeInstallationPath = new FilePath(workspace.getChannel(), bridgeDownloadParams.getBridgeInstallationPath());

            try {
                exitCode = scanner.runScanner(scanParameters, scanStrategy, bridgeInstallationPath);
            } catch (Exception e) {
                e.printStackTrace(listener.getLogger());
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
            }
        }

        if (exitCode != 0) {
            throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
        }

        return exitCode;
    }

}
