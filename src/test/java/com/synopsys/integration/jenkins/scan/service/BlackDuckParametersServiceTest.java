package com.synopsys.integration.jenkins.scan.service;

import static org.junit.jupiter.api.Assertions.*;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackDuckParametersServiceTest {
    private BlackDuckParametersService blackDuckParametersService;
    private final String TEST_BLACKDUCK_URL = "https://fake.blackduck.url";
    private final String TEST_BLACKDUCK_TOKEN = "MDJDSROSVC56FAKEKEY";
    private final String TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH = "/path/to/blackduck/directory";
    
    @BeforeEach
    void setUp() {
        blackDuckParametersService = new BlackDuckParametersService();
    }

    @Test
    void createBlackDuckObjectTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();

        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY, TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH);

        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, "true");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, "false");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_SCAN_FULL_KEY, "true");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY, "BLOCKER, CRITICAL, MAJOR, MINOR");


        BlackDuck blackDuck = blackDuckParametersService.createBlackDuckObject(blackDuckParametersMap);

        assertEquals(TEST_BLACKDUCK_URL, blackDuck.getUrl());
        assertEquals(TEST_BLACKDUCK_TOKEN, blackDuck.getToken());
        assertEquals(TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH, blackDuck.getInstallDirectory());

        assertEquals(true, blackDuck.getAutomation().getFixpr());
        assertEquals(false, blackDuck.getAutomation().getPrComment());
        assertEquals(true, blackDuck.getScan().getFull());
        assertEquals(List.of("BLOCKER", "CRITICAL", "MAJOR", "MINOR"), blackDuck.getScan().getFailureSeverities());
    }

    @Test
    void validateBlackDuckParametersForValidParametersTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        
        assertTrue(blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParametersMap));
    }

    @Test
    void validateBlackDuckParametersForMissingParametersTest() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);

        assertFalse(blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParametersMap));
    }

    @Test
    void validateBlackDuckParametersForNullAndEmptyTest() {
        BlackDuckParametersService service = new BlackDuckParametersService();
        assertFalse(service.performBlackDuckParameterValidation(null));

        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);

        assertFalse(blackDuckParametersService.performBlackDuckParameterValidation(blackDuckParametersMap));
    }

    @Test
    public void getCombinedBlackDuckParametersTest() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        expectedMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);

        Map<String, Object> pipelineMapWithUrl = new HashMap<>();
        pipelineMapWithUrl.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);

        Map<String, Object> uiMapWithToken = new HashMap<>();
        uiMapWithToken.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);

        Map<String, Object> combinedMap = blackDuckParametersService.getCombinedBlackDuckParameters(pipelineMapWithUrl, uiMapWithToken);

        assertEquals(expectedMap, combinedMap);
        assertEquals(2, expectedMap.size());

        combinedMap.clear();
        expectedMap.clear();

        Map<String, Object> pipelineMapNull = null;
        Map<String, Object> uiMapNull = null;

        combinedMap = blackDuckParametersService.getCombinedBlackDuckParameters(pipelineMapNull, uiMapNull);

        assertNull(combinedMap);
    }

}