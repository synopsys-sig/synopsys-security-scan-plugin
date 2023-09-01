/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.service.parameters;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.parameters.blackduck.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.parameters.coverity.CoverityParametersService;
import com.synopsys.integration.jenkins.scan.service.parameters.polaris.PolarisParametersService;
import hudson.model.TaskListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ParametersService {
    private final TaskListener listener;

    public ParametersService(TaskListener listener) {
        this.listener = listener;
    }

    public boolean isValidScanParameters(Map<String, Object> scanParameters) {
        Set<String> scanTypes = getScanTypes(scanParameters);
        
        boolean isValidBlackDuckParameters = true;
        boolean isValidCoverityParameters = true;
        boolean isValidPolarisParameters = true;

        if (scanTypes.contains(ScanType.BLACKDUCK.name())) {
            BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService(listener);
            isValidBlackDuckParameters = blackDuckParametersService.isValidBlackDuckParameters(scanParameters);
        }
        if (scanTypes.contains(ScanType.COVERITY.name())) {
            CoverityParametersService coverityParametersService = new CoverityParametersService(listener);
            isValidCoverityParameters = coverityParametersService.isValidCoverityParameters(scanParameters);
        }
        if (scanTypes.contains(ScanType.POLARIS.name())) {
            PolarisParametersService polarisParametersService = new PolarisParametersService(listener);
            isValidPolarisParameters = polarisParametersService.isValidPolarisParameters(scanParameters);
        }
        
        return isValidBlackDuckParameters && isValidCoverityParameters && isValidPolarisParameters;
    }
    
    public Set<String> getScanTypes(Map<String, Object> scanParameters) {
        String scanType = (String) scanParameters.get(ApplicationConstants.SCAN_TYPE_KEY);
        Set<String> scanTypes = new HashSet<>();

        if (scanType.contains(",")) {
            scanTypes = Arrays.stream(scanType.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        } else {
            scanTypes.add(scanType.trim().toUpperCase());
        }
        
        return scanTypes;
    }
}
