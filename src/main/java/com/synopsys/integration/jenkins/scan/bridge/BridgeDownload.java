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
                        listener.getLogger().printf(LogMessages.DOWNLOADING_SYNOPSYS_BRIDGE_FROM_URL, bridgeDownloadUrl);

                        bridgeZipFilePath = workspace.child(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT);
                        bridgeZipFilePath.copyFrom(new URL(bridgeDownloadUrl));
                        downloadSuccess = true;

                        listener.getLogger().printf(LogMessages.SYNOPSYS_BRIDGE_SUCCESSFULLY_DOWNLOADED_IN_PATH, bridgeZipFilePath);
                    } catch (Exception e) {
                        listener.getLogger().printf(LogMessages.SYNOPSYS_BRIDGE_DOWNLOAD_FAILED_AND_RETRY, retryCount);
                        Thread.sleep(2500);
                        retryCount++;
                    }
                }

                if (!downloadSuccess) {
                    listener.getLogger().printf(LogMessages.SYNOPSYS_BRIDGE_DOWNLOAD_FAILED_AND_WITH_MAX_ATTEMPT, ApplicationConstants.BRIDGE_DOWNLOAD_MAX_RETRIES);
                }
            } catch (InterruptedException e) {
                listener.getLogger().println(LogMessages.SYNOPSYS_BRIDGE_DOWNLOAD_INTERRUPTED);
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
            listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_CHECKING_BRIDGE_URL_EXISTENCE, e.getMessage());
            return false;
        }
    }
}
