package com.synopsys.integration.jenkins.scan;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyFactory;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyService;
import java.util.Map;

public class ScanPipelineCommands {
    private final SecurityScanner scanner;

    public ScanPipelineCommands(SecurityScanner scanner) {
        this.scanner = scanner;
    }

    public int runScanner(Map<String, Object> scanParameters, ScanStrategyFactory scanStrategyFactory) throws ScannerJenkinsException {
        ScanStrategyService scanStrategyService = scanStrategyFactory.getParametersService(scanParameters);

        int exitCode = -1;

        if (scanStrategyService.isValidScanParameters(scanParameters)) {
            try {
                exitCode = scanner.runScanner(scanParameters, scanStrategyService);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ScannerJenkinsException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
            }
        }

        if (exitCode != 0) {
            throw new ScannerJenkinsException(ExceptionMessages.scannerFailedWithExitCode(exitCode));
        }

        return exitCode;
    }

}
