/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.service.scan;

import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;
import io.jenkins.plugins.synopsys.security.scan.global.enums.SecurityProduct;
import io.jenkins.plugins.synopsys.security.scan.service.scan.blackduck.BlackDuckParametersService;
import io.jenkins.plugins.synopsys.security.scan.service.scan.coverity.CoverityParametersService;
import io.jenkins.plugins.synopsys.security.scan.service.scan.polaris.PolarisParametersService;
import hudson.model.TaskListener;

import java.util.Arrays;
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
        String securityPlatform = (String) scanParameters.get(ApplicationConstants.PRODUCT_KEY);

        return Arrays.stream(securityPlatform.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }
}
