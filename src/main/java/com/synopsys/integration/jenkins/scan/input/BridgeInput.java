package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;

public class BridgeInput {

    @JsonProperty("blackduck")
    private BlackDuck blackDuck;

    @JsonProperty("bitbucket")
    private Bitbucket bitbucket;

    public BlackDuck getBlackDuck() {
        return blackDuck;
    }

    public void setBlackDuck(BlackDuck blackDuck) {
        this.blackDuck = blackDuck;
    }

    public Bitbucket getBitbucket() {
        return bitbucket;
    }

    public void setBitbucket(Bitbucket bitbucket) {
        this.bitbucket = bitbucket;
    }


}
