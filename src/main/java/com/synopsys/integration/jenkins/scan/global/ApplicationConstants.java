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
    public static final String BLACKDUCK_URL_KEY = "blackduck_url";
    public static final String BLACKDUCK_API_TOKEN_KEY = "blackduck_api_token";
    public static final String BLACKDUCK_INSTALL_DIRECTORY_KEY = "blackduck_install_directory";
    public static final String BLACKDUCK_SCAN_FULL_KEY = "blackduck_scan_full";
    public static final String BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY = "blackduck_scan_failure_severities";
    public static final String BLACKDUCK_AUTOMATION_FIXPR_KEY = "blackduck_automation_fixpr";
    public static final String BLACKDUCK_AUTOMATION_PRCOMMENT_KEY = "blackduck_automation_prcomment";

    public static final String COVERITY_CONNECT_URL_KEY = "coverity_connect_url";
    public static final String COVERITY_CONNECT_USER_NAME_KEY = "coverity_connect_user_name";
    public static final String COVERITY_CONNECT_USER_PASSWORD_KEY = "coverity_connect_user_password";
    public static final String COVERITY_CONNECT_PROJECT_NAME_KEY = "coverity_connect_project_name";
    public static final String COVERITY_CONNECT_STREAM_NAME_KEY = "coverity_connect_stream_name";
    public static final String COVERITY_CONNECT_POLICY_VIEW_KEY = "coverity_connect_policy_view";
    public static final String COVERITY_INSTALL_DIRECTORY_KEY = "coverity_install_directory";
    public static final String COVERITY_AUTOMATION_PRCOMMENT_KEY = "coverity_automation_prcomment";

    public static final String BITBUCKET_TOKEN_KEY = "bitbucket_token";

    public static final String BRIDGE_DOWNLOAD_URL = "bridge_download_url";
    public static final String BRIDGE_DOWNLOAD_VERSION = "bridge_download_version";
    public static final String BRIDGE_INSTALLATION_PATH = "synopsys_bridge_path";
    public static final String INCLUDE_DIAGNOSTICS_KEY = "include_diagnostics";

}