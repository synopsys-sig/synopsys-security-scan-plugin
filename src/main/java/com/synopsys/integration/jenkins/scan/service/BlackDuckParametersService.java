package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import jenkins.model.GlobalConfiguration;

import java.util.*;
import java.util.stream.Stream;

public class BlackDuckParametersService {
    public BlackDuck prepareBlackDuckInputForBridge(Map<String, Object> blackDuckParametersFromPipeline) {
        Map<String, Object> blackDuckParametersMapFromUI = createBlackDuckParametersMapFromJenkinsUI();
        Map<String, Object> blackDuckParametersMap = getCombinedBlackDuckParameters(blackDuckParametersFromPipeline, blackDuckParametersMapFromUI);
        return createBlackDuckObject(blackDuckParametersMap);
    }

    public Map<String, Object> prepareBlackDuckParameterValidation(Map<String, Object> blackDuckParametersFromPipeline) {
        Map<String, Object> blackDuckParametersMapFromUI = createBlackDuckParametersMapFromJenkinsUI();
        Map<String, Object> blackDuckParametersMap = getCombinedBlackDuckParameters(blackDuckParametersFromPipeline, blackDuckParametersMapFromUI);
        return blackDuckParametersMap;
    }

    public BlackDuck createBlackDuckObject(Map<String, Object> blackDuckParametersMap) {
        BlackDuck blackDuck = new BlackDuck();

        for (Map.Entry<String, Object> entry : blackDuckParametersMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString().trim();

            switch (key) {
                case ApplicationConstants.BLACKDUCK_URL_KEY:
                    blackDuck.setUrl(value);
                    break;
                case ApplicationConstants.BLACKDUCK_API_TOKEN_KEY:
                    blackDuck.setToken(value);
                    break;
                case ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY:
                    blackDuck.setInstallDirectory(value);
                    break;
                case ApplicationConstants.BLACKDUCK_SCAN_FULL_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getScan().setFull(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY:
                    if (value.length() > 0) {
                        List<String> failureSeverities = new ArrayList<>();
                        String[] failureSeveritiesInput = value.toUpperCase().split(",");

                        for (String input : failureSeveritiesInput) {
                            failureSeverities.add(input.trim());
                        }
                        blackDuck.getScan().setFailureSeverities(failureSeverities);
                    }
                    break;
                case ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getAutomation().setFixpr(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getAutomation().setPrcomment(Boolean.parseBoolean(value));
                    }
                    break;
            }
        }

        return blackDuck;
    }

    public boolean performBlackDuckParameterValidation(Map<String, Object> blackDuckParams) {
        return blackDuckParams != null
                && Stream.of(ApplicationConstants.BLACKDUCK_URL_KEY, ApplicationConstants.BLACKDUCK_API_TOKEN_KEY)
                .allMatch(key -> blackDuckParams.containsKey(key)
                        && blackDuckParams.get(key) != null
                        && !blackDuckParams.get(key).toString().isEmpty());
    }

    public Map<String, Object> getCombinedBlackDuckParameters(Map<String, Object> blackDuckParamsFromPipeline, Map<String, Object> blackDuckParametersMapFromUI) {
        if (Objects.isNull(blackDuckParametersMapFromUI)) {
            return blackDuckParamsFromPipeline;
        }
        for (Map.Entry<String, Object> entry : blackDuckParametersMapFromUI.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //Giving precedence to the pipeline arguments. Therefore, if same key-value occurs, pipeline's value will be taken.
            if (!blackDuckParamsFromPipeline.containsKey(key) ||
                    (blackDuckParamsFromPipeline.containsKey(key) && blackDuckParamsFromPipeline.get(key) == null)) {
                blackDuckParamsFromPipeline.put(key, value);
            }
        }
        return blackDuckParamsFromPipeline;
    }

    public Map<String, Object> createBlackDuckParametersMapFromJenkinsUI() {
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);

        Map<String, Object> blackDuckParametersFromJenkinsUI = new HashMap<>();

        try {
            blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_URL_KEY, config.getBlackDuckUrl().trim());
            blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, config.getBlackDuckApiToken().trim());
        } catch (Exception e) {
            blackDuckParametersFromJenkinsUI.clear();
        }
        return blackDuckParametersFromJenkinsUI;
    }
}
