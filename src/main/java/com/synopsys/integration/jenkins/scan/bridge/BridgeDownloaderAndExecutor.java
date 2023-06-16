package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.FilePath;
import hudson.model.TaskListener;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author akib @Date 6/15/23
 */
public class BridgeDownloaderAndExecutor {

    public void downloadAndUnzipSynopsysBridge(String bridgeVersion, String bridgeDownloadUrl, TaskListener listener) throws IOException, InterruptedException {
        String tempDir = createTempDir();
        String bridgeZipFileName = "bridge.zip";
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
                FilePath destinationFilePath = new FilePath(new File(tempDir));
                destinationFilePath.mkdirs();
                FilePath bridgeZip = destinationFilePath.child(bridgeZipFileName);

                listener.getLogger().println("Downloading synopsys bridge from: " + bridgeUrl);
                bridgeZip.copyFrom(new URL(bridgeUrl));
                listener.getLogger().println("Synopsys bridge downloaded successfully to: " + bridgeZip.getRemote());

                listener.getLogger().println("Unzipping synopsys bridge from: " + destinationFilePath);
                bridgeZip.unzip(destinationFilePath);
                // Delete the zip file
                bridgeZip.delete();
                listener.getLogger().println("Synopsys bridge unzipped successfully");
            } catch (Exception e) {
                e.printStackTrace();
                cleanupTempDir(tempDir);
            }
        } else {
            listener.getLogger().println("Invalid synopsys bridge download url: " + bridgeUrl);
            cleanupTempDir(tempDir);
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

    private String createTempDir() throws IOException {
        Path tempDir = Files.createTempDirectory(ApplicationConstants.APPLICATION_NAME);
        return tempDir.toString();
    }

    private void cleanupTempDir(String tempDir) throws IOException, InterruptedException {
        FilePath dirPath = new FilePath(new File(tempDir));
        dirPath.delete();
    }

}
