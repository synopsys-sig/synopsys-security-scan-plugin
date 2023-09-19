/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.input.blackduck;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlackDuck {
    @JsonProperty("url")
    private String url;
    @JsonProperty("token")
    private String token;
    @JsonProperty("install")
    private Install install;
    @JsonProperty("scan")
    private Scan scan;
    @JsonProperty("automation")
    private Automation automation;
    @JsonProperty("download")
    private Download download;

    public BlackDuck() {
        scan = new Scan();
        automation = new Automation();
        install = new Install();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setInstall(Install install) {
        this.install = install;
    }

    public Scan getScan() {
        return scan;
    }

    public Install getInstall() {
        return install;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public Automation getAutomation() {
        return automation;
    }

    public void setAutomation(Automation automation) {
        this.automation = automation;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(final Download download) {
        this.download = download;
    }
}
