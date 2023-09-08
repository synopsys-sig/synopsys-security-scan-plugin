package com.synopsys.integration.jenkins.scan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.synopsys.integration.jenkins.scan.exception.ScannerJenkinsException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import com.synopsys.integration.jenkins.scan.input.coverity.Coverity;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ScannerArgumentServiceTest {
    private Bitbucket bitBucket;
    private ScannerArgumentService scannerArgumentService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final EnvVars envVarsMock = Mockito.mock(EnvVars.class);
    private FilePath workspace;

    @BeforeEach
    void setUp() {
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
        workspace = new FilePath(new File(getHomeDirectoryForTest())).child("mock-workspace");

        bitBucket = new Bitbucket();
        bitBucket.getProject().getRepository().setName("fake-repo");

        Mockito.doReturn("fake-branch").when(envVarsMock).get(ApplicationConstants.ENV_BRANCH_NAME_KEY);
        Mockito.doReturn("fake-job/branch").when(envVarsMock).get(ApplicationConstants.ENV_JOB_NAME_KEY);
        Mockito.doReturn("0").when(envVarsMock).get(ApplicationConstants.ENV_CHANGE_ID_KEY);

        scannerArgumentService = new ScannerArgumentService(listenerMock, envVarsMock, workspace);
    }

    @Test
    void createBlackDuckInputJsonTest() {
        BlackDuck blackDuck = new BlackDuck();
        blackDuck.setUrl("https://fake.blackduck.url");
        blackDuck.setToken("MDJDSROSVC56FAKEKEY");
        
        String inputJsonPath = scannerArgumentService.createBridgeInputJson(blackDuck, bitBucket, false, ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX);
        Path filePath = Paths.get(inputJsonPath);

        assertTrue(Files.exists(filePath), String.format("File %s does not exist at the specified path.", ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX.concat(".json")));
        Utility.removeFile(filePath.toString(), workspace, listenerMock);
    }

    @Test
    void writeInputJsonToFileTest() {
        String jsonString = "{\"data\":{\"blackduck\":{\"url\":\"https://fake.blackduck.url\",\"token\":\"MDJDSROSVC56FAKEKEY\"}}}";

        String jsonPath = scannerArgumentService.writeInputJsonToFile(jsonString, ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX);
        String fileContent = null;
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(Files.exists(Path.of(jsonPath)), String.format("%s does not exist at the specified path.", ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX.concat(".json")));
        assertEquals(jsonString,fileContent);

        Utility.removeFile(jsonPath, workspace, listenerMock);
    }

    @Test
    void createCoverityInputJsonTest() {
        Coverity coverity = new Coverity();
        coverity.getConnect().setUrl("https://fake.coverity.url");
        coverity.getConnect().getUser().setName("fake-user");
        coverity.getConnect().getUser().setPassword("fakeUserPassword");

        String inputJsonPath = scannerArgumentService.createBridgeInputJson(coverity, bitBucket, false, ApplicationConstants.COVERITY_INPUT_JSON_PREFIX);
        Path filePath = Paths.get(inputJsonPath);

        assertTrue(Files.exists(filePath), String.format("File %s does not exist at the specified path.", ApplicationConstants.COVERITY_INPUT_JSON_PREFIX.concat(".json")));
        Utility.removeFile(filePath.toString(), workspace, listenerMock);
    }

    @Test
    void getCommandLineArgsForBlackDuckTest() throws ScannerJenkinsException {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.SYNOPSYS_SECURITY_PLATFORM_KEY, "blackduck");
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, false);
        blackDuckParametersMap.put(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY, true);

        List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(blackDuckParametersMap, workspace);

        if(getOSNameForTest().contains("win")) {
            assertEquals(commandLineArgs.get(0), workspace.child(ApplicationConstants.BRIDGE_BINARY_WINDOWS).getRemote());
        } else {
            assertEquals(commandLineArgs.get(0), workspace.child(ApplicationConstants.BRIDGE_BINARY).getRemote());
        }
        assertEquals(commandLineArgs.get(1), BridgeParams.STAGE_OPTION);
        assertEquals(commandLineArgs.get(2), BridgeParams.BLACKDUCK_STAGE);
        assertNotEquals(commandLineArgs.get(2), BridgeParams.COVERITY_STAGE);
        assertNotEquals(commandLineArgs.get(2), BridgeParams.POLARIS_STAGE);
        assertEquals(commandLineArgs.get(3), BridgeParams.INPUT_OPTION);
        assertTrue(Files.exists(Path.of(commandLineArgs.get(4))), String.format("File %s does not exist at the specified path.", ApplicationConstants.BLACKDUCK_INPUT_JSON_PREFIX.concat(".json")));
        assertEquals(commandLineArgs.get(5), BridgeParams.DIAGNOSTICS_OPTION);

        Utility.removeFile(commandLineArgs.get(4), workspace, listenerMock);
    }

    @Test
    void getCommandLineArgsForCoverityTest() throws ScannerJenkinsException {
        Map<String, Object> coverityParameters = new HashMap<>();
        coverityParameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PLATFORM_KEY, "coverity");
        coverityParameters.put(ApplicationConstants.BRIDGE_COVERITY_CONNECT_URL_KEY, "https://fake.coverity.url");
        coverityParameters.put(ApplicationConstants.BRIDGE_COVERITY_CONNECT_USER_NAME_KEY, "fake-user");
        coverityParameters.put(ApplicationConstants.BRIDGE_COVERITY_CONNECT_USER_PASSWORD_KEY, "fakeUserPassword");
        coverityParameters.put(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY, true);

        List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(coverityParameters, workspace);

        if(getOSNameForTest().contains("win")) {
            assertEquals(commandLineArgs.get(0), workspace.child(ApplicationConstants.BRIDGE_BINARY_WINDOWS).getRemote());
        } else {
            assertEquals(commandLineArgs.get(0), workspace.child(ApplicationConstants.BRIDGE_BINARY).getRemote());
        }
        assertEquals(commandLineArgs.get(1), BridgeParams.STAGE_OPTION);
        assertEquals(commandLineArgs.get(2), BridgeParams.COVERITY_STAGE);
        assertNotEquals(commandLineArgs.get(2), BridgeParams.POLARIS_STAGE);
        assertNotEquals(commandLineArgs.get(2), BridgeParams.BLACKDUCK_STAGE);
        assertEquals(commandLineArgs.get(3), BridgeParams.INPUT_OPTION);
        assertTrue(Files.exists(Path.of(commandLineArgs.get(4))), String.format("File %s does not exist at the specified path.", ApplicationConstants.COVERITY_INPUT_JSON_PREFIX.concat(".json")));
        assertEquals(commandLineArgs.get(5), BridgeParams.DIAGNOSTICS_OPTION);

        Utility.removeFile(commandLineArgs.get(4), workspace, listenerMock);
    }

    public String getHomeDirectoryForTest() {
        return System.getProperty("user.home");
    }

    public String getOSNameForTest() {
        return System.getProperty("os.name").toLowerCase();
    }
}
