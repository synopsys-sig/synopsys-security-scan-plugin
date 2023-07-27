package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.remoting.Channel;
import jenkins.model.Jenkins;
import jenkins.security.MasterToSlaveCallable;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class BridgeDownload {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeDownload(TaskListener listener, FilePath workspace) {
        this.listener = listener;
        this.workspace = workspace;
    }

    /*public FilePath downloadSynopsysBridge(String bridgeDownloadUrl) {
        FilePath tempFilePath = null;

        if (checkIfBridgeUrlExists(bridgeDownloadUrl)) {
            try {
                int retryCount = 1;
                boolean downloadSuccess = false;

                while (!downloadSuccess && retryCount <= ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES) {
                    try {
                        listener.getLogger().println("Downloading Synopsys Bridge from: " + bridgeDownloadUrl);

                        // Get the temporary directory based on where Jenkins is running
                        FilePath tempDir;
                        if (isRunningOnAgent()) {
                            tempDir = workspace.createTempDir("bridge","tmp");
                            listener.getLogger().println("Running on agent node");
                        } else {
                            tempDir = Jenkins.getInstance().getRootPath().child("temp");
                            listener.getLogger().println("Running on master node");
                        }

                        // Create the temporary file
                        tempFilePath = tempDir.child("bridge.zip");

                        // Download the Synopsys Bridge to the temporary file
                        tempFilePath.copyFrom(new URL(bridgeDownloadUrl));
                        downloadSuccess = true;

                        listener.getLogger().println("Synopsys Bridge download is successful and bridge is downloaded in: " + tempFilePath);
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
        return tempFilePath;
    }*/

    public FilePath downloadSynopsysBridge(String bridgeDownloadUrl) {
        FilePath bridgeZipFilePath = null;

        if (checkIfBridgeUrlExists(bridgeDownloadUrl)) {
            try {
                int retryCount = 1;
                boolean downloadSuccess = false;

                while (!downloadSuccess && retryCount <= ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES) {
                    try {
                        listener.getLogger().println("Downloading Synopsys Bridge from: " + bridgeDownloadUrl);

                        // Create a directory in the workspace to store the downloaded zip
//                        FilePath bridgeDirectory = workspace.child("synopsys-bridge");
//                        bridgeDirectory.mkdirs();

                        bridgeZipFilePath = workspace.child("bridge.zip");

                        // Download the Synopsys Bridge to the FilePath
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

    private boolean isRunningOnAgent() {
        // Check if Jenkins is running on an agent or the master node
        return workspace != null && workspace.isRemote();
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
