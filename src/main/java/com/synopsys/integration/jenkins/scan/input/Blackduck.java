package com.synopsys.integration.jenkins.scan.input;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author akib @Date 6/20/23
 */
public class Blackduck {
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

    private class Scan {
        @JsonProperty("full")
        private boolean isFullScan = false;
        @JsonProperty("failure.severities")
        private List<BlackduckScanFailureSeverities> failureSeverities;

        public boolean isFullScan() {
            return isFullScan;
        }

        public void setFullScan(boolean fullScan) {
            isFullScan = fullScan;
        }

        public List<BlackduckScanFailureSeverities> getFailureSeverities() {
            return failureSeverities;
        }

        public void setFailureSeverities(List<BlackduckScanFailureSeverities> failureSeverities) {
            this.failureSeverities = failureSeverities;
        }
    }

    public enum BlackduckScanFailureSeverities {
        ALL("ALL"),
        NONE("NONE"),
        BLOCKER("BLOCKER"),
        CRITICAL("CRITICAL"),
        MAJOR("MAJOR"),
        MINOR("MINOR"),
        OK("OK"),
        TRIVIAL("TRIVIAL"),
        UNSPECIFIED("UNSPECIFIED");

        private final String value;

        BlackduckScanFailureSeverities(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
