package io.jenkins.plugins.synopsys.security.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.synopsys.security.scan.input.bitbucket.Bitbucket;
import io.jenkins.plugins.synopsys.security.scan.input.blackduck.BlackDuck;
import io.jenkins.plugins.synopsys.security.scan.input.coverity.Coverity;
import io.jenkins.plugins.synopsys.security.scan.input.polaris.Polaris;

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
