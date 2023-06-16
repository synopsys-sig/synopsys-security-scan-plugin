package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloaderAndExecutor;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akib @Date 6/15/23
 */
public class SecurityScanner {

    private final TaskListener listener;
    private final Launcher launcher;
    private final FilePath workspace;
    private final EnvVars envVars;

    public SecurityScanner(TaskListener listener, Launcher launcher, FilePath workspace,
                           EnvVars envVars) {
        this.listener = listener;
        this.launcher = launcher;
        this.workspace = workspace;
        this.envVars = envVars;
    }

    public int runScanner() throws IOException, InterruptedException {
        BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor = new BridgeDownloaderAndExecutor(listener, envVars);
        FilePath downloadFilePath = Utility.createTempDir(ApplicationConstants.APPLICATION_NAME);

        FilePath bridgeZipPath = bridgeDownloaderAndExecutor.downloadSynopsysBridge(downloadFilePath,null, null);
        bridgeDownloaderAndExecutor.unzipSynopsysBridge(bridgeZipPath, downloadFilePath);

        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(LogMessages.START_SCANNER);
        listener.getLogger().println(LogMessages.ASTERISKS);

        List<String> commandLine = new ArrayList<>();
        commandLine.add("synopsys-bridge --help"); //test command
        int scanner = launcher.launch()
            .cmds(commandLine)
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
