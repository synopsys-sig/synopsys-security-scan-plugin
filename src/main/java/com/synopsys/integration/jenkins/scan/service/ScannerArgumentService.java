package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;
import com.synopsys.integration.jenkins.scan.input.coverity.Coverity;
import com.synopsys.integration.jenkins.scan.input.polaris.Polaris;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.service.scan.ScanStrategyService;
import com.synopsys.integration.jenkins.scan.service.scm.SCMRepositoryService;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScannerArgumentService {
    private final TaskListener listener;
    private final EnvVars envVars;
    private final FilePath workspace;
    private static final String DATA_KEY = "data";
    private String blackDuckInputJsonFilePath;

    public ScannerArgumentService(TaskListener listener, EnvVars envVars, FilePath workspace) {
        this.listener = listener;
        this.envVars = envVars;
        this.workspace = workspace;
    }

    public String getBlackDuckInputJsonFilePath() {
        return blackDuckInputJsonFilePath;
    }

    public void setBlackDuckInputJsonFilePath(String blackDuckInputJsonFilePath) {
        this.blackDuckInputJsonFilePath = blackDuckInputJsonFilePath;
    }

    public List<String> getCommandLineArgs(Map<String, Object> scanParameters, ScanStrategyService scanStrategyService, String bridgeInstallationPath) throws ScannerJenkinsException {
        ScanType scanType = scanStrategyService.getScanType();
        Object scanObject = scanStrategyService.prepareScanInputForBridge(scanParameters);

        SCMRepositoryService scmRepositoryService = new SCMRepositoryService(listener, envVars);
        Object scmObject =  scmRepositoryService.fetchSCMRepositoryDetails(scanParameters);

        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(scanType, bridgeInstallationPath));
        commandLineArgs.add(createBridgeInputJson(scanObject, scmObject));

        if (Objects.equals(scanParameters.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
            commandLineArgs.add(BridgeParams.DIAGNOSTICS_OPTION);
        }

        return commandLineArgs;
    }

    public String createBridgeInputJson(Object scanObject, Object scmObject) {
        BridgeInput bridgeInput = new BridgeInput();
        
        setScanObject(bridgeInput, scanObject);
        setScmObject(bridgeInput, scmObject);

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
            listener.getLogger().println("An exception occurred while creating blackduck_input.json file: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }

        return jsonPath;
    }
    
    private void setScanObject(BridgeInput bridgeInput, Object scanObject) {
        if (scanObject instanceof BlackDuck) {
            bridgeInput.setBlackDuck((BlackDuck) scanObject);
        } else if (scanObject instanceof Coverity) {
            bridgeInput.setCoverity((Coverity) scanObject);
        } else if (scanObject instanceof Polaris) {
            bridgeInput.setPolaris((Polaris) scanObject);
        }
    }

    private void setScmObject(BridgeInput bridgeInput, Object scmObject) {
        if (scmObject instanceof Bitbucket) {
            bridgeInput.setBitbucket((Bitbucket) scmObject);
        }
    }

    public String writeBlackDuckJsonToFile(String blackDuckJson) {
        String blackDuckInputJsonPath = null;

        try {
            FilePath tempFile = workspace.createTempFile("blackduck_input", ".json");
            tempFile.write(blackDuckJson, StandardCharsets.UTF_8.name());
            blackDuckInputJsonPath = tempFile.getRemote();
        } catch (Exception e) {
            listener.getLogger().println("An exception occurred while writing into blackduck_input.json file: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }

        return blackDuckInputJsonPath;
    }

    public List<String> getInitialBridgeArgs(ScanType scanType, String bridgeInstallationPath) {
        List<String> initBridgeArgs = new ArrayList<>();
        String os = Utility.getAgentOs(workspace, listener);

        if(os.contains("win")) {
            initBridgeArgs.add(String.join("\\", bridgeInstallationPath, ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND_WINDOWS));
        } else {
            initBridgeArgs.add(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND);
        }

        initBridgeArgs.add(BridgeParams.STAGE_OPTION);
        initBridgeArgs.add(getScanStage(scanType));
        initBridgeArgs.add(BridgeParams.INPUT_OPTION);

        return initBridgeArgs;
    }

    private String getScanStage(ScanType scanType) {
        if (scanType.equals(ScanType.COVERITY)) {
            return BridgeParams.COVERITY_STAGE;
        } else if (scanType.equals(ScanType.POLARIS)) {
            return BridgeParams.POLARIS_STAGE;
        } else {
            return BridgeParams.BLACKDUCK_STAGE;
        }
    }

}
