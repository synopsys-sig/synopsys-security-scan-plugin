/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.service.scan;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.SecurityProduct;
import com.synopsys.integration.jenkins.scan.service.scan.blackduck.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.coverity.CoverityParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.polaris.PolarisParametersService;
import hudson.model.TaskListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ScanParametersService {
    private final TaskListener listener;

    public ScanParametersService(TaskListener listener) {
        this.listener = listener;
    }

    public boolean isValidScanParameters(Map<String, Object> scanParameters) {
        Set<String> securityProducts = getSynopsysSecurityProducts(scanParameters);
        
        boolean isValidBlackDuckParameters = true;
        boolean isValidCoverityParameters = true;
        boolean isValidPolarisParameters = true;

        if (securityProducts.contains(SecurityProduct.BLACKDUCK.name())) {
            BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService(listener);
            isValidBlackDuckParameters = blackDuckParametersService.isValidBlackDuckParameters(scanParameters);
        }
        if (securityProducts.contains(SecurityProduct.COVERITY.name())) {
            CoverityParametersService coverityParametersService = new CoverityParametersService(listener);
            isValidCoverityParameters = coverityParametersService.isValidCoverityParameters(scanParameters);
        }
        if (securityProducts.contains(SecurityProduct.POLARIS.name())) {
            PolarisParametersService polarisParametersService = new PolarisParametersService(listener);
            isValidPolarisParameters = polarisParametersService.isValidPolarisParameters(scanParameters);
        }
        
        return isValidBlackDuckParameters && isValidCoverityParameters && isValidPolarisParameters;
    }
    
    public Set<String> getSynopsysSecurityProducts(Map<String, Object> scanParameters) {
        String securityProduct = (String) scanParameters.get(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY);
        Set<String> securityProducts = new HashSet<>();

        if (securityProduct.contains(",")) {
            securityProducts = Arrays.stream(securityProduct.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        } else {
            securityProducts.add(securityProduct.trim().toUpperCase());
        }
        
        return securityProducts;
    }
}
