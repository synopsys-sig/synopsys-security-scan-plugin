package com.synopsys.integration.jenkins.scan.service;

import static org.junit.jupiter.api.Assertions.*;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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
    void testCreateBlackDuckObject() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();

        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY, TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH);

        //TODO: add Scan and Automation related unit tests.
        //blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, "true");
        //blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, "false");


        BlackDuck blackDuck = blackDuckParametersService.createBlackDuckObject(blackDuckParametersMap);

        assertEquals(TEST_BLACKDUCK_URL, blackDuck.getUrl());
        assertEquals(TEST_BLACKDUCK_TOKEN, blackDuck.getToken());
        assertEquals(TEST_BLACKDUCK_INSTALL_DIRECTORY_PATH, blackDuck.getInstallDirectory());

        //TODO: add Scan and Automation related unit tests.
        //assertEquals(true, blackDuck.getAutomation().isFixPr());
        //assertEquals(false, blackDuck.getAutomation().isPrComment());
    }

    @Test
    void testValidateBlackDuckParametersForValidParameters() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);
        
        assertTrue(blackDuckParametersService.performParameterValidation(blackDuckParametersMap));
    }

    @Test
    void testValidateBlackDuckParametersForMissingParameters() {
        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, TEST_BLACKDUCK_URL);

        assertFalse(blackDuckParametersService.performParameterValidation(blackDuckParametersMap));
    }

    @Test
    void testValidateBlackDuckParametersForNullAndEmpty() {
        BlackDuckParametersService service = new BlackDuckParametersService();
        assertFalse(service.performParameterValidation(null));

        Map<String, Object> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, TEST_BLACKDUCK_TOKEN);

        assertFalse(blackDuckParametersService.performParameterValidation(blackDuckParametersMap));
    }

    @Test
    public void testGetCombinedBlackDuckParameters() {
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

        assertEquals(null, combinedMap);
    }

}