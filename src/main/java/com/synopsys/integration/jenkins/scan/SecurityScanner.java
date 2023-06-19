package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloaderAndExecutor;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.service.ScannerArgumentService;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.List;

/**
 * @author akib @Date 6/15/23
 */
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

    public int runScanner(String blackDuckArgs, String bridgeArgs) throws IOException, InterruptedException {
        BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor = new BridgeDownloaderAndExecutor(listener, envVars);
        FilePath downloadFilePath = Utility.createTempDir(ApplicationConstants.APPLICATION_NAME);

        FilePath bridgeZipPath = bridgeDownloaderAndExecutor.downloadSynopsysBridge(downloadFilePath,null, null);
        bridgeDownloaderAndExecutor.unzipSynopsysBridge(bridgeZipPath, workspace);
        Utility.cleanupTempDir(downloadFilePath);

        List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(blackDuckArgs, bridgeArgs);

        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(LogMessages.START_SCANNER);
        listener.getLogger().println(LogMessages.ASTERISKS);

        int scanner = launcher.launch()
            .cmds(commandLineArgs)
            .envs(envVars)
            .pwd(workspace)
            .stdout(listener)
            .quiet(true)
            .join();

        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(LogMessages.END_SCANNER);
        listener.getLogger().println(LogMessages.ASTERISKS);

        return scanner;
    }

}
