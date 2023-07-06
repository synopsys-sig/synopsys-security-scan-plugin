package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.integration.jenkins.scan.input.bitbucket.BitBucket;

public class BridgeInput {

    @JsonProperty("blackduck")
    private BlackDuck blackDuck;

    @JsonProperty("bitbucket")
    private BitBucket bitBucket;

    public BlackDuck getBlackDuck() {
        return blackDuck;
    }

    public void setBlackDuck(BlackDuck blackDuck) {
        this.blackDuck = blackDuck;
    }

    public BitBucket getBitBucket() {
        return bitBucket;
    }

    public void setBitBucket(BitBucket bitBucket) {
        this.bitBucket = bitBucket;
    }


}
