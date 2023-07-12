package com.synopsys.integration.jenkins.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Project {
    @JsonProperty("repository")
    private Repository repository;
    @JsonProperty("key")
    private String key;

    public Project() {
        repository = new Repository();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
