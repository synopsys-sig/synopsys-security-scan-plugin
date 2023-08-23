package com.synopsys.integration.jenkins.scan.input.polaris;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationName {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
