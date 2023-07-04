package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Platform;
import hudson.model.TaskListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class BridgeDownloaderAndExecutor {

    private final String bridgeZipFileName = "bridge.zip";
    private final TaskListener listener;
    private final EnvVars envVars;

    public BridgeDownloaderAndExecutor(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public FilePath downloadSynopsysBridge(FilePath downloadFilePath, String bridgeVersion, String bridgeDownloadUrl) {
        FilePath bridgeZipPath = downloadFilePath.child(bridgeZipFileName);
        String bridgeUrl;
        
        if (isValidVersion(bridgeVersion)) {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL
                .concat(bridgeVersion).concat("/")
                .concat(ApplicationConstants.getSynopsysBridgeZipFileName(getPlatform(), bridgeVersion));
        } else if (isValidBridgeDownloadUrl(bridgeDownloadUrl)){
            bridgeUrl = bridgeDownloadUrl;
        } else {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL
                .concat(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION).concat("/")
                .concat(ApplicationConstants.getSynopsysBridgeZipFileName(ApplicationConstants.PLATFORM_LINUX));
        }

        if (checkIfBridgeUrlExists(bridgeUrl)) {
            try {
                listener.getLogger().println("Downloading synopsys bridge from: ".concat(bridgeUrl));
                bridgeZipPath.copyFrom(new URL(bridgeUrl));
                //TODO:We can check and copy the zip to temp directory if it is downloading the zip to current directory.

                listener.getLogger().println("Synopsys bridge downloaded successfully to: ".concat(bridgeZipPath.getRemote()));
            } catch (Exception e) {
                listener.getLogger().println("Synopsys bridge download failed");
                e.printStackTrace();
            }
        } else {
            listener.getLogger().println("Invalid synopsys bridge download url: " + bridgeUrl);
        }
        return bridgeZipPath;
    }

    public void unzipSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeUnzipPath) {
        //TODO: We may define a specific accessible directory (For default location) and unzip the executables inside that.
        // We should also have facility to accept a parameter for specifying location
        try {
            listener.getLogger().println("Synopsys bridge zip path: ".concat(bridgeZipPath.getRemote()));
            bridgeZipPath.unzip(bridgeUnzipPath);

            //TODO: If we download the zip to temp directory, we may not need to call delete.

            // Delete the zip file
            bridgeZipPath.delete();
            listener.getLogger().println("Synopsys bridge unzipped successfully");
            listener.getLogger().println("Synopsys bridge unzip path: ".concat(bridgeUnzipPath.getRemote()));
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed");
            e.printStackTrace();
        }
    }

    private String getPlatform() {
        //TODO:  We can also check for Mac, as bridge have Mac compatible executables as well.

        if (Objects.equals(envVars.getPlatform(), Platform.WINDOWS)) {
            return ApplicationConstants.PLATFORM_WINDOWS;
        } else {
            return ApplicationConstants.PLATFORM_LINUX;
        }
    }

    public boolean isValidVersion(String bridgeVersion) {
        return bridgeVersion != null && bridgeVersion.length() > 0;
    }

    public boolean isValidBridgeDownloadUrl(String bridgeDownloadUrl) {
        return bridgeDownloadUrl != null && bridgeDownloadUrl.length() > 0 &&
            bridgeDownloadUrl.matches(".*synopsys-bridge-([0-9.]*).*");
    }

    public boolean checkIfBridgeUrlExists(String bridgeUrl) {
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
