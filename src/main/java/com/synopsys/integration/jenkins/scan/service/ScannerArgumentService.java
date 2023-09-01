/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.input.BridgeInput;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.coverity.Coverity;
import com.synopsys.integration.jenkins.scan.input.polaris.Polaris;
import com.synopsys.integration.jenkins.scan.service.scan.ScanParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.blackduck.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.coverity.CoverityParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.polaris.PolarisParametersService;
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
import java.util.Set;

public class ScannerArgumentService {
    private final TaskListener listener;
    private final EnvVars envVars;
    private final FilePath workspace;
    private static final String DATA_KEY = "data";
    private final LoggerWrapper logger;

    public ScannerArgumentService(TaskListener listener, EnvVars envVars, FilePath workspace) {
        this.listener = listener;
        this.envVars = envVars;
        this.workspace = workspace;
        this.logger = new LoggerWrapper(listener);
    }

    public List<String> getCommandLineArgs(Map<String, Object> scanParameters, FilePath bridgeInstallationPath) throws ScannerJenkinsException {
        List<String> commandLineArgs = new ArrayList<>();

        commandLineArgs.add(getBridgeRunCommand(bridgeInstallationPath));

        commandLineArgs.addAll(getScanTypeSpecificCommands(scanParameters));

        if (Objects.equals(scanParameters.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
            commandLineArgs.add(BridgeParams.DIAGNOSTICS_OPTION);
        }

        return commandLineArgs;
    }

    private String getBridgeRunCommand(FilePath bridgeInstallationPath) {
        String os = Utility.getAgentOs(workspace, listener);

        if(os.contains("win")) {
            return bridgeInstallationPath.child(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND_WINDOWS).getRemote();
        } else {
            return bridgeInstallationPath.child(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND).getRemote();
        }
    }

    private List<String> getScanTypeSpecificCommands(Map<String, Object> scanParameters) throws ScannerJenkinsException {
        ScanParametersService scanParametersService = new ScanParametersService(listener);
        Set<String> scanTypes = scanParametersService.getScanTypes(scanParameters);

        boolean fixPrOrPrComment = isFixPrOrPrCommentValueSet(scanParameters);

        SCMRepositoryService scmRepositoryService = new SCMRepositoryService(listener, envVars);
        Object scmObject =  scmRepositoryService.fetchSCMRepositoryDetails(scanParameters, fixPrOrPrComment);

        List<String> scanCommands = new ArrayList<>();

        if (scanTypes.contains(ScanType.BLACKDUCK.name())) {
            BlackDuckParametersService blackDuckParametersService = new BlackDuckParametersService(listener);
            BlackDuck blackDuck = blackDuckParametersService.prepareBlackDuckObjectForBridge(scanParameters);

            scanCommands.add(BridgeParams.STAGE_OPTION);
            scanCommands.add(BridgeParams.BLACKDUCK_STAGE);
            scanCommands.add(BridgeParams.INPUT_OPTION);
            scanCommands.add(createBridgeInputJson(blackDuck, scmObject, fixPrOrPrComment, ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX));
        }
        if (scanTypes.contains(ScanType.COVERITY.name())) {
            CoverityParametersService coverityParametersService = new CoverityParametersService(listener);
            Coverity coverity = coverityParametersService.prepareCoverityObjectForBridge(scanParameters);

            scanCommands.add(BridgeParams.STAGE_OPTION);
            scanCommands.add(BridgeParams.COVERITY_STAGE);
            scanCommands.add(BridgeParams.INPUT_OPTION);
            scanCommands.add(createBridgeInputJson(coverity, scmObject, fixPrOrPrComment, ApplicationConstants.COVERITY_INPUT_JSON_PREFIX));
        }
        if (scanTypes.contains(ScanType.POLARIS.name())) {
            PolarisParametersService polarisParametersService = new PolarisParametersService(listener);
            Polaris polaris = polarisParametersService.preparePolarisObjectForBridge(scanParameters);

            scanCommands.add(BridgeParams.STAGE_OPTION);
            scanCommands.add(BridgeParams.POLARIS_STAGE);
            scanCommands.add(BridgeParams.INPUT_OPTION);
            scanCommands.add(createBridgeInputJson(polaris, scmObject, fixPrOrPrComment, ApplicationConstants.POLARIS_INPUT_JSON_PREFIX));
        }

        return scanCommands;
    }

    public String createBridgeInputJson(Object scanObject, Object scmObject, boolean fixPrOrPrComment, String jsonPrefix) {
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
            jsonPath = writeInputJsonToFile(inputJson, jsonPrefix);
        } catch (Exception e) {
            logger.error("An exception occurred while creating input.json file: " + e.getMessage());
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
            bridgeInput.setPolaris((Polaris) scanObject);
        }
    }

    private void setCoverityProjectNameAndStreamName(Coverity coverity, Object scmObject) {
        String repositoryName = getRepositoryName(scmObject);
        String branchName = envVars.get(ApplicationConstants.ENV_BRANCH_NAME_KEY);

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

    public String writeInputJsonToFile(String inputJson, String jsonPrefix) {
        String inputJsonPath = null;

        try {
            FilePath tempFile = workspace.createTempFile(jsonPrefix, ".json");
            tempFile.write(inputJson, StandardCharsets.UTF_8.name());
            inputJsonPath = tempFile.getRemote();
        } catch (Exception e) {
            logger.error("An exception occurred while writing into input.json file: " + e.getMessage());
        }

        return inputJsonPath;
    }

    private boolean isFixPrOrPrCommentValueSet(Map<String, Object> scanParameters) {
        if (scanParameters.containsKey(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_FIXPR_KEY) &&
            Objects.equals(scanParameters.get(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_FIXPR_KEY), true)) {
            return true;
        } else if (scanParameters.containsKey(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY) &&
            Objects.equals(scanParameters.get(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY), true)) {
            return true;
        } else if (scanParameters.containsKey(ApplicationConstants.BRIDGE_COVERITY_AUTOMATION_PRCOMMENT_KEY) &&
            Objects.equals(scanParameters.get(ApplicationConstants.BRIDGE_COVERITY_AUTOMATION_PRCOMMENT_KEY), true)) {
            return true;
        }
        return false;
    }

    public void removeTemporaryInputJson(List<String> commandLineArgs) {
        for (String arg : commandLineArgs) {
            if (arg.endsWith(".json")) {
                Utility.removeFile(arg, workspace, listener);
            }
        }
    }
}
