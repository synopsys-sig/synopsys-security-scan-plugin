package com.synopsys.integration.jenkins.scan.service.scan.blackduck;

import static org.junit.jupiter.api.Assertions.*;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.blackduck.BlackDuck;
import hudson.model.TaskListener;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BlackDuckParametersServiceTest {
    private BlackDuckParametersService blackDuckParametersService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final String TEST_BLACKDUCK_URL = "https://fake.blackduck.url";
    private final String TEST_BLACKDUCK_TOKEN = "MDJDSROSVC56FAKEKEY";
    private final String TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH = "/path/to/blackduck/directory";
    
    @BeforeEach
    void setUp() {
        blackDuckParametersService = new BlackDuckParametersService(listenerMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    void createBlackDuckObjectTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();

        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_INSTALL_DIRECTORY_KEY, TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH);

        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_FIXPR_KEY, true);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, false);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_SCAN_FULL_KEY, true);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY, "BLOCKER, CRITICAL, MAJOR, MINOR");

        BlackDuck blackDuck = blackDuckParametersService.prepareBlackDuckObjectForBridge(blackDuckParametersMap);

        assertEquals(TEST_BLACKDUCK_URL, blackDuck.getUrl());
        assertEquals(TEST_BLACKDUCK_TOKEN, blackDuck.getToken());
        assertEquals(TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH, blackDuck.getInstall().getDirectory());

        assertEquals(true, blackDuck.getAutomation().getFixpr());
        assertEquals(false, blackDuck.getAutomation().getPrComment());
        assertEquals(true, blackDuck.getScan().getFull());
        assertEquals(List.of("BLOCKER", "CRITICAL", "MAJOR", "MINOR"), blackDuck.getScan().getFailure().getSeverities());
    }

    @Test
    void validateBlackDuckParametersForValidParametersTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        
        assertTrue(blackDuckParametersService.isValidBlackDuckParameters(blackDuckParametersMap));
    }

    @Test
    void validateBlackDuckParametersForMissingParametersTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        
        assertFalse(blackDuckParametersService.isValidBlackDuckParameters(blackDuckParametersMap));
    }

    @Test
    void validateBlackDuckParametersForNullAndEmptyTest() {
        assertFalse(blackDuckParametersService.isValidBlackDuckParameters(null));

        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, "");
        blackDuckParametersMap.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        
        assertFalse(blackDuckParametersService.isValidBlackDuckParameters(blackDuckParametersMap));
    }

}