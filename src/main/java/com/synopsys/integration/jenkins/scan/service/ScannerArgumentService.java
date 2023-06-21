package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.Blackduck;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;

import hudson.FilePath;

import jenkins.model.GlobalConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author akib @Date 6/19/23
 */
public class ScannerArgumentService {
    private static final String DATA_KEY = "data";

    public List<String> getCommandLineArgs(FilePath workspace, String blackduckArgs) throws IOException {
        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(BridgeParams.BLACKDUCK_STAGE));
        commandLineArgs.add(createBlackDuckInputJson(workspace, blackduckArgs));

        return commandLineArgs;
    }

    private String createBlackDuckInputJson(FilePath workspace, String blackduckArgs) throws IOException {
        Blackduck blackduck = new Blackduck();
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);

        blackduck.setUrl(config.getBlackDuckUrl().trim());
        blackduck.setToken(config.getBlackDuckCredentialsId().trim());

        BridgeInput bridgeInput = new BridgeInput();
        bridgeInput.setBlackduck(blackduck);

        Map<String, Object> blackduckJsonMap = new HashMap<>();
        blackduckJsonMap.put(DATA_KEY, bridgeInput);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String blackduckJson = mapper.writeValueAsString(blackduckJsonMap);
        String jsonPath = workspace.getRemote().concat("/").concat(BridgeParams.BLACKDUCK_STATE_FILE_NAME);

        writeBlackduckJsonToFile(jsonPath, blackduckJson);

        return BridgeParams.BLACKDUCK_STATE_FILE_NAME;
    }

    public void writeBlackduckJsonToFile(String jsonPath, String blackduckJson) {
        try (FileWriter fileWriter = new FileWriter(jsonPath)) {
            fileWriter.write(blackduckJson);
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
    
}
