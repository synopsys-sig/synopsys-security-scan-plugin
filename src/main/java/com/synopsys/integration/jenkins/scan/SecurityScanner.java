package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloaderAndExecutor;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.service.ScannerArgumentService;
import com.synopsys.integration.jenkins.scan.service.BlackDuckParametersService;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.List;

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

    public int runScanner(String stageParams, String bridgeParams) throws IOException, InterruptedException {

        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService();

        //TODO: add validation for Synopsys-Bridge parameters in the if condition as well.
        if (!blackDuckParametersService.validateBlackDuckParameters(stageParams)) {
            listener.getLogger().println("Couldn't validate BlackDuck or Synopsys-Bridge parameters!");
            return 1;
        }

        List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(workspace, stageParams);

        initiateBridgeDownloadAndUnzip(listener, envVars, workspace);

        printMessages(LogMessages.START_SCANNER);

        int scanner = launcher.launch()
                .cmds(commandLineArgs)
                .envs(envVars)
                .pwd(workspace)
                .stdout(listener)
                .quiet(true)
                .join();

        printMessages(LogMessages.END_SCANNER);

        return scanner;
    }

    private void initiateBridgeDownloadAndUnzip(TaskListener listener, EnvVars envVars, FilePath workspace) throws InterruptedException, IOException {
        BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor = new BridgeDownloaderAndExecutor(listener, envVars);
        FilePath downloadFilePath = Utility.createTempDir(ApplicationConstants.APPLICATION_NAME);

        try {
            FilePath bridgeZipPath = bridgeDownloaderAndExecutor.downloadSynopsysBridge(downloadFilePath, null, null);
            bridgeDownloaderAndExecutor.unzipSynopsysBridge(bridgeZipPath, workspace);
        } catch (Exception e) {
            listener.getLogger().println("There is an exception while downloading/unzipping Synopsys-bridge.");
            e.getStackTrace();
        } finally {
            Utility.cleanupTempDir(downloadFilePath);
        }
    }

    public void printMessages(String message) {
        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(message);
        listener.getLogger().println(LogMessages.ASTERISKS);
    }

}
