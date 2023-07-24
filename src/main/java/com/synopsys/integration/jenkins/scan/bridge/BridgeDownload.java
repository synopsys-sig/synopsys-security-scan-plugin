package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class BridgeDownload {
    private final TaskListener listener;

    public BridgeDownload(TaskListener listener) {
        this.listener = listener;
    }

    public FilePath downloadSynopsysBridge(String bridgeDownloadUrl) {
        FilePath tempFilePath = null;

        if (checkIfBridgeUrlExists(bridgeDownloadUrl)) {
            try {
                int retryCount = 1;
                boolean downloadSuccess = false;

                while (!downloadSuccess && retryCount <= ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES) {
                    try {
                        listener.getLogger().println("Downloading Synopsys Bridge from: " + bridgeDownloadUrl);
                        File tempFile = File.createTempFile("bridge", ".zip");
                        tempFilePath = new FilePath(tempFile);

                        tempFilePath.copyFrom(new URL(bridgeDownloadUrl));
                        downloadSuccess = true;

                        listener.getLogger().println("Synopsys Bridge download is successful!");
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
                e.printStackTrace();
            }
        } else {
            listener.getLogger().println("Invalid Synopsys Bridge download URL: " + bridgeDownloadUrl);
        }
        return tempFilePath;
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
