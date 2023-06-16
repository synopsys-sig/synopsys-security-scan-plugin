package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.FilePath;
import hudson.model.TaskListener;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author akib @Date 6/15/23
 */
public class BridgeDownloaderAndExecutor {

    private final String bridgeZipFileName = "bridge.zip";
    private final TaskListener listener;

    public BridgeDownloaderAndExecutor(TaskListener listener) {
        this.listener = listener;
    }

    public FilePath downloadSynopsysBridge(FilePath downloadFilePath, String bridgeVersion, String bridgeDownloadUrl) {
        FilePath bridgeZipPath = downloadFilePath.child(bridgeZipFileName);
        String bridgeUrl;
        
        if (isValidVersion(bridgeVersion)) {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL + bridgeVersion + "/" +
                ApplicationConstants.getSynopsysBridgeZipFileName(ApplicationConstants.PLATFORM_LINUX);
        } else if (isValidBridgeDownloadUrl(bridgeDownloadUrl)){
            bridgeUrl = bridgeDownloadUrl;
        } else {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL +
                ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION + "/" +
                ApplicationConstants.getSynopsysBridgeZipFileName(ApplicationConstants.PLATFORM_LINUX);
        }

        if (checkIfBridgeUrlExists(bridgeUrl)) {
            try {
                listener.getLogger().println("Downloading synopsys bridge from: " + bridgeUrl);
                bridgeZipPath.copyFrom(new URL(bridgeUrl));
                listener.getLogger().println("Synopsys bridge downloaded successfully to: " + bridgeZipPath.getRemote());
            } catch (Exception e) {
                listener.getLogger().println("Synopsys bridge download failed");
                e.printStackTrace();
            }
        } else {
            listener.getLogger().println("Invalid synopsys bridge download url: " + bridgeUrl);
        }
        return bridgeZipPath;
    }

    public void unzipSynopsysBridge(FilePath bridgeZipPath, FilePath downloadFilePath) {
        try {
            listener.getLogger().println("Unzipping synopsys bridge from: " + downloadFilePath);
            bridgeZipPath.unzip(downloadFilePath);
            // Delete the zip file
            bridgeZipPath.delete();
            listener.getLogger().println("Synopsys bridge unzipped successfully");
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed");
            e.printStackTrace();
        }
    }

    private boolean isValidVersion(String bridgeVersion) {
        return bridgeVersion != null && bridgeVersion.length() > 0;
    }

    private boolean isValidBridgeDownloadUrl(String bridgeDownloadUrl) {
        return bridgeDownloadUrl != null && bridgeDownloadUrl.length() > 0 &&
            bridgeDownloadUrl.matches(".*synopsys-bridge-([0-9.]*).*");
    }

    private boolean checkIfBridgeUrlExists(String bridgeUrl) {
        try {
            URL url = new URL(bridgeUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

}
