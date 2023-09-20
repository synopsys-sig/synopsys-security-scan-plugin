/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.coverity.Coverity;
import com.synopsys.integration.jenkins.scan.input.polaris.Polaris;

public class BridgeInput {
    @JsonProperty("blackduck")
    private BlackDuck blackDuck;

    @JsonProperty("coverity")
    private Coverity coverity;

    @JsonProperty("polaris")
    private Polaris polaris;

    @JsonProperty("bitbucket")
    private Bitbucket bitbucket;

    @JsonProperty("network")
    private NetworkAirGap networkAirGap;

    public BlackDuck getBlackDuck() {
        return blackDuck;
    }

    public void setBlackDuck(BlackDuck blackDuck) {
        this.blackDuck = blackDuck;
    }

    public Coverity getCoverity() {
        return coverity;
    }

    public void setCoverity(Coverity coverity) {
        this.coverity = coverity;
    }

    public Polaris getPolaris() {
        return polaris;
    }

    public void setPolaris(Polaris polaris) {
        this.polaris = polaris;
    }

    public Bitbucket getBitbucket() {
        return bitbucket;
    }

    public void setBitbucket(Bitbucket bitbucket) {
        this.bitbucket = bitbucket;
    }

    public NetworkAirGap getNetworkAirGap() {
        return networkAirGap;
    }

    public void setNetworkAirGap(final NetworkAirGap networkAirGap) {
        this.networkAirGap = networkAirGap;
    }
}
