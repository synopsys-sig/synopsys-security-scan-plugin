package com.synopsys.integration.jenkins.scan.bridge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BridgeDownloadManager {
    private final String bridgeBinary = "synopsys-bridge";
    private final String extensionsDirectory = "extensions";
    private final String versionFile = "versions.txt";

    public boolean isSynopsysBridgeDownloadRequired(BridgeDownloadParameters bridgeDownloadParameters) {
        String bridgeDownloadUrl = bridgeDownloadParameters.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParameters.getBridgeInstallationPath();

        if (!checkIfBridgeInstalled(bridgeInstallationPath)) {
            return true;
        }

        String installedBridgeVersionFilePath = null;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            installedBridgeVersionFilePath = String.join("\\", bridgeInstallationPath, versionFile);
        } else {
            installedBridgeVersionFilePath = String.join("/", bridgeInstallationPath, versionFile);
        }

        String installedBridgeVersion = getBridgeVersionFromVersionFile(installedBridgeVersionFilePath);
        String latestBridgeVersion = getLatestBridgeVersionFromArtifactory(bridgeDownloadUrl);

        return !latestBridgeVersion.equals(installedBridgeVersion);
    }

    public boolean checkIfBridgeInstalled(String synopsysBridgeInstallationPath) {
        File installationDirectory = new File(synopsysBridgeInstallationPath);

        if (installationDirectory.exists() && installationDirectory.isDirectory()) {
            return new File(installationDirectory, extensionsDirectory).isDirectory()
                    && new File(installationDirectory, bridgeBinary).isFile()
                    && new File(installationDirectory, versionFile).isFile();
        }
        return false;
    }

    public String getBridgeVersionFromVersionFile(String versionFilePath) {
        File installationDirectory = new File(versionFilePath);

        try {
            String versionsFileContent = Files.readString(installationDirectory.toPath());
            Matcher matcher = Pattern.compile("Synopsys Bridge Package: (\\d+\\.\\d+\\.\\d+)")
                    .matcher(versionsFileContent);

            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLatestBridgeVersionFromArtifactory(String bridgeDownloadUrl) {
        String extractedVersionNumber = extractVersionFromUrl(bridgeDownloadUrl);
        if(extractedVersionNumber.equals("NA")) {
            String directoryUrl = getDirectoryUrl(bridgeDownloadUrl);
            if(versionFileAvailable(directoryUrl)) {
                String versionFilePath = downloadVersionFile(directoryUrl);
                String latestVersion = getBridgeVersionFromVersionFile(versionFilePath);

                return latestVersion;
            }
            else {
                return "NA";
            }
        }
        else {
            return extractedVersionNumber;
        }
    }

    public String downloadVersionFile(String directoryUrl) {
        String versionFileUrl = String.join("/", directoryUrl, versionFile);
        String tempVersionFilePath = null;

        try {
            URL url = new URL(versionFileUrl);
            InputStream inputStream = url.openStream();
            Path tempFilePath = Files.createTempFile("versions", ".txt");

            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            tempVersionFilePath = tempFilePath.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempVersionFilePath;
    }

    public boolean versionFileAvailable(String directoryUrl) {
        try {
            URL url = new URL(String.join("/",directoryUrl,versionFile));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            return (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300);
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return directoryUrl;
    }

    public String extractVersionFromUrl(String url) {
        String regex = "/(\\d+\\.\\d+\\.\\d+)/";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(url);

        String version;
        if (matcher.find()) {
            version = matcher.group(1);
        } else {
            version = "NA";
        }

        return version;
    }
}