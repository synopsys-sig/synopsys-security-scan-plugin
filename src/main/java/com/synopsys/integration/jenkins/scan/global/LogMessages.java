package com.synopsys.integration.jenkins.scan.global;

public class LogMessages {
    public static final String ASTERISKS = "******************************************************************************";
    public static final String START_BRIDGE_EXECUTION = "START EXECUTION OF SYNOPSYS BRIDGE";
    public static final String END_BRIDGE_EXECUTION = "END EXECUTION OF SYNOPSYS BRIDGE";

    public static final String DOWNLOADING_SYNOPSYS_BRIDGE_FROM_URL = "Downloading Synopsys Bridge from: %s %n";
    public static final String SYNOPSYS_BRIDGE_SUCCESSFULLY_DOWNLOADED_IN_PATH = "Synopsys Bridge successfully downloaded in: %s %n";
    public static final String SYNOPSYS_BRIDGE_DOWNLOADED_FAILED_AND_RETRY = "Synopsys Bridge download failed and attempt#%s to download again %n";
    public static final String SYNOPSYS_BRIDGE_DOWNLOADED_FAILED_AND_WITH_MAX_ATTEMPT = "Synopsys Bridge download failed after %s attempts %n";
    public static final String SYNOPSYS_BRIDGE_DOWNLOADED_INTERRUPTED = "Interrupted while waiting to retry Synopsys Bridge download";
    public static final String INVALID_SYNOPSYS_BRIDGE_DOWNLOAD_URL = "Invalid Synopsys Bridge download URL: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_CHECKING_BRIDGE_URL_EXISTENCE = "An exception occurred while checking bridge url exits or not: %s %n";

    public static final String EXCEPTION_OCCURRED_WHILE_CHECKING_BRIDGE_INSTALLATION = "An exception occurred while checking if the bridge is installed: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_EXTRACTING_BRIDGE_VERSION = "An exception occurred while extracting bridge-version from the versions.txt: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_DOWNLOADING_VERSION_FILE = "An exception occurred while downloading 'versions.txt': %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_CHECKING_VERSION_FILE_AVAILABILITY = "An exception occurred while checking if 'versions.txt' is available or not in the URL: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_GETTING_DIRECTORY_URL_FROM_DOWNLOAD_URL = "An exception occurred while getting directoryUrl from downloadUrl: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_GETTING_OS_INFO_FROM_AGENT_NODE = "An exception occurred while fetching the OS information for the agent node: %s %n";

    public static final String SYNOPSYS_BRIDGE_ZIP_PATH_AND_INSTALLATION_PATH = "Synopsys Bridge zip path: %s and bridge installation path: %s %n";
    public static final String EXCEPTION_OCCURRED_WHILE_UNZIPPING_SYNOPSYS_BRIDGE = "An exception occurred while unzipping Synopsys Bridge zip file: %s %n";
    public static final String JOB_RUNNING_ON_MASTER_NODE = "Jenkins job is running on master node";
    public static final String JOB_RUNNING_ON_AGENT_NODE = "Jenkins job is running on agent node remotely";
    public static final String FAILED_TO_FETCH_PLUGINS_DEFAULT_INSTALLATION_PATH = "Failed to fetch plugin's default installation path: %s %n";

    public static final String ARCHIVING_DIAGNOSTICS_FROM_PATH = "Archiving diagnostics jenkins artifact from: %s %n";
    public static final String DIAGNOSTICS_PATH_NOT_FOUND = "Archiving diagnostics failed as diagnostics path not found at: %s %n";
    public static final String DIAGNOSTICS_ARCHIVED_SUCCESSFULLY = "Diagnostics archived successfully in jenkins artifact";
    public static final String EXCEPTION_OCCURRED_WHILE_ARCHIVING_DIAGNOSTICS = "An exception occurred while archiving diagnostics in jenkins artifact! Reason: %s %n";

    public static final String BLACKDUCK_PARAMETER_VALIDATION_FAILED_FOR_PARAM = "BlackDuck parameter validation failed for %s %n";
    public static final String BLACKDUCK_PARAMETER_VALIDATION_FAILED = "BlackDuck parameters are not valid";
    public static final String BLACKDUCK_PARAMETER_VALIDATED_SUCCESSFULLY = "BlackDuck parameters are validated successfully";

}
