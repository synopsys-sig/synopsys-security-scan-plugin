package com.synopsys.integration.jenkins.scan.strategy;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.scan.blackduck.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.coverity.CoverityParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.polaris.PolarisParametersService;
import hudson.model.TaskListener;
import java.util.Map;

public class ScanStrategyFactory {
    private final TaskListener listener;

    public ScanStrategyFactory(TaskListener listener) {
        this.listener = listener;
    }

    public ScanStrategy getParametersService(Map<String, Object> parametersMap) {
        ScanType scanType = ScanType.BLACKDUCK;

        if (parametersMap.containsKey(ApplicationConstants.SCAN_TYPE_KEY)) {
            scanType = ScanType.valueOf(parametersMap.get(ApplicationConstants.SCAN_TYPE_KEY).toString());
        }
        if (scanType.equals(ScanType.COVERITY)) {
            return new CoverityParametersService(listener);
        } else if (scanType.equals(ScanType.POLARIS)) {
            return new PolarisParametersService(listener);
        } else {
            return new BlackDuckParametersService(listener);
        }
    }

}
