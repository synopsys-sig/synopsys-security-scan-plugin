package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;

import java.util.Map;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;

    public ScanPipelineCommands(SecurityScanner scanner) {
        this.scanner = scanner;
    }

    public int runScanner(Map<String, Object> scanParameters) {
        for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
            if (entry.getValue().equals("null")) {
                entry.setValue(null);
            }
        }

        int exitCode = 1;
        try {
            exitCode = scanner.runScanner(scanParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (exitCode > 0) {
            handleScannerFailure(exitCode);
        }

        return exitCode;
    }

    private void handleScannerFailure(int exitCode) {
        try {
            throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
        } catch (ScannerJenkinsException e) {
            e.printStackTrace();
        }
    }
}
