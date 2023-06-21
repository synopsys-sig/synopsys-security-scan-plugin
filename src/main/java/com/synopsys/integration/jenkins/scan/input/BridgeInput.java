package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author akib @Date 6/21/23
 */
public class BridgeInput {

    @JsonProperty("blackduck")
    private Blackduck blackduck;

    public Blackduck getBlackduck() {
        return blackduck;
    }

    public void setBlackduck(Blackduck blackduck) {
        this.blackduck = blackduck;
    }
}
