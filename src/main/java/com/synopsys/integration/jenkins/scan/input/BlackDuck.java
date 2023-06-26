package com.synopsys.integration.jenkins.scan.input;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BlackDuck {
    @JsonProperty("url")
    private String url;
    @JsonProperty("token")
    private String token;
    @JsonProperty("install.directory")
    private String installDirectory;
    @JsonProperty("scan")
    private Scan scan;
    @JsonProperty("automation")
    private Automation automation;

    public BlackDuck() {
        scan = new Scan();
        automation = new Automation();
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

    public String getInstallDirectory() {
        return installDirectory;
    }

    public void setInstallDirectory(String installDirectory) {
        this.installDirectory = installDirectory;
    }

    public Scan getScan() {
        return scan;
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

    public static class Scan {
        @JsonProperty("full")
        private Boolean full;
        @JsonProperty("failure.severities")
        private List<String> failureSeverities;

        public Boolean getFull() {
            return full;
        }

        public void setFull(Boolean full) {
            this.full = full;
        }

        public List<String> getFailureSeverities() {
            return failureSeverities;
        }

        public void setFailureSeverities(List<String> failureSeverities) {
            this.failureSeverities = failureSeverities;
        }
    }

}
