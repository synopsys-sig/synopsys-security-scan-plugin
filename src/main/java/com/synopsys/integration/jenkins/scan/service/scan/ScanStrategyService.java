package com.synopsys.integration.jenkins.scan.service.scan;

import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import java.util.Map;

public interface ScanStrategyService {
    ScanType getScanType();

    boolean isValidScanParameters(Map<String, Object> parametersMap);
    
    Object prepareScanInputForBridge(Map<String, Object> parametersMap);
}
