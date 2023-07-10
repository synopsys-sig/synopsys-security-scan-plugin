package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Scan {
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
