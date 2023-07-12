package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.service.scm.BitbucketRepositoryService;
import hudson.EnvVars;
import hudson.model.TaskListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerArgumentService {

    private final TaskListener listener;
    private final EnvVars envVars;
    private static final String DATA_KEY = "data";
    private String blackDuckInputJsonFilePath;

    public ScannerArgumentService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }


    public String getBlackDuckInputJsonFilePath() {
        return blackDuckInputJsonFilePath;
    }

    public void setBlackDuckInputJsonFilePath(String blackDuckInputJsonFilePath) {
        this.blackDuckInputJsonFilePath = blackDuckInputJsonFilePath;
    }

    public List<String> getCommandLineArgs(Map<String, Object> scanParameters) {
        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(BridgeParams.BLACKDUCK_STAGE));

        BitbucketRepositoryService bitBucketRepositoryService = new BitbucketRepositoryService(listener, envVars);
        Bitbucket bitBucket = bitBucketRepositoryService.fetchBitbucketRepoDetails(scanParameters);

        BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService();
        BlackDuck blackDuck = blackDuckParametersService.prepareBlackDuckInputForBridge(scanParameters);
        // added the Prcomment and Fixpr condition to create BlackDuck Input Json on the following code
        commandLineArgs.add(createBlackDuckInputJson( blackDuck, blackDuck.getAutomation().getPrcomment() || blackDuck.getAutomation().getFixpr() ? bitBucket : null));

        return commandLineArgs;
    }

    public String createBlackDuckInputJson(BlackDuck blackDuck, Object scm) {
        BridgeInput bridgeInput = new BridgeInput();
        bridgeInput.setBlackDuck(blackDuck);
        if (scm instanceof Bitbucket) {
            Bitbucket bitbucket = (Bitbucket) scm;
            bridgeInput.setBitbucket(bitbucket);
        }

        Map<String, Object> blackDuckJsonMap = new HashMap<>();
        blackDuckJsonMap.put(DATA_KEY, bridgeInput);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String jsonPath = null;
        try {
            String blackDuckJson = mapper.writeValueAsString(blackDuckJsonMap);
            jsonPath = writeBlackDuckJsonToFile(blackDuckJson);
            setBlackDuckInputJsonFilePath(jsonPath);
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
}
