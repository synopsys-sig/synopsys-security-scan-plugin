package com.synopsys.integration.jenkins.scan.bridge;

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
                listener.getLogger().println("Downloading synopsys bridge from: " + bridgeDownloadUrl);
                File tempFile = File.createTempFile("bridge", ".zip");

                tempFilePath = new FilePath(tempFile);
                tempFilePath.copyFrom(new URL(bridgeDownloadUrl));

            } catch (Exception e) {
                listener.getLogger().println("Synopsys bridge download failed");
            }
        }
        else {
            listener.getLogger().println("Invalid synopsys bridge download url: " + bridgeDownloadUrl);
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
