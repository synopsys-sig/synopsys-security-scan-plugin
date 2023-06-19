package com.synopsys.integration.jenkins.scan.global;
/**
 * @author akib @Date 6/15/23
 */
public class ApplicationConstants {

    public static final String APPLICATION_NAME = "synopsys-security-scan";
    public static final String DISPLAY_NAME = "Synopsys Scan";
    public static final String PIPELINE_NAME = "synopsys_scan";
    public static final String BRIDGE_ARTIFACTORY_URL = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/";
    public static final String SYNOPSYS_BRIDGE_RUN_COMMAND = "./synopsys-bridge";
    public static final String SYNOPSYS_BRIDGE_LATEST_VERSION = "latest";
    public static final String PLATFORM_LINUX = "linux64";
    public static final String PLATFORM_WINDOWS = "win64";

    public static final String getSynopsysBridgeZipFileName(String platform) {
        return "synopsys-bridge-" + platform + ".zip";
    }

}
