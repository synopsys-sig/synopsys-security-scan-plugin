package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerArgumentService {
    private static final String DATA_KEY = "data";

    public List<String> getCommandLineArgs(Map<String, Object> scanParameters) {
        String stageName = getStageType(scanParameters);
        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(stageName));

        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService();

        if (stageName.equals(BridgeParams.BLACKDUCK_STAGE)) {
            BlackDuck blackDuck = blackDuckParametersService.prepareBlackDuckInputForBridge(scanParams);
            commandLineArgs.add(createBlackDuckInputJson(blackDuck));
        }

        return commandLineArgs;
    }

    public String createBlackDuckInputJson(BlackDuck blackDuck) {
        BridgeInput bridgeInput = new BridgeInput();
        bridgeInput.setBlackDuck(blackDuck);

        Map<String, Object> blackDuckJsonMap = new HashMap<>();
        blackDuckJsonMap.put(DATA_KEY, bridgeInput);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String jsonPath = null;
        try {
            String blackDuckJson = mapper.writeValueAsString(blackDuckJsonMap);
            jsonPath = writeBlackDuckJsonToFile(blackDuckJson);
            ApplicationConstants.BLACKDUCK_INPUT_JSON_PATH = jsonPath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonPath;
    }

    public String writeBlackDuckJsonToFile(String blackDuckJson) {
        String blackDuckInputJsonPath = null;

        try {
            Path tempFilePath = Files.createTempFile("blackduck_input", ".json");
            Files.writeString(tempFilePath, blackDuckJson);
            blackDuckInputJsonPath = tempFilePath.toAbsolutePath().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return blackDuckInputJsonPath;
    }

    public static List<String> getInitialBridgeArgs(String stage) {
        List<String> initBridgeArgs = new ArrayList<>();
        initBridgeArgs.add(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND);
        initBridgeArgs.add(BridgeParams.STAGE_OPTION);
        initBridgeArgs.add(stage);
        initBridgeArgs.add(BridgeParams.INPUT_OPTION);

        return initBridgeArgs;
    }

    public String getStageType(Map<String, Object> scanParameters) {
        String params = scanParameters.toString();
        if (params.contains(BridgeParams.COVERITY_STAGE)) {
            return BridgeParams.COVERITY_STAGE;
        } else if (params.contains(BridgeParams.POLARIS_STAGE)) {
            return BridgeParams.POLARIS_STAGE;
        } else {
            return BridgeParams.BLACKDUCK_STAGE;
        }
    }
}
