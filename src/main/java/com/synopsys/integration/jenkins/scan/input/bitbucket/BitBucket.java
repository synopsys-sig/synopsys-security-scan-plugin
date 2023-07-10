package com.synopsys.integration.jenkins.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitBucket {
    @JsonProperty("api")
    private Api api;

    @JsonProperty("project")
    private Project project;

    public BitBucket() {
        api = new Api();
        project = new Project();
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
