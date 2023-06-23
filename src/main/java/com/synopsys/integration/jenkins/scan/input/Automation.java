package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Automation {

    @JsonProperty("fixpr")
    private boolean isFixPr;
    @JsonProperty("prcomment")
    private boolean isPrComment;

    public boolean isFixPr() {
        return isFixPr;
    }

    public void setFixPr(boolean fixPr) {
        isFixPr = fixPr;
    }

    public boolean isPrComment() {
        return isPrComment;
    }

    public void setPrComment(boolean prComment) {
        isPrComment = prComment;
    }
}