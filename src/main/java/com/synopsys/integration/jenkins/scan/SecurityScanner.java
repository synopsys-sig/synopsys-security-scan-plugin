package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloaderAndExecutor;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;

import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.IOException;

/**
 * @author akib @Date 6/15/23
 */
public class SecurityScanner {

    private final TaskListener listener;

    public SecurityScanner(TaskListener listener) {
        this.listener = listener;
    }

    public int runScanner() throws IOException, InterruptedException {
        BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor = new BridgeDownloaderAndExecutor(listener);
        FilePath downloadFilePath = Utility.createTempDir(ApplicationConstants.APPLICATION_NAME);

        FilePath bridgeZipPath = bridgeDownloaderAndExecutor.downloadSynopsysBridge(downloadFilePath,null, null);
        bridgeDownloaderAndExecutor.unzipSynopsysBridge(bridgeZipPath, downloadFilePath);

        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(LogMessages.START_SCANNER);
        listener.getLogger().println(LogMessages.ASTERISKS);

        int scanner = 0;

        listener.getLogger().println(LogMessages.ASTERISKS);
        listener.getLogger().println(LogMessages.END_SCANNER);
        listener.getLogger().println(LogMessages.ASTERISKS);

        return scanner;
    }

}
