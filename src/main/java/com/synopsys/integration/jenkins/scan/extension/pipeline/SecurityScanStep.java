/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.extension.pipeline;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.SecurityProduct;
import com.synopsys.integration.jenkins.scan.service.ScanCommandsFactory;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class SecurityScanStep extends Step implements Serializable {
    private static final long serialVersionUID = 6294070801130995534L;

    private String synopsys_security_product;

    private String bridge_blackduck_url;
    private String bridge_blackduck_api_token;
    private String bridge_blackduck_install_directory;
    private Boolean bridge_blackduck_scan_full;
    private String bridge_blackduck_scan_failure_severities;
//    private Boolean bridge_blackduck_automation_fixpr;
    private Boolean bridge_blackduck_automation_prcomment;

    private String bridge_coverity_connect_url;
    private String bridge_coverity_connect_user_name;
    private String bridge_coverity_connect_user_password;
    private String bridge_coverity_connect_project_name;
    private String bridge_coverity_connect_stream_name;
    private String bridge_coverity_connect_policy_view;
    private String bridge_coverity_install_directory;
    private Boolean bridge_coverity_automation_prcomment;
    private String bridge_coverity_version;
    private Boolean bridge_coverity_local;

    private String bridge_polaris_serverurl;
    private String bridge_polaris_accesstoken;
    private String bridge_polaris_application_name;
    private String bridge_polaris_project_name;
    private String bridge_polaris_assessment_types;
    private String bridge_polaris_triage;
//    private String bridge_polaris_branch_name;
//    private String bridge_polaris_branch_parent_name;


    private String bitbucket_token;

    private String bridge_download_url;
    private String bridge_download_version;
    private String synopsys_bridge_path;
    private Boolean bridge_include_diagnostics;

    @DataBoundConstructor
    public SecurityScanStep(String synopsys_security_product) {
        this.synopsys_security_product = synopsys_security_product;
    }

    @DataBoundSetter
    public void setBridge_blackduck_url(String bridge_blackduck_url) {
        this.bridge_blackduck_url = bridge_blackduck_url;
    }

    @DataBoundSetter
    public void setBridge_blackduck_api_token(String bridge_blackduck_api_token) {
        this.bridge_blackduck_api_token = bridge_blackduck_api_token;
    }

    @DataBoundSetter
    public void setBridge_blackduck_install_directory(String bridge_blackduck_install_directory) {
        this.bridge_blackduck_install_directory = bridge_blackduck_install_directory;
    }

    @DataBoundSetter
    public void setBridge_blackduck_scan_full(Boolean bridge_blackduck_scan_full) {
        this.bridge_blackduck_scan_full = bridge_blackduck_scan_full;
    }

    @DataBoundSetter
    public void setBridge_blackduck_scan_failure_severities(
        String bridge_blackduck_scan_failure_severities) {
        this.bridge_blackduck_scan_failure_severities = bridge_blackduck_scan_failure_severities;
    }

//    @DataBoundSetter
//    public void setBridge_blackduck_automation_fixpr(Boolean bridge_blackduck_automation_fixpr) {
//        this.bridge_blackduck_automation_fixpr = bridge_blackduck_automation_fixpr;
//    }

    @DataBoundSetter
    public void setBridge_blackduck_automation_prcomment(
            Boolean bridge_blackduck_automation_prcomment) {
        this.bridge_blackduck_automation_prcomment = bridge_blackduck_automation_prcomment;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_url(String bridge_coverity_connect_url) {
        this.bridge_coverity_connect_url = bridge_coverity_connect_url;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_user_name(String bridge_coverity_connect_user_name) {
        this.bridge_coverity_connect_user_name = bridge_coverity_connect_user_name;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_user_password(
        String bridge_coverity_connect_user_password) {
        this.bridge_coverity_connect_user_password = bridge_coverity_connect_user_password;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_project_name(
        String bridge_coverity_connect_project_name) {
        this.bridge_coverity_connect_project_name = bridge_coverity_connect_project_name;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_stream_name(String bridge_coverity_connect_stream_name) {
        this.bridge_coverity_connect_stream_name = bridge_coverity_connect_stream_name;
    }

    @DataBoundSetter
    public void setBridge_coverity_connect_policy_view(String bridge_coverity_connect_policy_view) {
        this.bridge_coverity_connect_policy_view = bridge_coverity_connect_policy_view;
    }

    @DataBoundSetter
    public void setBridge_coverity_install_directory(String bridge_coverity_install_directory) {
        this.bridge_coverity_install_directory = bridge_coverity_install_directory;
    }

    @DataBoundSetter
    public void setBridge_coverity_automation_prcomment(Boolean bridge_coverity_automation_prcomment) {
        this.bridge_coverity_automation_prcomment = bridge_coverity_automation_prcomment;
    }

    @DataBoundSetter
    public void setBridge_coverity_version(String bridge_coverity_version) {
        this.bridge_coverity_version = bridge_coverity_version;
    }

    @DataBoundSetter
    public void setBridge_coverity_local(Boolean bridge_coverity_local) {
        this.bridge_coverity_local = bridge_coverity_local;
    }

    @DataBoundSetter
    public void setBridge_polaris_serverurl(String bridge_polaris_serverurl) {
        this.bridge_polaris_serverurl = bridge_polaris_serverurl;
    }

    @DataBoundSetter
    public void setBridge_polaris_accesstoken(String bridge_polaris_accesstoken) {
        this.bridge_polaris_accesstoken = bridge_polaris_accesstoken;
    }

    @DataBoundSetter
    public void setBridge_polaris_application_name(String bridge_polaris_application_name) {
        this.bridge_polaris_application_name = bridge_polaris_application_name;
    }

    @DataBoundSetter
    public void setBridge_polaris_project_name(String bridge_polaris_project_name) {
        this.bridge_polaris_project_name = bridge_polaris_project_name;
    }

    @DataBoundSetter
    public void setBridge_polaris_assessment_types(String bridge_polaris_assessment_types) {
        this.bridge_polaris_assessment_types = bridge_polaris_assessment_types;
    }

    @DataBoundSetter
    public void setBridge_polaris_triage(String bridge_polaris_triage) {
        this.bridge_polaris_triage = bridge_polaris_triage;
    }

//    @DataBoundSetter
//    public void setBridge_polaris_branch_name(String bridge_polaris_branch_name) {
//        this.bridge_polaris_branch_name = bridge_polaris_branch_name;
//    }
//
//    @DataBoundSetter
//    public void setBridge_polaris_branch_parent_name(String bridge_polaris_branch_parent_name) {
//        this.bridge_polaris_branch_parent_name = bridge_polaris_branch_parent_name;
//    }

    @DataBoundSetter
    public void setBitbucket_token(String bitbucket_token) {
        this.bitbucket_token = bitbucket_token;
    }

    @DataBoundSetter
    public void setBridge_download_url(String bridge_download_url) {
        this.bridge_download_url = bridge_download_url;
    }

    @DataBoundSetter
    public void setBridge_download_version(String bridge_download_version) {
        this.bridge_download_version = bridge_download_version;
    }

    @DataBoundSetter
    public void setSynopsys_bridge_path(String synopsys_bridge_path) {
        this.synopsys_bridge_path = synopsys_bridge_path;
    }

    @DataBoundSetter
    public void setBridge_include_diagnostics(Boolean bridge_include_diagnostics) {
        this.bridge_include_diagnostics = bridge_include_diagnostics;
    }

    public String getBridge_polaris_serverurl() {
        return bridge_polaris_serverurl;
    }

    public String getBridge_polaris_accesstoken() {
        return bridge_polaris_accesstoken;
    }

    public String getBridge_polaris_application_name() {
        return bridge_polaris_application_name;
    }

    public String getBridge_polaris_project_name() {
        return bridge_polaris_project_name;
    }

    public String getBridge_polaris_assessment_types() {
        return bridge_polaris_assessment_types;
    }

    public String getBridge_polaris_triage() {
        return bridge_polaris_triage;
    }

//    public String getBridge_polaris_branch_name() {
//        return bridge_polaris_branch_name;
//    }
//
//    public String getBridge_polaris_branch_parent_name() {
//        return bridge_polaris_branch_parent_name;
//    }

    public String getSynopsys_security_product() {
        return synopsys_security_product;
    }

    public String getBridge_blackduck_url() {
        return bridge_blackduck_url;
    }

    public String getBridge_blackduck_api_token() {
        return bridge_blackduck_api_token;
    }

    public String getBridge_blackduck_install_directory() {
        return bridge_blackduck_install_directory;
    }

    public Boolean isBridge_blackduck_scan_full() {
        return bridge_blackduck_scan_full;
    }

    public String getBridge_blackduck_scan_failure_severities() {
        return bridge_blackduck_scan_failure_severities;
    }

//    public Boolean isBridge_blackduck_automation_fixpr() {
//        return bridge_blackduck_automation_fixpr;
//    }

    public Boolean isBridge_blackduck_automation_prcomment() {
        return bridge_blackduck_automation_prcomment;
    }

    public String getBridge_coverity_connect_url() {
        return bridge_coverity_connect_url;
    }

    public String getBridge_coverity_connect_user_name() {
        return bridge_coverity_connect_user_name;
    }

    public String getBridge_coverity_connect_user_password() {
        return bridge_coverity_connect_user_password;
    }

    public String getBridge_coverity_connect_project_name() {
        return bridge_coverity_connect_project_name;
    }

    public String getBridge_coverity_connect_stream_name() {
        return bridge_coverity_connect_stream_name;
    }

    public String getBridge_coverity_connect_policy_view() {
        return bridge_coverity_connect_policy_view;
    }

    public String getBridge_coverity_install_directory() {
        return bridge_coverity_install_directory;
    }

    public Boolean isBridge_coverity_automation_prcomment() {
        return bridge_coverity_automation_prcomment;
    }
    public String getBridge_coverity_version() {
        return bridge_coverity_version;
    }

    public Boolean isBridge_coverity_local() {
        return bridge_coverity_local;
    }

    public String getBitbucket_token() {
        return bitbucket_token;
    }

    public String getBridge_download_url() {
        return bridge_download_url;
    }

    public String getBridge_download_version() {
        return bridge_download_version;
    }

    public String getSynopsys_bridge_path() {
        return synopsys_bridge_path;
    }

    public Boolean isBridge_include_diagnostics() {
        return bridge_include_diagnostics;
    }

    private Map<String, Object> getParametersMap(FilePath workspace, TaskListener listener) {
        return ScanCommandsFactory.preparePipelineParametersMap(this, workspace, listener);
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(context);
    }

    @Extension(optional = true)
    public static final class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return new HashSet<>(Arrays.asList(Run.class, TaskListener.class, EnvVars.class, FilePath.class, Launcher.class, Node.class));
        }

        @Override
        public String getFunctionName() {
            return ApplicationConstants.PIPELINE_NAME;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return ApplicationConstants.DISPLAY_NAME;
        }

        public ListBoxModel doFillSynopsys_security_productItems() {
            ListBoxModel items = new ListBoxModel();
            Arrays.stream(SecurityProduct.values()).forEach(
                securityProduct -> items.add(String.valueOf(securityProduct)));
            return items;
        }
    }

    public class Execution extends SynchronousNonBlockingStepExecution<Integer> {
        private static final long serialVersionUID = -2514079516220990421L;
        private final transient Run<?, ?> run;
        private final transient TaskListener listener;
        private final transient EnvVars envVars;
        private final transient FilePath workspace;
        private final transient Launcher launcher;
        private final transient Node node;

        protected Execution(@Nonnull StepContext context) throws InterruptedException, IOException {
            super(context);
            run = context.get(Run.class);
            listener = context.get(TaskListener.class);
            envVars = context.get(EnvVars.class);
            workspace = context.get(FilePath.class);
            launcher = context.get(Launcher.class);
            node = context.get(Node.class);
        }

        @Override
        protected Integer run() throws ScannerJenkinsException {
            return ScanCommandsFactory.createPipelineCommand(run, listener, envVars, launcher, node, workspace)
                .runScanner(getParametersMap(workspace, listener));
        }

    }

}
