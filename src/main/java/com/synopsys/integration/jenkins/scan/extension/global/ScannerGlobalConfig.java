/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.extension.global;

import hudson.Extension;
import java.io.Serializable;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class ScannerGlobalConfig extends GlobalConfiguration implements Serializable {
    private static final long serialVersionUID = -3129542889827231427L;
    
    private String blackDuckUrl;
    private String blackDuckApiToken;
    private String blackDuckInstallationPath;
    private String coverityConnectUrl;
    private String coverityConnectUserName;
    private String coverityConnectUserPassword;
    private String coverityInstallationPath;
    private String synopsysBridgeDownloadUrlForMac;
    private String synopsysBridgeDownloadUrlForWindows;
    private String synopsysBridgeDownloadUrlForLinux;
    private String synopsysBridgeVersion;
    private String synopsysBridgeInstallationPath;
    private String bitbucketToken;
    private String polarisServerUrl;
    private String polarisAccessToken;

    @DataBoundConstructor
    public ScannerGlobalConfig() {
        load();
    }

    @DataBoundSetter
    public void setBlackDuckUrl(String blackDuckUrl) {
        this.blackDuckUrl = blackDuckUrl;
        save();
    }

    @DataBoundSetter
    public void setBlackDuckApiToken(String blackDuckApiToken) {
        this.blackDuckApiToken = blackDuckApiToken;
        save();
    }

    @DataBoundSetter
    public void setBlackDuckInstallationPath(String blackDuckInstallationPath) {
        this.blackDuckInstallationPath = blackDuckInstallationPath;
        save();
    }

    @DataBoundSetter
    public void setCoverityConnectUrl(String coverityConnectUrl) {
        this.coverityConnectUrl = coverityConnectUrl;
        save();
    }

    @DataBoundSetter
    public void setCoverityConnectUserName(String coverityConnectUserName) {
        this.coverityConnectUserName = coverityConnectUserName;
        save();
    }

    @DataBoundSetter
    public void setCoverityConnectUserPassword(String coverityConnectUserPassword) {
        this.coverityConnectUserPassword = coverityConnectUserPassword;
        save();
    }

    @DataBoundSetter
    public void setCoverityInstallationPath(String coverityInstallationPath) {
        this.coverityInstallationPath = coverityInstallationPath;
        save();
    }

    @DataBoundSetter
    public void setBitbucketToken(String bitbucketToken) {
        this.bitbucketToken = bitbucketToken;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeDownloadUrlForMac(String synopsysBridgeDownloadUrlForMac) {
        this.synopsysBridgeDownloadUrlForMac = synopsysBridgeDownloadUrlForMac;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeDownloadUrlForWindows(String synopsysBridgeDownloadUrlForWindows) {
        this.synopsysBridgeDownloadUrlForWindows = synopsysBridgeDownloadUrlForWindows;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeDownloadUrlForLinux(String synopsysBridgeDownloadUrlForLinux) {
        this.synopsysBridgeDownloadUrlForLinux = synopsysBridgeDownloadUrlForLinux;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeVersion(String synopsysBridgeVersion) {
        this.synopsysBridgeVersion = synopsysBridgeVersion;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeInstallationPath(String synopsysBridgeInstallationPath) {
        this.synopsysBridgeInstallationPath = synopsysBridgeInstallationPath;
        save();
    }

    @DataBoundSetter
    public void setPolarisServerUrl(String polarisServerUrl) {
        this.polarisServerUrl = polarisServerUrl;
        save();
    }

    @DataBoundSetter
    public void setPolarisAccessToken(String polarisAccessToken) {
        this.polarisAccessToken = polarisAccessToken;
        save();
    }

    public String getBlackDuckUrl() {
        return blackDuckUrl;
    }

    public String getBlackDuckApiToken() {
        return blackDuckApiToken;
    }
    public String getBlackDuckInstallationPath() {
        return blackDuckInstallationPath;
    }

    public String getCoverityConnectUrl() {
        return coverityConnectUrl;
    }

    public String getCoverityConnectUserName() {
        return coverityConnectUserName;
    }

    public String getCoverityConnectUserPassword() {
        return coverityConnectUserPassword;
    }

    public String getCoverityInstallationPath() {
        return coverityInstallationPath;
    }

    public String getSynopsysBridgeDownloadUrlForMac() {
        return synopsysBridgeDownloadUrlForMac;
    }

    public String getSynopsysBridgeDownloadUrlForWindows() {
        return synopsysBridgeDownloadUrlForWindows;
    }

    public String getSynopsysBridgeDownloadUrlForLinux() {
        return synopsysBridgeDownloadUrlForLinux;
    }

    public String getSynopsysBridgeVersion() {
        return synopsysBridgeVersion;
    }

    public String getSynopsysBridgeInstallationPath() {
        return synopsysBridgeInstallationPath;
    }

    public String getBitbucketToken() {
        return bitbucketToken;
    }

    public String getPolarisServerUrl() {
        return polarisServerUrl;
    }

    public String getPolarisAccessToken() {
        return polarisAccessToken;
    }
}
