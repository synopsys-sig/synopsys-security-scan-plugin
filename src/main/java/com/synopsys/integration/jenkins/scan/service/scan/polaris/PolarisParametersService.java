package com.synopsys.integration.jenkins.scan.service.scan.polaris;

import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.polaris.Polaris;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyService;
import hudson.model.TaskListener;
import java.util.Map;

public class PolarisParametersService implements ScanStrategyService {
    private final TaskListener listener;

    public PolarisParametersService(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.POLARIS;
    }

    @Override
    public boolean isValidScanParameters(Map<String, Object> parametersMap) {
        return false;
    }

    @Override
    public Polaris prepareScanInputForBridge(Map<String, Object> parametersMap) {
        return null;
    }
}
