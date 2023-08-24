/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.strategy;

import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import java.util.Map;

public interface ScanStrategy {
    ScanType getScanType();

    boolean isValidScanParameters(Map<String, Object> parametersMap);
    
    Object prepareScanInputForBridge(Map<String, Object> parametersMap);
}
