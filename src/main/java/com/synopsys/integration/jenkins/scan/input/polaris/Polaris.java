/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.input.polaris;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Polaris {
    @JsonProperty("accesstoken")
    private String accessToken;

    @JsonProperty("application")
    private ApplicationName applicationName;

    @JsonProperty("project")
    private ProjectName projectName;

    @JsonProperty("assessment")
    private AssessmentTypes assessmentTypes;

    @JsonProperty("serverUrl")
    private String serverUrl;

    @JsonProperty("triage")
    private String triage;

    @JsonProperty("branch")
    private Branch branch;

    public Polaris() {
        applicationName = new ApplicationName();
        projectName = new ProjectName();
        assessmentTypes = new AssessmentTypes();
        branch = new Branch();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ApplicationName getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(ApplicationName applicationName) {
        this.applicationName = applicationName;
    }

    public ProjectName getProjectName() {
        return projectName;
    }

    public void setProjectName(ProjectName projectName) {
        this.projectName = projectName;
    }

    public AssessmentTypes getAssessmentTypes() {
        return assessmentTypes;
    }

    public void setAssessmentTypes(AssessmentTypes assessmentTypes) {
        this.assessmentTypes = assessmentTypes;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getTriage() {
        return triage;
    }

    public void setTriage(String triage) {
        this.triage = triage;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
