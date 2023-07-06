package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.*;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.service.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.BridgeDownloadParametersService;
import com.synopsys.integration.jenkins.scan.service.ScannerArgumentService;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SecurityScanner {
    private final TaskListener listener;
    private final Launcher launcher;
    private final FilePath workspace;
    private final EnvVars envVars;
    private final ScannerArgumentService scannerArgumentService;
    
    public SecurityScanner(TaskListener listener, Launcher launcher, FilePath workspace,
                           EnvVars envVars, ScannerArgumentService scannerArgumentService) {
        this.listener = listener;
        this.launcher = launcher;
        this.workspace = workspace;
        this.envVars = envVars;
        this.scannerArgumentService = scannerArgumentService;
    }

    public int runScanner(Map<String, Object> scanParams) {
        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService();

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters();
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService();
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        Map<String, Object> blackDuckParameters = blackDuckParametersService.prepareBlackDuckParameterValidation(scanParams);
        int scanner = 1;

        if (blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParameters)
                && bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {

            FilePath bridgeInstallationPath = new FilePath(new File(bridgeDownloadParams.getBridgeInstallationPath()));
            List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(bridgeInstallationPath, scanParams);

            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager();
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);
            if (isBridgeDownloadRequired) {
                initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
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
                e.printStackTrace();
            } finally {
                Utility.cleanupInputJson(bridgeDownloadParams.getBridgeInstallationPath(), BridgeParams.BLACKDUCK_JSON_FILE_NAME);
            }
        }
        else {
           listener.getLogger().println("Couldn't validate BlackDuck or Synopsys-Bridge parameters!");
        }

        printMessages(LogMessages.END_SCANNER);

        return scanner;
    }

    private void initiateBridgeDownloadAndUnzip(BridgeDownloadParameters bridgeDownloadParams) {
        BridgeDownload bridgeDownload = new BridgeDownload(listener);
        BridgeInstall bridgeInstall = new BridgeInstall(listener);

        String bridgeDownloadUrl = bridgeDownloadParams.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParams.getBridgeInstallationPath();
        String bridgeDownloadVersion = bridgeDownloadParams.getBridgeDownloadVersion();

        Utility.verifyAndCreateInstallationPath(bridgeInstallationPath);

        try {
            FilePath bridgeZipPath = bridgeDownload.downloadSynopsysBridge(bridgeDownloadVersion, bridgeDownloadUrl);
            bridgeInstall.installSynopsysBridge(bridgeZipPath, new FilePath(new File(bridgeInstallationPath)));
        } catch (Exception e) {
            listener.getLogger().println("There is an exception while downloading/installing Synopsys-bridge.");
        }
    }

    public void printMessages(String message) {
        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(message);
        listener.getLogger().println(LogMessages.ASTERISKS);
    }

}
