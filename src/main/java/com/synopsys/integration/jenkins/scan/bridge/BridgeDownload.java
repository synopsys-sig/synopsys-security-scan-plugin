package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
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

                        bridgeZipFilePath = workspace.child("bridge.zip");
                        bridgeZipFilePath.copyFrom(new URL(bridgeDownloadUrl));
                        downloadSuccess = true;

                        listener.getLogger().println("Synopsys Bridge download is successful and bridge is downloaded in: " + bridgeZipFilePath);
                    } catch (Exception e) {
                        listener.getLogger().printf("Synopsys Bridge download failed. Attempt#%s to download again.%n", retryCount);
                        Thread.sleep(2500);
                        retryCount++;
                    }
                }

                if (!downloadSuccess) {
                    listener.getLogger().printf("Synopsys Bridge download failed after %s attempts.%n", ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES);
                }
            } catch (InterruptedException e) {
                listener.getLogger().println("Interrupted while waiting to retry Synopsys Bridge download");
                e.printStackTrace(listener.getLogger());
            }
        } else {
            listener.getLogger().println("Invalid Synopsys Bridge download URL: " + bridgeDownloadUrl);
        }
        listener.getLogger().println("Method downloadSynopsysBridge(): bridge zip file path: " + bridgeZipFilePath);
        return bridgeZipFilePath;
    }

    public boolean checkIfBridgeUrlExists(String bridgeDownloadUrl) {
        try {
            URL url = new URL(bridgeDownloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            listener.getLogger().println("The bridge download url doesn't exist: " + bridgeDownloadUrl);
            return false;
        }
    }
}
