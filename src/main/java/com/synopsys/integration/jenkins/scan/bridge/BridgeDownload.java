package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.net.HttpURLConnection;
import java.net.URL;

public class BridgeDownload {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeDownload(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
    }

    public FilePath downloadSynopsysBridge(String bridgeDownloadUrl) {
        FilePath bridgeZipFilePath = null;

        if (checkIfBridgeUrlExists(bridgeDownloadUrl)) {
            try {
                int retryCount = 1;
                boolean downloadSuccess = false;

                while (!downloadSuccess && retryCount <= ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES) {
                    try {
                        listener.getLogger().println("Downloading Synopsys Bridge from: " + bridgeDownloadUrl);

                        bridgeZipFilePath = workspace.child(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT);
                        bridgeZipFilePath.copyFrom(new URL(bridgeDownloadUrl));
                        downloadSuccess = true;

                        listener.getLogger().println("Synopsys Bridge successfully downloaded in: " + bridgeZipFilePath);
                    } catch (Exception e) {
                        listener.getLogger().printf("Synopsys Bridge download failed and attempt#%s to download again %n", retryCount);
                        Thread.sleep(2500);
                        retryCount++;
                    }
                }

                if (!downloadSuccess) {
                    listener.getLogger().printf(LogMessages.SYNOPSYS_BRIDGE_DOWNLOAD_FAILED_AND_WITH_MAX_ATTEMPT, ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES);
                }
            } catch (InterruptedException e) {
                listener.getLogger().println("Interrupted while waiting to retry Synopsys Bridge download");
                e.printStackTrace(listener.getLogger());
            }
        } else {
            listener.getLogger().printf(LogMessages.INVALID_SYNOPSYS_BRIDGE_DOWNLOAD_URL, bridgeDownloadUrl);
        }
        return bridgeZipFilePath;
    }

    public boolean checkIfBridgeUrlExists(String bridgeDownloadUrl) {
        try {
            URL url = new URL(bridgeDownloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            listener.getLogger().println("An exception occurred while checking bridge url exists or not: " + e.getMessage());
            return false;
        }
    }
}
