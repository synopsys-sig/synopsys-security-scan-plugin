package com.synopsys.integration.jenkins.scan.service.scan.coverity;

import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.Coverity;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyService;
import hudson.model.TaskListener;
import java.util.Map;

public class CoverityParametersService implements ScanStrategyService {
    private final TaskListener listener;

    public CoverityParametersService(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.COVERITY;
    }

    @Override
    public boolean isValidScanParameters(Map<String, Object> parametersMap) {
        return false;
    }

    @Override
    public Coverity prepareScanInputForBridge(Map<String, Object> parametersMap) {
        return null;
    }
}
