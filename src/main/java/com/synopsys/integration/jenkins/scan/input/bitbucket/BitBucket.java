package com.synopsys.integration.jenkins.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BitBucket {
    @JsonProperty("api")
    private Api api;

    @JsonProperty("project")
    private Project project;
//    @JsonProperty("project.repository.pull.number")
//    private String projectRepositoryPullNumber;
//    @JsonProperty("project.repository.name")
//    private String projectRepositoryName;
//    @JsonProperty("project.key")
//    private String projectKey;

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
