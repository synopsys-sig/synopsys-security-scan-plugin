package io.jenkins.plugins.synopsys.security.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repository {
    @JsonProperty("pull")
    private Pull pull;

    @JsonProperty("name")
    private String name;

    public Repository() {
        pull = new Pull();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pull getPull() {
        return pull;
    }

    public void setPull(Pull pull) {
        this.pull = pull;
    }
}
