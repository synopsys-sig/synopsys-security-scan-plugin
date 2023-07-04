package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Platform;
import hudson.model.TaskListener;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class BridgeDownloaderAndExecutor {
    private final TaskListener listener;
    private final EnvVars envVars;

    public BridgeDownloaderAndExecutor(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public FilePath downloadSynopsysBridge(String bridgeVersion, String bridgeDownloadUrl) {
        String bridgeUrl;
        
        if (isValidVersion(bridgeVersion)) {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL
                .concat(bridgeVersion).concat("/")
                .concat(ApplicationConstants.getSynopsysBridgeZipFileName(getPlatform(), bridgeVersion));
        }
        else if (isValidBridgeDownloadUrl(bridgeDownloadUrl)) {
            bridgeUrl = bridgeDownloadUrl;
        }
        else {
            bridgeUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL
                .concat(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION).concat("/")
                .concat(ApplicationConstants.getSynopsysBridgeZipFileName(ApplicationConstants.PLATFORM_LINUX));
        }

        FilePath tempFilePath = null;
        if (checkIfBridgeUrlExists(bridgeUrl)) {
            try {
                listener.getLogger().println("Downloading synopsys bridge from: " + bridgeUrl);
                File tempFile = File.createTempFile("bridge", ".zip");

                tempFilePath = new FilePath(tempFile);
                tempFilePath.copyFrom(new URL(bridgeUrl));

            } catch (Exception e) {
                listener.getLogger().println("Synopsys bridge download failed");
                e.printStackTrace();
            }
        }
        else {
            listener.getLogger().println("Invalid synopsys bridge download url: " + bridgeUrl);
        }
        return tempFilePath;
    }

    public void unzipSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            bridgeZipPath.unzip(bridgeInstallationPath);

            //Have to call delete *explicitly* as JVM doesn't erase this file.
            bridgeZipPath.delete();

            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s \n", bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed");
            e.printStackTrace();
        }
    }

    private String getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return ApplicationConstants.PLATFORM_WINDOWS;
        } else if (os.contains("mac")) {
            return ApplicationConstants.PLATFORM_MAC;
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
