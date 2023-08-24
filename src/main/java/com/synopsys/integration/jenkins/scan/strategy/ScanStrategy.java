package com.synopsys.integration.jenkins.scan.strategy;

import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import java.util.Map;

public interface ScanStrategy {
    ScanType getScanType();

    boolean isValidScanParameters(Map<String, Object> parametersMap);
    
    Object prepareScanInputForBridge(Map<String, Object> parametersMap);
}
