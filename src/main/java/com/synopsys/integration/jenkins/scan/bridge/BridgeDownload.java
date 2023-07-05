package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class BridgeDownload {
    private final TaskListener listener;
    private final EnvVars envVars;

    public BridgeDownload(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public FilePath downloadSynopsysBridge(String bridgeVersion, String bridgeDownloadUrl) {
        String bridgeUrl = null;

        if (bridgeVersion.equals(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION)) {
            bridgeUrl =bridgeDownloadUrl.concat(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION).concat("/")
                    .concat(ApplicationConstants.getSynopsysBridgeZipFileName(ApplicationConstants.PLATFORM_LINUX));

        }
        else if (isValidVersion(bridgeVersion)) {
            bridgeUrl = bridgeDownloadUrl.concat(bridgeVersion).concat("/")
                    .concat(ApplicationConstants.getSynopsysBridgeZipFileName(getPlatform(), bridgeVersion));
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
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(bridgeVersion);
        return matcher.matches();
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
