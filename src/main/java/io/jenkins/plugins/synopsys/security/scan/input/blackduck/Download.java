package io.jenkins.plugins.synopsys.security.scan.input.blackduck;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Download {
    @JsonProperty("url")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
