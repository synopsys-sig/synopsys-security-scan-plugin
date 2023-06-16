package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloaderAndExecutor;
import com.synopsys.integration.jenkins.scan.global.LogMessages;

import hudson.model.TaskListener;
import java.io.IOException;

/**
 * @author akib @Date 6/15/23
 */
public class SecurityScanner {

    private final BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor;
    private final TaskListener listener;

    public SecurityScanner(
        BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor,
        TaskListener listener) {
        this.bridgeDownloaderAndExecutor = bridgeDownloaderAndExecutor;
        this.listener = listener;
    }

    public int runScanner() throws IOException, InterruptedException {
        bridgeDownloaderAndExecutor.downloadAndUnzipSynopsysBridge(null, null, listener);

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
