package com.synopsys.integration.jenkins.scan.global;

public class ExceptionMessages {

    public static final String NULL_WORKSPACE = "Detect cannot be executed when the workspace is null";

    public static String scannerFailedWithExitCode(int exitCode) {
        return "Synopsys Security Scan failed with exit code " + exitCode;
    }

    public static String scannerFailureMessage(String message) {
        return "Synopsys Security Scan failed!! " + message;
    }

}
