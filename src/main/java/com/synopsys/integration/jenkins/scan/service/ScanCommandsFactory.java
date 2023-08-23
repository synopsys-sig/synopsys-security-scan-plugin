package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.ScanPipelineCommands;
import com.synopsys.integration.jenkins.scan.SecurityScanner;
import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.extension.pipeline.SecurityScanStep;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyFactory;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.HashMap;
import java.util.Map;
import jenkins.model.GlobalConfiguration;

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
                new ScannerArgumentService(listener, envVars, workspace)), workspace, listener);
    }

    public static Map<String, Object> preparePipelineParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> parametersMap = new HashMap<>();

        setGlobalConfigurationValues(parametersMap);

        if (!Utility.isStringNullOrBlank(scanStep.getScan_type())) {
            parametersMap.put(ApplicationConstants.SCAN_TYPE_KEY, scanStep.getScan_type());
        }

        ScanType scanType = ScanStrategyFactory.getScanType(scanStep.getScan_type());
        if (scanType.equals(ScanType.COVERITY)) {
            prepareCoverityParametersMap(scanStep, parametersMap);
        } else if (scanType.equals(ScanType.POLARIS)) {
            // preparePolarisParametersMap
        } else {
            prepareBlackDuckParametersMap(scanStep, parametersMap);
        }

        if (!Utility.isStringNullOrBlank(scanStep.getBitbucket_token())) {
            parametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, scanStep.getBitbucket_token());
        }

        prepareBridgeParametersMap(scanStep, parametersMap);

        return parametersMap;
    }

    private static void setGlobalConfigurationValues(Map<String, Object> parametersMap) {
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);
        if (config != null) {
            ScanType scanType = ScanType.BLACKDUCK;

            if (!Utility.isStringNullOrBlank(config.getScanType())) {
                scanType = ScanType.valueOf(config.getScanType());
                parametersMap.put(ApplicationConstants.SCAN_TYPE_KEY, config.getScanType());
            }

            if (scanType.equals(ScanType.COVERITY)) {
                if (!Utility.isStringNullOrBlank(config.getCoverityConnectUrl())) {
                    parametersMap.put(ApplicationConstants.COVERITY_CONNECT_URL_KEY, config.getCoverityConnectUrl());
                }
                if (!Utility.isStringNullOrBlank(config.getCoverityConnectUserName())) {
                    parametersMap.put(ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY, config.getCoverityConnectUserName());
                }
                if (!Utility.isStringNullOrBlank(config.getCoverityConnectUserPassword())) {
                    parametersMap.put(ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY, config.getCoverityConnectUserPassword());
                }
            } else if (scanType.equals(ScanType.POLARIS)) {
                // set polaris global config values
            } else {
                if (!Utility.isStringNullOrBlank(config.getBlackDuckUrl())) {
                    parametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, config.getBlackDuckUrl());
                }
                if (!Utility.isStringNullOrBlank(config.getBlackDuckApiToken())) {
                    parametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, config.getBlackDuckApiToken());
                }
            }

            if (!Utility.isStringNullOrBlank(config.getBitbucketToken())) {
                parametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, config.getBitbucketToken());
            }

            if (!Utility.isStringNullOrBlank(config.getSynopsysBridgeDownloadUrl())) {
                parametersMap.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, config.getSynopsysBridgeDownloadUrl());
            }
        }
    }

    private static void prepareBlackDuckParametersMap(SecurityScanStep scanStep, Map<String, Object> parametersMap) {
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
        parametersMap.put(ApplicationConstants.BLACKDUCK_SCAN_FULL_KEY, scanStep.getBlackduck_scan_full());
        parametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, scanStep.getBlackduck_automation_fixpr());
        parametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, scanStep.getBlackduck_automation_prcomment());
    }

    private static void prepareCoverityParametersMap(SecurityScanStep scanStep, Map<String, Object> parametersMap) {
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_url())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_URL_KEY, scanStep.getCoverity_connect_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_user_name())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY, scanStep.getCoverity_connect_user_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_user_password())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY, scanStep.getCoverity_connect_user_password());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_project_name())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_PROJECT_NAME_KEY, scanStep.getCoverity_connect_project_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_stream_name())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_STREAM_NAME_KEY, scanStep.getCoverity_connect_stream_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_policy_view())) {
            parametersMap.put(ApplicationConstants.COVERITY_CONNECT_POLICY_VIEW_KEY, scanStep.getCoverity_connect_policy_view());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_install_directory())) {
            parametersMap.put(ApplicationConstants.COVERITY_INSTALL_DIRECTORY_KEY, scanStep.getCoverity_install_directory());
        }
        parametersMap.put(ApplicationConstants.COVERITY_AUTOMATION_PRCOMMENT_KEY, scanStep.getCoverity_automation_prcomment());
    }

    private static void prepareBridgeParametersMap(SecurityScanStep scanStep, Map<String, Object> parametersMap) {
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
    }

}
