package com.synopsys.integration.jenkins.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Policy {
    @JsonProperty("view")
    private String view;

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
