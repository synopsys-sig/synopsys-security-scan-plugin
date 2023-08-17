package com.synopsys.integration.jenkins.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Stream {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
