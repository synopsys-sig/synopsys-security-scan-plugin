package com.synopsys.integration.jenkins.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Api {
    @JsonProperty("url")
    private String url;

    @JsonProperty("token")
    private String token;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
