package com.synopsys.integration.jenkins.scan.input;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Automation {

    @JsonProperty("fixpr")
    private Boolean fixpr;
    @JsonProperty("prcomment")
    private Boolean prcomment;

    public Boolean getFixpr() {
        return fixpr;
    }

    public void setFixpr(Boolean fixpr) {
        this.fixpr = fixpr;
    }

    public Boolean getPrcomment() {
        return prcomment;
    }

    public void setPrcomment(Boolean prcomment) {
        this.prcomment = prcomment;
    }
}
