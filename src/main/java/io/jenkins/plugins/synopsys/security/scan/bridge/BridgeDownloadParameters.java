/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;

public class BridgeDownloadParameters {
    private String bridgeDownloadUrl;
    private String bridgeDownloadVersion;
    private String bridgeInstallationPath;

    public BridgeDownloadParameters(FilePath workspace, TaskListener listener) {
        BridgeInstall bridgeInstall = new BridgeInstall(workspace, listener);
        this.bridgeDownloadUrl = ApplicationConstants.BRIDGE_ARTIFACTORY_URL;
        this.bridgeDownloadVersion = ApplicationConstants.SYNOPSYS_BRIDGE_LATEST_VERSION;
        this.bridgeInstallationPath = bridgeInstall.defaultBridgeInstallationPath(workspace, listener);
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
