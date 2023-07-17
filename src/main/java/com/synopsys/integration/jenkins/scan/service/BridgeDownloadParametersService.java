package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadParameters;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BridgeDownloadParametersService {

    public boolean performBridgeDownloadParameterValidation(BridgeDownloadParameters bridgeDownloadParameters) {
        boolean validUrl = isValidUrl(bridgeDownloadParameters.getBridgeDownloadUrl());
        boolean validVersion = isValidVersion(bridgeDownloadParameters.getBridgeDownloadVersion());
        boolean validInstallationPath = isValidInstallationPath(bridgeDownloadParameters.getBridgeInstallationPath());

        return validUrl && validVersion && validInstallationPath;

    }

    public boolean isValidUrl(String url) {
        if (url.isEmpty()) {
            return false;
        }

        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidVersion(String version) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(version);
        return matcher.matches() || version.equals(ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION);
    }

    public boolean isValidInstallationPath(String installationPath) {
        Path path = Paths.get(installationPath);
        Path parentPath = path.getParent();
        if (parentPath != null && Files.exists(parentPath) && Files.isWritable(parentPath)) {
            return true;
        } else {
            return false;
        }
    }

    public BridgeDownloadParameters getBridgeDownloadParams(Map<String, Object> scanParameters, BridgeDownloadParameters bridgeDownloadParameters) {

        if (scanParameters.containsKey(ApplicationConstants.BRIDGE_DOWNLOAD_URL)) {
            bridgeDownloadParameters.setBridgeDownloadUrl(scanParameters.get(ApplicationConstants.BRIDGE_DOWNLOAD_URL).toString());
        }

        if (scanParameters.containsKey(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION)) {
            bridgeDownloadParameters.setBridgeDownloadVersion(scanParameters.get(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION).toString());
        }

        if (scanParameters.containsKey(ApplicationConstants.BRIDGE_INSTALLATION_PATH)) {
            bridgeDownloadParameters.setBridgeInstallationPath(scanParameters.get(ApplicationConstants.BRIDGE_INSTALLATION_PATH).toString());
        }

        return bridgeDownloadParameters;
    }
}
