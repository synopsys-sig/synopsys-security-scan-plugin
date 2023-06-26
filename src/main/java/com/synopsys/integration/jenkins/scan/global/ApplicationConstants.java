package com.synopsys.integration.jenkins.scan.global;

public class ApplicationConstants {

    public static final String APPLICATION_NAME = "synopsys-security-scan";
    public static final String DISPLAY_NAME = "Synopsys Scan";
    public static final String PIPELINE_NAME = "synopsys_scan";
    public static final String BRIDGE_ARTIFACTORY_URL = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/";
    public static final String SYNOPSYS_BRIDGE_RUN_COMMAND = "./synopsys-bridge";
    public static final String SYNOPSYS_BRIDGE_LATEST_VERSION = "latest";
    public static final String PLATFORM_LINUX = "linux64";
    public static final String PLATFORM_WINDOWS = "win64";
    
    public static final String BLACKDUCK_URL_KEY = "blackduck.url";
    public static final String BLACKDUCK_API_TOKEN_KEY = "blackduck.token";
    public static final String BLACKDUCK_INSTALL_DIRECTORY_KEY = "blackduck.install.directory";
    public static final String BLACKDUCK_SCAN_FULL_KEY = "blackduck.scan.full";
    public static final String BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY = "blackduck.scan.failure.severities";
    public static final String BLACKDUCK_AUTOMATION_FIXPR_KEY = "blackduck.automation.fixpr";
    public static final String BLACKDUCK_AUTOMATION_PRCOMMENT_KEY = "blackduck.automation.prcomment";
    public static final String BRIDGE_DOWNLOAD_FILE_PATH = "/tmp/synopsys-security-scan";
    public static final String BRIDGE_ZIP_FILE_FORMAT = "bridge.zip";



    public static final String getSynopsysBridgeZipFileName(String platform) {
        return "synopsys-bridge-" + platform + ".zip";
    }

}
