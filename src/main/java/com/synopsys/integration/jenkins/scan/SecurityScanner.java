package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.*;
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
        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService(listener);

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listener);
        BridgeDownloadParametersService bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listener);
        BridgeDownloadParameters bridgeDownloadParams = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        Map<String, Object> blackDuckParameters = blackDuckParametersService.prepareBlackDuckParameterValidation(scanParams);
        int scanner = 1;

        if (blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParameters)
                && bridgeDownloadParametersService.performBridgeDownloadParameterValidation(bridgeDownloadParams)) {

//            FilePath bridgeInstallationPath = new FilePath(new File(bridgeDownloadParams.getBridgeInstallationPath()));
//            List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(scanParams, bridgeDownloadParams.getBridgeInstallationPath());


            BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace,listener);
            boolean isBridgeDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParams);

            if (isBridgeDownloadRequired) {
                initiateBridgeDownloadAndUnzip(bridgeDownloadParams);
            } else {
                listener.getLogger().println("Bridge download is not required. Found installed in: " + bridgeDownloadParams.getBridgeInstallationPath());
            }

            Utility.copyRepository(bridgeDownloadParams.getBridgeInstallationPath(), workspace, listener);
            FilePath bridgeInstallationPath = new FilePath(new File(bridgeDownloadParams.getBridgeInstallationPath()));
            List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(scanParams, bridgeDownloadParams.getBridgeInstallationPath());
            String argsAsString = String.join(" ", commandLineArgs);
            listener.getLogger().println("Method runScanner(): bridgeArgs: " + argsAsString);

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
                Utility.removeFile(scannerArgumentService.getBlackDuckInputJsonFilePath(), workspace, listener);
                Utility.cleanupOtherFiles(workspace, listener);
            }
        }

        printMessages(LogMessages.END_SCANNER);

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
            listener.getLogger().println("There is an exception while downloading/installing Synopsys-bridge: " + e.getMessage());
        }
    }

    public void printMessages(String message) {
        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(message);
        listener.getLogger().println(LogMessages.ASTERISKS);
    }

}
