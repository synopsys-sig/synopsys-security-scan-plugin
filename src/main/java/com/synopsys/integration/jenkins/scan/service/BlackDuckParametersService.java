package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import jenkins.model.GlobalConfiguration;

import java.util.*;
import java.util.stream.Stream;

public class BlackDuckParametersService {

    public BlackDuck prepareBlackDuckInputForBridge(String blackDuckArguments) {
        Map<String, String> blackDuckParametersMap = getCombinedBlackDuckParameters(blackDuckArguments);
        return createBlackDuckObject(blackDuckParametersMap);
    }

    private BlackDuck createBlackDuckObject(Map<String, String> blackDuckParametersMap) {
        BlackDuck blackDuck = new BlackDuck();

        for (Map.Entry<String, String> entry : blackDuckParametersMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().trim();

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
                        blackDuck.getScan().setFullScan(Boolean.parseBoolean(value));
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
                        blackDuck.getAutomation().setFixPr(Boolean.parseBoolean(value));
                    }
                    break;
                case ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY:
                    if (value.equals("true") || value.equals("false")) {
                        blackDuck.getAutomation().setPrComment(Boolean.parseBoolean(value));
                    }
                    break;
            }
        }

        return blackDuck;
    }

    public boolean validateBlackDuckParameters(String blackDuckArguments) {
        Map<String, String> blackDuckParametersMap = getCombinedBlackDuckParameters(blackDuckArguments);

        return blackDuckParametersMap != null
                && Stream.of(ApplicationConstants.BLACKDUCK_URL_KEY, ApplicationConstants.BLACKDUCK_API_TOKEN_KEY)
                .allMatch(key -> blackDuckParametersMap.containsKey(key)
                        && blackDuckParametersMap.get(key) != null
                        && !blackDuckParametersMap.get(key).isEmpty());
    }

    public Map<String, String> getCombinedBlackDuckParameters(String blackDuckArguments) {
        List<String> parsedBlackDuckParameters = parseBlackDuckParameters(blackDuckArguments);

        Map<String, String> blackDuckParametersMapFromPipeline = createBlackDuckParametersMapFromPipeline(parsedBlackDuckParameters);
        Map<String, String> blackDuckParametersMapFromUI = createBlackDuckParametersMapFromJenkinsUI();

        for (Map.Entry<String, String> entry : blackDuckParametersMapFromUI.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            //Giving precedence to the pipeline arguments. Therefore, if same key-value occurs, pipeline's value will be taken.
            if (!blackDuckParametersMapFromPipeline.containsKey(key)) {
                blackDuckParametersMapFromPipeline.put(key, value);
            }
        }
        return blackDuckParametersMapFromPipeline;
    }

    public Map<String, String> createBlackDuckParametersMapFromJenkinsUI() {
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);

        Map<String, String> blackDuckParametersFromJenkinsUI = new HashMap<>();

        blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_URL_KEY, config.getBlackDuckUrl().trim());
        blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY,config.getBlackDuckCredentialsId().trim());

        return blackDuckParametersFromJenkinsUI;
    }

    private List<String> parseBlackDuckParameters(String blackDuckArguments) {
        if (blackDuckArguments == null || blackDuckArguments.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.asList(blackDuckArguments.trim().split("\\s+"));
    }

    public Map<String, String> createBlackDuckParametersMapFromPipeline(List<String> parsedParameters) {
        Map<String, String> parameterMap = new HashMap<>();

        for (String parameter : parsedParameters) {
            String[] keyValue = parameter.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].substring(2).trim().toLowerCase();
                String value = keyValue[1].trim();
                parameterMap.put(key, value);
            }
        }

        return parameterMap;
    }
}
