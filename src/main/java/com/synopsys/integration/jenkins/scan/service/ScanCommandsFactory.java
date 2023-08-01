package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.ScanPipelineCommands;
import com.synopsys.integration.jenkins.scan.SecurityScanner;
import com.synopsys.integration.jenkins.scan.extension.pipeline.SecurityScanStep;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;

import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.util.HashMap;
import java.util.Map;

public class ScanCommandsFactory {
    private final TaskListener listener;
    private final EnvVars envVars;
    private final FilePath workspace;

    private ScanCommandsFactory(TaskListener listener, EnvVars envVars, FilePath workspace) throws AbortException {
        this.listener = listener;
        this.envVars = envVars;

        if (workspace == null) {
            throw new AbortException(ExceptionMessages.NULL_WORKSPACE);
        }
        this.workspace = workspace;
    }

    public static ScanPipelineCommands createPipelineCommand(Run<?, ?> run, TaskListener listener,
                                                             EnvVars envVars, Launcher launcher,
                                                             Node node, FilePath workspace) {
        return new ScanPipelineCommands(
            new SecurityScanner(run, listener, launcher, workspace, envVars,
                new ScannerArgumentService(listener, envVars)));
    }

    public static Map<String, Object> preparePipelineParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> parametersMap = new HashMap<>();

        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_url())) {
            parametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, scanStep.getBlackduck_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_api_token())) {
            parametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, scanStep.getBlackduck_api_token());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_install_directory())) {
            parametersMap.put(ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY, scanStep.getBlackduck_install_directory());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_scan_failure_severities())) {
            parametersMap.put(ApplicationConstants.BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY, scanStep.getBlackduck_scan_failure_severities().toUpperCase());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBitbucket_token())) {
            parametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, scanStep.getBitbucket_token());
        }

        parametersMap.put(ApplicationConstants.BLACKDUCK_SCAN_FULL_KEY, scanStep.getBlackduck_scan_full());
        parametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, scanStep.getBlackduck_automation_fixpr());
        parametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, scanStep.getBlackduck_automation_prcomment());

        if (!Utility.isStringNullOrBlank(scanStep.getBridge_download_url())) {
            parametersMap.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, scanStep.getBridge_download_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_download_version())) {
            parametersMap.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, scanStep.getBridge_download_version());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getSynopsys_bridge_path())) {
            parametersMap.put(ApplicationConstants.BRIDGE_INSTALLATION_PATH, scanStep.getSynopsys_bridge_path());
        }
        parametersMap.put(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY, scanStep.getInclude_diagnostics());

        return parametersMap;
    }
    
}
