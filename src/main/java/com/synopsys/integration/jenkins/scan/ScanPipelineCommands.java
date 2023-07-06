package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;

import java.io.IOException;
import java.util.Map;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;
    public ScanPipelineCommands(SecurityScanner scanner) {
        this.scanner = scanner;
    }

    public int runScanner(Map<String, Object> scanParameters) throws IOException, InterruptedException, ScannerJenkinsException {
        for (Map.Entry<String, Object> entry : scanParameters.entrySet()) {
            if (entry.getValue().equals("null")) {
                entry.setValue(null);
            }
        }

        int exitCode = scanner.runScanner(scanParameters);
        if (exitCode > 0) {
            throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
        }
        return exitCode;
    }
}
