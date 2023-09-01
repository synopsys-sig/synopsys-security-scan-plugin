/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.service.scan.blackduck;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategy;
import hudson.model.TaskListener;
import java.util.*;

public class BlackDuckParametersService implements ScanStrategy {
    private final LoggerWrapper logger;
    public BlackDuckParametersService(TaskListener listener) {
        this.logger = new LoggerWrapper(listener);
    }

    @Override
    public ScanType getScanType() {
        return ScanType.BLACKDUCK;
    }

    @Override
    public boolean isValidScanParameters(Map<String, Object> blackDuckParameters) {
        if (blackDuckParameters == null || blackDuckParameters.isEmpty()) {
            return false;
        }
        
        List<String> invalidParams = new ArrayList<>();

        Arrays.asList(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY,
                ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY)
            .forEach(key -> {
                boolean isKeyValid = blackDuckParameters.containsKey(key)
                    && blackDuckParameters.get(key) != null
                    && !blackDuckParameters.get(key).toString().isEmpty();

                if (!isKeyValid) {
                    invalidParams.add(key);
                }
            });

        if (invalidParams.isEmpty()) {
            logger.info("BlackDuck parameters are validated successfully");
            return true;
        } else {
            logger.error(LogMessages.BLACKDUCK_PARAMETER_VALIDATION_FAILED);
            logger.error("Invalid BlackDuck parameters: " + invalidParams);
            return false;
        }
    }

    @Override
    public BlackDuck prepareScanInputForBridge(Map<String, Object> blackDuckParameters) {
        BlackDuck blackDuck = new BlackDuck();

        for (Map.Entry<String, Object> entry : blackDuckParameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();

            switch (key) {
                case ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY:
                    blackDuck.setUrl(value);
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY:
                    blackDuck.setToken(value);
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_INSTALL_DIRECTORY_KEY:
                    blackDuck.getInstall().setDirectory(value);
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_SCAN_FULL_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getScan().setFull(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY:
                    if (!value.isEmpty()) {
                        List<String> failureSeverities = new ArrayList<>();
                        String[] failureSeveritiesInput = value.toUpperCase().split(",");

                        for (String input : failureSeveritiesInput) {
                            failureSeverities.add(input.trim());
                        }
                        blackDuck.getScan().getFailure().setSeverities(failureSeverities);
                    }
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_FIXPR_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getAutomation().setFixpr(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getAutomation().setPrComment(Boolean.parseBoolean(value));
                    }
                    break;
                default:
                    break;
            }
        }

        return blackDuck;
    }

}
