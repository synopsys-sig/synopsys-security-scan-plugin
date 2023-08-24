package com.synopsys.integration.jenkins.scan.extension.pipeline;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.ScanCommandsFactory;
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategyFactory;
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

    private String scan_type;

    private String blackduck_url;
    private String blackduck_api_token;
    private String blackduck_install_directory;
    private boolean blackduck_scan_full = true;
    private String blackduck_scan_failure_severities;
    private boolean blackduck_automation_fixpr = false;
    private boolean blackduck_automation_prcomment = false;

    private String coverity_connect_url;
    private String coverity_connect_user_name;
    private String coverity_connect_user_password;
    private String coverity_connect_project_name;
    private String coverity_connect_stream_name;
    private String coverity_connect_policy_view;
    private String coverity_install_directory;
    private boolean coverity_automation_prcomment = false;

    private String bitbucket_token;

    private String bridge_download_url;
    private String bridge_download_version;
    private String synopsys_bridge_path;
    private boolean include_diagnostics = false;
    private String bridge_polaris_serverurl;
    private String bridge_polaris_accesstoken;
    private String bridge_polaris_application_name;
    private String bridge_polaris_project_name;
    private String bridge_polaris_assessment_types;

    @DataBoundConstructor
    public SecurityScanStep() {
        /* Intentionally left empty */
    }

    @DataBoundSetter
    public void setScan_type(String scan_type) {
        this.scan_type = scan_type;
    }

    @DataBoundSetter
    public void setBlackduck_url(String blackduck_url) {
        this.blackduck_url = blackduck_url;
    }

    @DataBoundSetter
    public void setBlackduck_api_token(String blackduck_api_token) {
        this.blackduck_api_token = blackduck_api_token;
    }

    @DataBoundSetter
    public void setBlackduck_install_directory(String blackduck_install_directory) {
        this.blackduck_install_directory = blackduck_install_directory;
    }

    @DataBoundSetter
    public void setBlackduck_scan_full(boolean blackduck_scan_full) {
        this.blackduck_scan_full = blackduck_scan_full;
    }

    @DataBoundSetter
    public void setBlackduck_scan_failure_severities(String blackduck_scan_failure_severities) {
        this.blackduck_scan_failure_severities = blackduck_scan_failure_severities;
    }

    @DataBoundSetter
    public void setBlackduck_automation_fixpr(boolean blackduck_automation_fixpr) {
        this.blackduck_automation_fixpr = blackduck_automation_fixpr;
    }

    @DataBoundSetter
    public void setBlackduck_automation_prcomment(boolean blackduck_automation_prcomment) {
        this.blackduck_automation_prcomment = blackduck_automation_prcomment;
    }

    @DataBoundSetter
    public void setCoverity_connect_url(String coverity_connect_url) {
        this.coverity_connect_url = coverity_connect_url;
    }

    @DataBoundSetter
    public void setCoverity_connect_user_name(String coverity_connect_user_name) {
        this.coverity_connect_user_name = coverity_connect_user_name;
    }

    @DataBoundSetter
    public void setCoverity_connect_user_password(String coverity_connect_user_password) {
        this.coverity_connect_user_password = coverity_connect_user_password;
    }

    @DataBoundSetter
    public void setCoverity_connect_project_name(String coverity_connect_project_name) {
        this.coverity_connect_project_name = coverity_connect_project_name;
    }

    @DataBoundSetter
    public void setCoverity_connect_stream_name(String coverity_connect_stream_name) {
        this.coverity_connect_stream_name = coverity_connect_stream_name;
    }

    @DataBoundSetter
    public void setCoverity_connect_policy_view(String coverity_connect_policy_view) {
        this.coverity_connect_policy_view = coverity_connect_policy_view;
    }

    @DataBoundSetter
    public void setCoverity_install_directory(String coverity_install_directory) {
        this.coverity_install_directory = coverity_install_directory;
    }

    @DataBoundSetter
    public void setCoverity_automation_prcomment(boolean coverity_automation_prcomment) {
        this.coverity_automation_prcomment = coverity_automation_prcomment;
    }

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
    public void setInclude_diagnostics(boolean include_diagnostics) {
        this.include_diagnostics = include_diagnostics;
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

    public String getScan_type() {
        return scan_type;
    }

    public String getBlackduck_url() {
        return blackduck_url;
    }

    public String getBlackduck_api_token() {
        return blackduck_api_token;
    }

    public String getBlackduck_install_directory() {
        return blackduck_install_directory;
    }

    public boolean getBlackduck_scan_full() {
        return blackduck_scan_full;
    }

    public String getBlackduck_scan_failure_severities() {
        return blackduck_scan_failure_severities;
    }

    public boolean getBlackduck_automation_fixpr() {
        return blackduck_automation_fixpr;
    }

    public boolean getBlackduck_automation_prcomment() {
        return blackduck_automation_prcomment;
    }

    public String getCoverity_connect_url() {
        return coverity_connect_url;
    }

    public String getCoverity_connect_user_name() {
        return coverity_connect_user_name;
    }

    public String getCoverity_connect_user_password() {
        return coverity_connect_user_password;
    }

    public String getCoverity_connect_project_name() {
        return coverity_connect_project_name;
    }

    public String getCoverity_connect_stream_name() {
        return coverity_connect_stream_name;
    }

    public String getCoverity_connect_policy_view() {
        return coverity_connect_policy_view;
    }

    public String getCoverity_install_directory() {
        return coverity_install_directory;
    }

    public boolean getCoverity_automation_prcomment() {
        return coverity_automation_prcomment;
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

    public boolean getInclude_diagnostics() {
        return include_diagnostics;
    }

    private Map<String, Object> getParametersMap() {
        return ScanCommandsFactory.preparePipelineParametersMap(this);
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

        public ListBoxModel doFillScan_typeItems() {
            ListBoxModel items = new ListBoxModel();
            Arrays.stream(ScanType.values()).forEach(scanType -> items.add(String.valueOf(scanType)));
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
                .runScanner(getParametersMap(), new ScanStrategyFactory(listener));
        }

    }

}
