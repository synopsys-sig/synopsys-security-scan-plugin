package com.synopsys.integration.jenkins.scan.validation;

import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import jenkins.model.GlobalConfiguration;

import java.util.*;
import java.util.stream.Stream;

public class BlackDuckParametersValidation {

    public boolean validateBlackDuckParameters(String blackDuckArguments) {
        List<String> parsedBlackDuckParameters = parseBlackDuckParameters(blackDuckArguments);

        Map<String, String> blackDuckParametersMapFromPipeline = createBlackDuckParametersMap(parsedBlackDuckParameters);
        Map<String, String> blackDuckParametersMapFromsUI = blackDuckParametersFromJenkinsUI();

        Map<String, String> blackDuckParametersMap = combineBlackDuckParameters(blackDuckParametersMapFromPipeline, blackDuckParametersMapFromsUI);

        return blackDuckParametersMap != null
                && Stream.of(ApplicationConstants.BLACKDUCK_URL_KEY, ApplicationConstants.BLACKDUCK_API_TOKEN_KEY)
                .allMatch(key -> blackDuckParametersMap.containsKey(key)
                        && blackDuckParametersMap.get(key) != null
                        && !blackDuckParametersMap.get(key).isEmpty());
    }

    public Map<String, String> combineBlackDuckParameters(Map<String, String> blackDuckParametersMapFromPipeline, Map<String, String> blackDuckParametersMapFromsUI) {

        for (Map.Entry<String, String> entry : blackDuckParametersMapFromsUI.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            //Giving precedence the pipeline arguments. Therefore, if same key-value occurs pipeline's value will be taken.
            if (!blackDuckParametersMapFromPipeline.containsKey(key)) {
                blackDuckParametersMapFromPipeline.put(key, value);
            }
        }
        return blackDuckParametersMapFromPipeline;
    }

    public Map<String, String> blackDuckParametersFromJenkinsUI() {
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);

        Map<String, String> blackDuckParametersFromJenkinsUI = new HashMap<>();

        blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_URL_KEY, config.getBlackDuckUrl().trim());
        blackDuckParametersFromJenkinsUI.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY,config.getBlackDuckCredentialsId().trim());

        return blackDuckParametersFromJenkinsUI;
    }

    private List<String> parseBlackDuckParameters(String blackDuckArguments) {
        if (blackDuckArguments == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(blackDuckArguments.trim().split("\\s+"));
    }

    public Map<String, String> createBlackDuckParametersMap(List<String> parsedParameters) {
        Map<String, String> parameterMap = new HashMap<>();

        for (String parameter : parsedParameters) {
            String[] keyValue = parameter.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].substring(2).trim().toLowerCase();
                String value = keyValue[1].trim().toLowerCase();
                parameterMap.put(key, value);
            }
        }

        return parameterMap;
    }
}
