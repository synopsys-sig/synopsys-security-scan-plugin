package com.synopsys.integration.jenkins.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Automation {
    @JsonProperty("prComment")
    private boolean prComment;

    public boolean getPrComment() {
        return prComment;
    }

    public void setPrComment(boolean prComment) {
        this.prComment = prComment;
    }
}
