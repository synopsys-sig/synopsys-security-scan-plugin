package com.synopsys.integration.jenkins.scan.input;


import com.fasterxml.jackson.databind.annotation.JsonAppend;

import java.util.ArrayList;
import java.util.List;

/**
 * @author akib @Date 6/20/23
 */
@JsonAppend
public class Blackduck {
    private String url;
    private String token;
    private String installDirectory;
    private boolean scanFull;
    private List<BlackduckScanFailureSeverities> scanFailureSeverities = new ArrayList<>();
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

    public boolean isScanFull() {
        return scanFull;
    }

    public void setScanFull(boolean scanFull) {
        this.scanFull = scanFull;
    }

    public List<BlackduckScanFailureSeverities> getScanFailureSeverities() {
        return scanFailureSeverities;
    }

    public void setScanFailureSeverities(
        List<BlackduckScanFailureSeverities> scanFailureSeverities) {
        this.scanFailureSeverities = scanFailureSeverities;
    }

    public Automation getAutomation() {
        return automation;
    }

    public void setAutomation(Automation automation) {
        this.automation = automation;
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
