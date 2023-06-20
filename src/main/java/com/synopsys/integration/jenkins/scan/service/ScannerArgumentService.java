package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.Blackduck;

import hudson.FilePath;

import java.util.HashMap;
import jenkins.model.GlobalConfiguration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author akib @Date 6/19/23
 */
public class ScannerArgumentService {

    public List<String> getCommandLineArgs(FilePath workspace, String blackduckArgs) throws IOException {
        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(BridgeParams.BLACKDUCK_STAGE));
        commandLineArgs.add(createBlackDuckInputJson(workspace, blackduckArgs));
        return commandLineArgs;
    }

    private String createBlackDuckInputJson(FilePath workspace, String blackduckArgs) throws IOException {
        Blackduck blackduck = new Blackduck();
        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);
        blackduck.setUrl(config.getBlackDuckUrl());
        blackduck.setToken(config.getBlackDuckCredentialsId());

        Map<String, Object> data = new HashMap<>();
        data.put("blackduck", blackduck);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("data", data);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String blackduckJson = mapper.writeValueAsString(jsonMap);
        String jsonPath = workspace.getRemote() + "/" + BridgeParams.BLACKDUCK_STATE_FILE_NAME;

        try {
            FileWriter fileWriter = new FileWriter(jsonPath);
            fileWriter.write(blackduckJson);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BridgeParams.BLACKDUCK_STATE_FILE_NAME;
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
