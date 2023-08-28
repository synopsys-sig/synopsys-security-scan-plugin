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

}
