package com.synopsys.integration.jenkins.scan.global;
/**
 * @author akib @Date 6/15/23
 */
public class ExceptionMessages {

    public static final String NULL_WORKSPACE = "Detect cannot be executed when the workspace is null";

    public static String scannerFailedWithExitCode(int exitCode) {
        return "Scanner failed with exit code " + exitCode;
    }

}
