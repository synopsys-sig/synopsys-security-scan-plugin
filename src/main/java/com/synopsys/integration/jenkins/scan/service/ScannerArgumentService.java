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
import com.synopsys.integration.jenkins.scan.strategy.ScanStrategy;
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
    private String inputJsonFilePath;

    public ScannerArgumentService(TaskListener listener, EnvVars envVars, FilePath workspace) {
        this.listener = listener;
        this.envVars = envVars;
        this.workspace = workspace;
    }

    public String getInputJsonFilePath() {
        return inputJsonFilePath;
    }

    public void setInputJsonFilePath(String inputJsonFilePath) {
        this.inputJsonFilePath = inputJsonFilePath;
    }

    public List<String> getCommandLineArgs(Map<String, Object> scanParameters, ScanStrategy scanStrategy, FilePath bridgeInstallationPath) throws ScannerJenkinsException {
        ScanType scanType = scanStrategy.getScanType();
        Object scanObject = scanStrategy.prepareScanInputForBridge(scanParameters);

        boolean fixPrOrPrComment = isFixPrOrPrCommentValueSet(scanObject);

        SCMRepositoryService scmRepositoryService = new SCMRepositoryService(listener, envVars);
        Object scmObject =  scmRepositoryService.fetchSCMRepositoryDetails(scanParameters, fixPrOrPrComment);

        List<String> commandLineArgs = new ArrayList<>(getInitialBridgeArgs(scanType, bridgeInstallationPath));
        commandLineArgs.add(createBridgeInputJson(scanObject, scmObject, fixPrOrPrComment));

        if (Objects.equals(scanParameters.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
            commandLineArgs.add(BridgeParams.DIAGNOSTICS_OPTION);
        }

        return commandLineArgs;
    }

    private List<String> getInitialBridgeArgs(ScanType scanType, FilePath bridgeInstallationPath) {
        List<String> initBridgeArgs = new ArrayList<>();
        String os = Utility.getAgentOs(workspace, listener);

        if(os.contains("win")) {
            initBridgeArgs.add(bridgeInstallationPath.child(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND_WINDOWS).getRemote());
        } else {
            initBridgeArgs.add(bridgeInstallationPath.child(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND).getRemote());
        }

        initBridgeArgs.add(BridgeParams.STAGE_OPTION);
        initBridgeArgs.add(getScanStage(scanType));
        initBridgeArgs.add(BridgeParams.INPUT_OPTION);

        return initBridgeArgs;
    }

    public String createBridgeInputJson(Object scanObject, Object scmObject, boolean fixPrOrPrComment) {
        BridgeInput bridgeInput = new BridgeInput();

        setScanObject(bridgeInput, scanObject, scmObject);

        if (fixPrOrPrComment) {
            setScmObject(bridgeInput, scmObject);
        }

        Map<String, Object> inputJsonMap = new HashMap<>();
        inputJsonMap.put(DATA_KEY, bridgeInput);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String jsonPath = null;
        try {
            String inputJson = mapper.writeValueAsString(inputJsonMap);
            jsonPath = writeInputJsonToFile(inputJson);
            setInputJsonFilePath(jsonPath);
        } catch (Exception e) {
            listener.getLogger().println("An exception occurred while creating input.json file: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }

        return jsonPath;
    }

    private void setScanObject(BridgeInput bridgeInput, Object scanObject, Object scmObject) {
        if (scanObject instanceof BlackDuck) {
            bridgeInput.setBlackDuck((BlackDuck) scanObject);
        } else if (scanObject instanceof Coverity) {
            Coverity coverity = (Coverity) scanObject;
            setCoverityProjectNameAndStreamName(coverity, scmObject);
            bridgeInput.setCoverity(coverity);
        } else if (scanObject instanceof Polaris) {
            Polaris polaris = (Polaris) scanObject;
            setPolarisProjectName(polaris, scmObject);
            bridgeInput.setPolaris((Polaris) scanObject);
        }
    }

    private void setPolarisProjectName(Polaris polaris , Object scmObject) {
        String repositoryName = getRepositoryName(scmObject);

        if (Utility.isStringNullOrBlank(polaris.getProjectName().getName())) {
            polaris.getProjectName().setName(repositoryName);
        }
    }
    private void setCoverityProjectNameAndStreamName(Coverity coverity, Object scmObject) {
        String repositoryName = getRepositoryName(scmObject);
        String branchName = envVars.get("BRANCH_NAME");

        if (Utility.isStringNullOrBlank(coverity.getConnect().getProject().getName())) {
            coverity.getConnect().getProject().setName(repositoryName);
        }
        if (Utility.isStringNullOrBlank(coverity.getConnect().getStream().getName())) {
            coverity.getConnect().getStream().setName(repositoryName.concat("-").concat(branchName));
        }
    }

    private String getRepositoryName(Object scmObject) {
        if (scmObject instanceof Bitbucket) {
            Bitbucket bitbucket = (Bitbucket) scmObject;
            return bitbucket.getProject().getRepository().getName();
        }
        return "";
    }

    private void setScmObject(BridgeInput bridgeInput, Object scmObject) {
        if (scmObject instanceof Bitbucket) {
            bridgeInput.setBitbucket((Bitbucket) scmObject);
        }
    }

    public String writeInputJsonToFile(String inputJson) {
        String inputJsonPath = null;

        try {
            FilePath tempFile = workspace.createTempFile("input", ".json");
            tempFile.write(inputJson, StandardCharsets.UTF_8.name());
            inputJsonPath = tempFile.getRemote();
        } catch (Exception e) {
            listener.getLogger().println("An exception occurred while writing into input.json file: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }

        return inputJsonPath;
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

    private boolean isFixPrOrPrCommentValueSet(Object scanObject) {
        if (scanObject instanceof BlackDuck) {
            BlackDuck blackDuck = (BlackDuck) scanObject;
            return blackDuck.getAutomation().getFixpr() || blackDuck.getAutomation().getPrComment();
        } else if (scanObject instanceof Coverity) {
            Coverity coverity = (Coverity) scanObject;
            return coverity.getAutomation().getPrComment();
        }
        return false;
    }
}
