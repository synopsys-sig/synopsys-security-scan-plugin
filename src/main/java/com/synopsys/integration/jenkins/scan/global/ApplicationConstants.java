/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.global;

public class ApplicationConstants {
    public static final String DISPLAY_NAME = "Synopsys Scan";
    public static final String PIPELINE_NAME = "synopsys_scan";
    public static final String BRIDGE_ARTIFACTORY_URL =
            "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge";
    public static final String SYNOPSYS_BRIDGE_RUN_COMMAND = "synopsys-bridge";
    public static final String SYNOPSYS_BRIDGE_RUN_COMMAND_WINDOWS = "synopsys-bridge.exe";
    public static final String SYNOPSYS_BRIDGE_LATEST_VERSION = "latest";
    public static final String BRIDGE_DOWNLOAD_FILE_PATH = "/tmp/synopsys-security-scan";
    public static final String BRIDGE_ZIP_FILE_FORMAT = "bridge.zip";
    public static final String PLATFORM_LINUX = "linux64";
    public static final String PLATFORM_WINDOWS = "win64";
    public static final String PLATFORM_MAC = "macosx";
    public static final String DEFAULT_DIRECTORY_NAME = "synopsys-bridge";
    public static final String BRIDGE_DIAGNOSTICS_DIRECTORY = ".bridge";
    public static final int BRIDGE_DOWNLOAD_MAX_RETRIES = 3;
    public static final int INTERVAL_BETWEEN_CONSECUTIVE_RETRY_ATTEMPTS = 10000;
    public static final String ALL_FILES_WILDCARD_SYMBOL = "**";
    public static final String BRIDGE_BINARY = "synopsys-bridge";
    public static final String BRIDGE_BINARY_WINDOWS = "synopsys-bridge.exe";
    public static final String EXTENSIONS_DIRECTORY = "extensions";
    public static final String VERSION_FILE = "versions.txt";
    public static final String NOT_AVAILABLE = "NA";
    public static final String ENV_JOB_NAME_KEY = "JOB_NAME";
    public static final String ENV_CHANGE_ID_KEY = "CHANGE_ID";
    public static final String ENV_BRANCH_NAME_KEY = "BRANCH_NAME";

    public static final String SCAN_TYPE_KEY = "scan_type";
    public static final String BRIDGE_BLACKDUCK_URL_KEY = "bridge_blackduck_url";
    public static final String BRIDGE_BLACKDUCK_API_TOKEN_KEY = "bridge_blackduck_api_token";
    public static final String BRIDGE_BLACKDUCK_INSTALL_DIRECTORY_KEY = "bridge_blackduck_install_directory";
    public static final String BRIDGE_BLACKDUCK_SCAN_FULL_KEY = "bridge_blackduck_scan_full";
    public static final String BRIDGE_BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY = "bridge_blackduck_scan_failure_severities";
    public static final String BRIDGE_BLACKDUCK_AUTOMATION_FIXPR_KEY = "bridge_blackduck_automation_fixpr";
    public static final String BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY = "bridge_blackduck_automation_prcomment";

    public static final String BRIDGE_COVERITY_CONNECT_URL_KEY = "bridge_coverity_connect_url";
    public static final String BRIDGE_COVERITY_CONNECT_USER_NAME_KEY = "bridge_coverity_connect_user_name";
    public static final String BRIDGE_COVERITY_CONNECT_USER_PASSWORD_KEY = "bridge_coverity_connect_user_password";
    public static final String BRIDGE_COVERITY_CONNECT_PROJECT_NAME_KEY = "bridge_coverity_connect_project_name";
    public static final String BRIDGE_COVERITY_CONNECT_STREAM_NAME_KEY = "bridge_coverity_connect_stream_name";
    public static final String BRIDGE_COVERITY_CONNECT_POLICY_VIEW_KEY = "bridge_coverity_connect_policy_view";
    public static final String BRIDGE_COVERITY_INSTALL_DIRECTORY_KEY = "bridge_coverity_install_directory";
    public static final String BRIDGE_COVERITY_AUTOMATION_PRCOMMENT_KEY = "bridge_coverity_automation_prcomment";
    public static final String BRIDGE_COVERITY_VERSION_KEY = "bridge_coverity_version";
    public static final String BRIDGE_COVERITY_LOCAL_KEY = "bridge_coverity_local";
    public static final String BRIDGE_POLARIS_SERVER_URL_KEY = "bridge_polaris_serverurl";
    public static final String BRIDGE_POLARIS_ACCESS_TOKEN_KEY = "bridge_polaris_accesstoken";
    public static final String BRIDGE_POLARIS_APPLICATION_NAME_KEY = "bridge_polaris_application_name";
    public static final String BRIDGE_POLARIS_PROJECT_NAME_KEY = "bridge_polaris_project_name";
    public static final String BRIDGE_POLARIS_ASSESSMENT_TYPES_KEY = "bridge_polaris_assessment_types";
    public static final String BRIDGE_POLARIS_TRIAGE_KEY = "bridge_polaris_triage";

    public static final String BITBUCKET_TOKEN_KEY = "bitbucket_token";

    public static final String BRIDGE_DOWNLOAD_URL = "bridge_download_url";
    public static final String BRIDGE_DOWNLOAD_VERSION = "bridge_download_version";
    public static final String BRIDGE_INSTALLATION_PATH = "synopsys_bridge_path";
    public static final String BRIDGE_INCLUDE_DIAGNOSTICS_KEY = "bridge_include_diagnostics";

    public static final String BLACKDUCK_INPUT_JSON_PREFIX = "blackduck_input";
    public static final String COVERITY_INPUT_JSON_PREFIX = "coverity_input";
    public static final String POLARIS_INPUT_JSON_PREFIX = "polaris_input";

}