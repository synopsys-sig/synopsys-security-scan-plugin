package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.FilePath;
import hudson.model.TaskListener;

public class BridgeDownloadParameters {
    private String bridgeDownloadUrl;
    private String bridgeDownloadVersion;
    private String bridgeInstallationPath;
    private final FilePath workspace;
    private final TaskListener listener;

    public BridgeDownloadParameters(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
        this.bridgeDownloadUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL;
        this.bridgeDownloadVersion = ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION;
        this.bridgeInstallationPath = Utility.defaultBridgeInstallationPath(workspace, listener);
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
