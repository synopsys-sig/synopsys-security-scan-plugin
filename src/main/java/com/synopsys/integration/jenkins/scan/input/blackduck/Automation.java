package com.synopsys.integration.jenkins.scan.input.blackduck;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Automation {
    @JsonProperty("fixpr")
    private boolean fixpr;
    @JsonProperty("prComment")
    private boolean prComment;

    public boolean getFixpr() {
        return fixpr;
    }

    public void setFixpr(boolean fixpr) {
        this.fixpr = fixpr;
    }

    public boolean getPrComment() {
        return prComment;
    }

    public void setPrComment(boolean prComment) {
        this.prComment = prComment;
    }
}

