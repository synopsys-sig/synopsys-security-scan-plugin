package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.*;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.service.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.ScannerArgumentService;
import com.synopsys.integration.jenkins.scan.service.diagnostics.DiagnosticsService;
import com.synopsys.integration.jenkins.scan.service.scan.blackduck.BlackDuckParametersService;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.ArtifactArchiver;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SecurityScanner {
    private final Run<?, ?> run;
    private final TaskListener listener;
    private final Launcher launcher;
    private final FilePath workspace;
    private final EnvVars envVars;
    private final ScannerArgumentService scannerArgumentService;

    public SecurityScanner(Run<?, ?> run, TaskListener listener, Launcher launcher, FilePath workspace,
                           EnvVars envVars, ScannerArgumentService scannerArgumentService) {
        this.run = run;
        this.listener = listener;
        this.launcher = launcher;
        this.workspace = workspace;
        this.envVars = envVars;
        this.scannerArgumentService = scannerArgumentService;
    }

    public int runScanner(Map<String, Object> scanParams) throws ScannerJenkinsException {
        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService(listener);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        Map<String, Object> blackDuckParameters = blackDuckParametersService.prepareBlackDuckParameterValidation(scanParams);
        int scanner = 1;

        if (blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParameters)
                && bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {
            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listener);
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);

            if (isBridgeDownloadRequired) {
                initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                listener.getLogger().printf(LogMessages.BRIDGE_INSTALLATION_ALREADY_FOUND_IN_PATH, bridgeDownloadParams.getBridgeInstallationPath());
            }

            Utility.copyRepository(bridgeDownloadParams.getBridgeInstallationPath(), workspace, listener);
            FilePath bridgeInstallationPath = new FilePath(new File(bridgeDownloadParams.getBridgeInstallationPath()));
            List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(scanParams, bridgeDownloadParams.getBridgeInstallationPath());

            try {
                printBridgeExecutionLogs(LogMessages.START_BRIDGE_EXECUTION);

                scanner = launcher.launch()
                        .cmds(commandLineArgs)
                        .envs(envVars)
                        .pwd(bridgeInstallationPath)
                        .stdout(listener)
                        .quiet(true)
                        .join();
            } catch (Exception e) {
                listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_INVOKING_SYNOPSYS_BRIDGE, e.getMessage());
            } finally {
                printBridgeExecutionLogs(LogMessages.END_BRIDGE_EXECUTION);

                Utility.removeFile(scannerArgumentService.getBlackDuckInputJsonFilePath(), workspace, listener);
                Utility.cleanupOtherFiles(workspace, listener);

                if ( Objects.equals(scanParams.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
                    uploadDiagnostics(bridgeInstallationPath);
                }
            }
        }

        return scanner;
    }

    private void initiateBridgeDownloadAndUnzip(BridgeDownloadParameters bridgeDownloadParams) {
        BridgeDownload bridgeDownload = new BridgeDownload(workspace, listener);
        BridgeInstall bridgeInstall = new BridgeInstall(workspace, listener);

        String bridgeDownloadUrl = bridgeDownloadParams.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParams.getBridgeInstallationPath();

        Utility.verifyAndCreateInstallationPath(bridgeInstallationPath, workspace, listener);

        try {
            FilePath bridgeZipPath = bridgeDownload.downloadSynopsysBridge(bridgeDownloadUrl);
            bridgeInstall.installSynopsysBridge(bridgeZipPath, new FilePath(new File(bridgeInstallationPath)));
        } catch (Exception e) {
            listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_DOWNLOADING_OR_INSTALLING_SYNOPSYS_BRIDGE, e.getMessage());
        }
    }

    public void printBridgeExecutionLogs(String message) {
        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(message);
        listener.getLogger().println(LogMessages.ASTERISKS);
    }

    private void uploadDiagnostics(FilePath bridgeInstallationPath) {
        DiagnosticsService diagnosticsService = new DiagnosticsService(run, listener, launcher, envVars,
            new ArtifactArchiver(ApplicationConstants.ALL_FILES_WILDCARD_SYMBOL));
        FilePath diagnosticsPath = new FilePath(workspace.getChannel(), bridgeInstallationPath.getRemote()
                .concat(Utility.getDirectorySeparator(workspace, listener))
                .concat(ApplicationConstants.BRIDGE_DIAGNOSTICS_DIRECTORY));
        diagnosticsService.archiveDiagnostics(diagnosticsPath);
    }

}
