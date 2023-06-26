package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;

import hudson.FilePath;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerArgumentService {
    private static final String DATA_KEY = "data";

    public List<String> getCommandLineArgs(FilePath workspace, Map<String, Object> scanParams) throws IOException {
        String stageName = getStageType(scanParams);
        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(stageName));

        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService();

        if (stageName.equals(BridgeParams.BLACKDUCK_STAGE)) {
            BlackDuck blackDuck = blackDuckParametersService.prepareBlackDuckInputForBridge(scanParams);
            commandLineArgs.add(createBlackDuckInputJson(workspace, blackDuck));
        }

        return commandLineArgs;
    }

    public String createBlackDuckInputJson(FilePath workspace, BlackDuck blackDuck) throws IOException {
        BridgeInput bridgeInput = new BridgeInput();
        bridgeInput.setBlackDuck(blackDuck);

        Map<String, Object> blackDuckJsonMap = new HashMap<>();
        blackDuckJsonMap.put(DATA_KEY, bridgeInput);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String blackDuckJson = mapper.writeValueAsString(blackDuckJsonMap);
        String jsonPath = workspace.getRemote().concat("/").concat(BridgeParams.BLACKDUCK_JSON_FILE_NAME);

        writeBlackDuckJsonToFile(jsonPath, blackDuckJson);

        return BridgeParams.BLACKDUCK_JSON_FILE_NAME;
    }

    public void writeBlackDuckJsonToFile(String jsonPath, String blackDuckJson) {
        try (FileWriter fileWriter = new FileWriter(jsonPath)) {
            fileWriter.write(blackDuckJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getInitialBridgeArgs(String stage) {
        List<String> initBridgeArgs = new ArrayList<>();
        initBridgeArgs.add(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND);
        initBridgeArgs.add(BridgeParams.STAGE_OPTION);
        initBridgeArgs.add(stage);
        initBridgeArgs.add(BridgeParams.INPUT_OPTION);

        return initBridgeArgs;
    }

    public String getStageType(Map<String, Object> scanParams) {
        String params = scanParams.toString();
        if (params.contains(BridgeParams.COVERITY_STAGE)) {
            return BridgeParams.COVERITY_STAGE;
        } else if (params.contains(BridgeParams.POLARIS_STAGE)) {
            return BridgeParams.POLARIS_STAGE;
        } else {
            return BridgeParams.BLACKDUCK_STAGE;
        }
    }
    
}
