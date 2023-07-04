package com.synopsys.integration.jenkins.scan.bridge;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BridgeDownloadManager {
    private final String bridgeBinary = "synopsys-bridge";
    private final String extensionsDirectory = "extensions";
    private final String versionFile = "versions.txt";

    //TODO may be to service class?
    public BridgeDownloadParameters getBridgeDownloadParams(Map<String, Object> scanParams, BridgeDownloadParameters bridgeDownloadParameters) {

        //TODO: CONSTANTS
        if (scanParams.containsKey("bridge_download_url")) {
            bridgeDownloadParameters.setBridgeDownloadUrl(scanParams.get("bridge_download_url").toString());
        }

        if (scanParams.containsKey("bridge_download_version")) {
            bridgeDownloadParameters.setBridgeDownloadVersion(scanParams.get("bridge_download_version").toString());
        }

        if (scanParams.containsKey("synopsys_bridge_path")) {
            bridgeDownloadParameters.setBridgeInstallationPath(scanParams.get("synopsys_bridge_path").toString());
        }

        return bridgeDownloadParameters;
    }

    public boolean isSynopsysBridgeDownloadRequired(BridgeDownloadParameters bridgeDownloadParameters) {
        String bridgeDownloadUrl = bridgeDownloadParameters.getBridgeDownloadUrl();
        String bridgeInstallationPath = bridgeDownloadParameters.getBridgeInstallationPath();
        String bridgeDownloadVersion = bridgeDownloadParameters.getBridgeDownloadVersion();

        if (!checkIfBridgeInstalled(bridgeInstallationPath)) {
            return true;
        }

        String installedBridgeVersion = getInstalledBridgeVersion(bridgeInstallationPath);
        List<String> availableVersions = getAllAvailableBridgeVersionsFromArtifactory(bridgeDownloadUrl);
        String desiredBridgeVersion = bridgeDownloadVersion.equals(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION)
                ? getLatestVersion(availableVersions) : bridgeDownloadVersion;

        return !desiredBridgeVersion.equals(installedBridgeVersion);
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

    public String getInstalledBridgeVersion(String synopsysBridgeInstallationPath) {
        File installationDirectory = new File(synopsysBridgeInstallationPath);

        try {
            String versionsFileContent = Files.readString(installationDirectory.toPath().resolve(versionFile));
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

    public List<String> getAllAvailableBridgeVersionsFromArtifactory(String url) {
        List<String> availableVersions = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.getElementsByTag("a");

            for (Element element : elements) {
                String content = element.text().endsWith("/") ? element.text().substring(0, element.text().length() - 1) : element.text();
                String version = content.matches("^[0-9]+\\.[0-9]+\\.[0-9]+") ? content : null;
                if (version != null) {
                    availableVersions.add(version);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableVersions;

    }

    public String getLatestVersion(List<String> versions) {
        String latestVersion = "0.0.0";

        for (String version : versions) {
            if (version.compareTo(latestVersion) > 0) {
                latestVersion = version;
            }
        }

        return latestVersion;
    }
}