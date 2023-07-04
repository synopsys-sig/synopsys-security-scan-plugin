package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.Utility;

public class BridgeDownloadParameters {
    private String bridgeDownloadUrl;
    private String bridgeDownloadVersion;
    private String bridgeInstallationPath;

    public BridgeDownloadParameters() {
        this.bridgeDownloadUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL;
        this.bridgeDownloadVersion = ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION;
        this.bridgeInstallationPath = Utility.defaultBridgeInstallationPath();
    }

    public String getBridgeDownloadUrl() {
        return bridgeDownloadUrl;
    }

    public void setBridgeDownloadUrl(String bridgeDownloadUrl) {
        this.bridgeDownloadUrl = bridgeDownloadUrl;
    }

    public String getBridgeDownloadVersion() {
        return bridgeDownloadVersion;
    }

    public void setBridgeDownloadVersion(String bridgeDownloadVersion) {
        this.bridgeDownloadVersion = bridgeDownloadVersion;
    }

    public String getBridgeInstallationPath() {
        return bridgeInstallationPath;
    }

    public void setBridgeInstallationPath(String bridgeInstallationPath) {
        this.bridgeInstallationPath = bridgeInstallationPath;
    }
}
