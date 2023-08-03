package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.*;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.service.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.diagnostics.DiagnosticsService;
import com.synopsys.integration.jenkins.scan.service.ScannerArgumentService;
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

    public SecurityScanner( Run<?, ?> run, TaskListener listener, Launcher launcher, FilePath workspace,
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

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters();
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        Map<String, Object> blackDuckParameters = blackDuckParametersService.prepareBlackDuckParameterValidation(scanParams);
        int scanner = 1;

        if (blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParameters)
                && bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {

            FilePath bridgeInstallationPath = new FilePath(new File(bridgeDownloadParams.getBridgeInstallationPath()));
            List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(scanParams);

            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(listener);
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);

            if (isBridgeDownloadRequired) {
                initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                listener.getLogger().println("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
            }

            Utility.copyRepository(workspace.getRemote(), bridgeDownloadParams.getBridgeInstallationPath());

            printMessages(LogMessages.START_SCANNER);

            try {
                scanner = launcher.launch()
                        .cmds(commandLineArgs)
                        .envs(envVars)
                        .pwd(bridgeInstallationPath)
                        .stdout(listener)
                        .quiet(true)
                        .join();
            } catch (Exception e) {
                listener.getLogger().println("Exception occurred while invoking synopsys-bridge from the plugin : " + e.getMessage());
            } finally {
                Utility.cleanupInputJson(scannerArgumentService.getBlackDuckInputJsonFilePath());

                if ( Objects.equals(scanParams.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
                    DiagnosticsService diagnosticsService = new DiagnosticsService(run, listener, launcher, envVars,
                        new ArtifactArchiver(ApplicationConstants.ALL_FILES_WILDCARD_SYMBOL));
                    diagnosticsService.archiveDiagnostics(bridgeInstallationPath.child(ApplicationConstants.BRIDGE_DIAGNOSTICS_DIRECTORY));
                }
            }
        }

        printMessages(LogMessages.END_SCANNER);

        return scanner;
    }

    private void initiateBridgeDownloadAndUnzip(BridgeDownloadParameters bridgeDownloadParams) {
        BridgeDownload bridgeDownload = new BridgeDownload(listener);
        BridgeInstall bridgeInstall = new BridgeInstall(listener);

        String bridgeDownloadUrl = bridgeDownloadParams.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParams.getBridgeInstallationPath();

        Utility.verifyAndCreateInstallationPath(bridgeInstallationPath);

        try {
            FilePath bridgeZipPath = bridgeDownload.downloadSynopsysBridge(bridgeDownloadUrl);
            bridgeInstall.installSynopsysBridge(bridgeZipPath, new FilePath(new File(bridgeInstallationPath)));
        } catch (Exception e) {
            listener.getLogger().println("There is an exception while downloading/installing Synopsys-bridge: " + e.getMessage());
        }
    }

    public void printMessages(String message) {
        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(message);
        listener.getLogger().println(LogMessages.ASTERISKS);
    }

}
