package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BridgeDownloadManager {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeDownloadManager(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
    }

    public boolean isSynopsysBridgeDownloadRequired(BridgeDownloadParameters bridgeDownloadParameters) {
        String bridgeDownloadUrl = bridgeDownloadParameters.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParameters.getBridgeInstallationPath();

        if (!checkIfBridgeInstalled(bridgeInstallationPath)) {
            return true;
        }

        String installedBridgeVersionFilePath;
        String os = Utility.getAgentOs(workspace, listener);
        if (os.contains("win")) {
            installedBridgeVersionFilePath = String.join("\\", bridgeInstallationPath, ApplicationConstants.VERSION_FILE);
        } else {
            installedBridgeVersionFilePath = String.join("/", bridgeInstallationPath, ApplicationConstants.VERSION_FILE);
        }

        String installedBridgeVersion = getBridgeVersionFromVersionFile(installedBridgeVersionFilePath);
        String latestBridgeVersion = getLatestBridgeVersionFromArtifactory(bridgeDownloadUrl);

        return !latestBridgeVersion.equals(installedBridgeVersion);
    }

    public boolean checkIfBridgeInstalled(String synopsysBridgeInstallationPath) {
        try {
            FilePath installationDirectory = new FilePath(workspace.getChannel(), synopsysBridgeInstallationPath);

            if (installationDirectory.exists() && installationDirectory.isDirectory()) {
                FilePath extensionsDir = installationDirectory.child(ApplicationConstants.EXTENSIONS_DIRECTORY);
                FilePath bridgeBinaryFile = installationDirectory.child(ApplicationConstants.BRIDGE_BINARY);
                FilePath bridgeBinaryFileWindows = installationDirectory.child(ApplicationConstants.BRIDGE_BINARY_WINDOWS);
                FilePath versionFile = installationDirectory.child(ApplicationConstants.VERSION_FILE);

                return extensionsDir.isDirectory() && (bridgeBinaryFile.exists() || bridgeBinaryFileWindows.exists()) && versionFile.exists();
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while checking if the bridge is installed: " + e.getMessage());
        }
        return false;
    }

    public String getBridgeVersionFromVersionFile(String versionFilePath) {
        try {
            FilePath file = new FilePath(workspace.getChannel(), versionFilePath);
            if (file.exists()) {
                String versionsFileContent = file.readToString();
                Matcher matcher = Pattern.compile("Synopsys Bridge Package: (\\d+\\.\\d+\\.\\d+)")
                        .matcher(versionsFileContent);

                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while extracting bridge-version from the 'versions.txt': " + e.getMessage());
        }
        return null;
    }

    public String getLatestBridgeVersionFromArtifactory(String bridgeDownloadUrl) {
        String extractedVersionNumber = extractVersionFromUrl(bridgeDownloadUrl);
        if(extractedVersionNumber.equals(ApplicationConstants.NOT_AVAILABLE)) {
            String directoryUrl = getDirectoryUrl(bridgeDownloadUrl);
            if(versionFileAvailable(directoryUrl)) {
                String versionFilePath = downloadVersionFile(directoryUrl);
                String latestVersion = getBridgeVersionFromVersionFile(versionFilePath);

                Utility.removeFile(versionFilePath, workspace, listener);

                return latestVersion;
            }
            else {
                return ApplicationConstants.NOT_AVAILABLE;
            }
        }
        else {
            return extractedVersionNumber;
        }
    }

    public String downloadVersionFile(String directoryUrl) {
        String versionFileUrl = String.join("/", directoryUrl, ApplicationConstants.VERSION_FILE);
        String tempVersionFilePath = null;

        try {
            FilePath tempFilePath = workspace.createTempFile("versions", ".txt");
            URL url = new URL(versionFileUrl);

            tempFilePath.copyFrom(url);
            tempVersionFilePath = tempFilePath.getRemote();
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while downloading 'versions.txt': " + e.getMessage());
        }
        return tempVersionFilePath;
    }

    public boolean versionFileAvailable(String directoryUrl) {
        try {
            URL url = new URL(String.join("/",directoryUrl,ApplicationConstants.VERSION_FILE));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300);
        } catch (IOException e) {
            listener.getLogger().println("An exception occurred while checking if 'versions.txt' is available or not in the URL: " + e.getMessage());
            return false;
        }
    }

    public String getDirectoryUrl(String downloadUrl) {
        String directoryUrl = null;
        try {
            URI uri = new URI(downloadUrl);
            String path = uri.getPath();

            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            String directoryPath = path.substring(0, path.lastIndexOf('/'));
            directoryUrl = uri.getScheme().concat("://").concat(uri.getHost()).concat(directoryPath);
        } catch (URISyntaxException e) {
            listener.getLogger().println("An exception occurred while getting directoryUrl from downloadUrl: " + e.getMessage());
        }
        return directoryUrl;
    }

    public String extractVersionFromUrl(String url) {
        String regex = "/(\\d+\\.\\d+\\.\\d+)/";
        Pattern pattern = Pattern.compile(regex);
        String version;

        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            version = matcher.group(1);
        } else {
            version = ApplicationConstants.NOT_AVAILABLE;
        }

        return version;
    }
    
}