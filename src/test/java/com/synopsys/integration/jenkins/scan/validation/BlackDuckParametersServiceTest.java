package com.synopsys.integration.jenkins.scan.validation;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;
import com.synopsys.integration.jenkins.scan.service.BlackDuckParametersService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BlackDuckParametersServiceTest {
    private BlackDuckParametersService blackDuckParametersService;
    @BeforeEach
    void setUp() {
        blackDuckParametersService = new BlackDuckParametersService();
    }

    @Test
    void testCreateBlackDuckObject() {
        Map<String, String> blackDuckParametersMap = new HashMap<>();

        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY, "/path/to/blackduck/directory");

        //TODO: add Scan and Automation related unit tests.
        //blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, "true");
        //blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, "false");


        BlackDuck blackDuck = blackDuckParametersService.createBlackDuckObject(blackDuckParametersMap);

        assertEquals("https://fake.blackduck.url", blackDuck.getUrl());
        assertEquals("MDJDSROSVC56FAKEKEY", blackDuck.getToken());
        assertEquals("/path/to/blackduck/directory", blackDuck.getInstallDirectory());

        //TODO: add Scan and Automation related unit tests.
        //assertEquals(true, blackDuck.getAutomation().isFixPr());
        //assertEquals(false, blackDuck.getAutomation().isPrComment());
    }

    @Test
    void testValidateBlackDuckParametersForValidParameters() {
        Map<String, String> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");
        
        assertTrue(blackDuckParametersService.validateBlackDuckParameters(blackDuckParametersMap));
    }

    @Test
    void testValidateBlackDuckParametersForMissingParameters() {
        Map<String, String> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");

        assertFalse(blackDuckParametersService.validateBlackDuckParameters(blackDuckParametersMap));
    }

    @Test
    void testValidateBlackDuckParametersForNullAndEmpty() {
        BlackDuckParametersService service = new BlackDuckParametersService();
        assertFalse(service.validateBlackDuckParameters(null));

        Map<String, String> blackDuckParametersMap = new HashMap<>();
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "");
        blackDuckParametersMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertFalse(blackDuckParametersService.validateBlackDuckParameters(blackDuckParametersMap));
    }

    @Test
    void testParseBlackDuckParameters() {
        List<String> parsedParameters = blackDuckParametersService.parseBlackDuckParameters(null);
        assertEquals(Collections.emptyList(), parsedParameters);

        parsedParameters = blackDuckParametersService.parseBlackDuckParameters("");
        assertEquals(Collections.emptyList(), parsedParameters);

        String blackDuckArguments = String.join(" ",
                "--blackduck.url=https://fake.blackduck.url",
                "--blackduck.token=MDJDSROSVC56FAKEKEY",
                "--blackduck.install.directory=/path/to/blackduck/directory");

        List<String> expectedBlackDuckArguments = Arrays.asList(
                "--blackduck.url=https://fake.blackduck.url",
                "--blackduck.token=MDJDSROSVC56FAKEKEY",
                "--blackduck.install.directory=/path/to/blackduck/directory"
        );

        parsedParameters = blackDuckParametersService.parseBlackDuckParameters(blackDuckArguments);

        assertEquals(expectedBlackDuckArguments, parsedParameters);
        assertEquals(3, parsedParameters.size());
    }

    @Test
    void testCreateBlackDuckParametersMapFromPipeline() {
        List<String> parsedParameters = Collections.emptyList();
        Map<String, String> result = blackDuckParametersService.createBlackDuckParametersMapFromPipeline(parsedParameters);
        assertEquals(Collections.emptyMap(), result);

        parsedParameters = Arrays.asList(
                "--blackduck.url=https://fake.blackduck.url",
                "--blackduck.token=MDJDSROSVC56FAKEKEY");
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        expectedMap.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        result = blackDuckParametersService.createBlackDuckParametersMapFromPipeline(parsedParameters);

        assertEquals(expectedMap, result);
        assertEquals(2, expectedMap.size());
    }

    @Test
    public void testGetCombinedBlackDuckParameters() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("blackduck.url", "https://fake.blackduck.url");
        expectedMap.put("blackduck.token", "MDJDSROSVC56FAKEKEY");

        Map<String, String> pipelineMapWithUrl = new HashMap<>();
        pipelineMapWithUrl.put("blackduck.url", "https://fake.blackduck.url");

        Map<String, String> uiMapWithToken = new HashMap<>();
        uiMapWithToken.put("blackduck.token", "MDJDSROSVC56FAKEKEY");

        Map<String, String> combinedMap = blackDuckParametersService.getCombinedBlackDuckParameters(pipelineMapWithUrl, uiMapWithToken);

        assertEquals(expectedMap, combinedMap);
        assertEquals(2, expectedMap.size());

        combinedMap.clear();
        expectedMap.clear();

        Map<String, String> pipelineMapNull = null;
        Map<String, String> uiMapNull = null;

        combinedMap = blackDuckParametersService.getCombinedBlackDuckParameters(pipelineMapNull, pipelineMapNull);

        assertEquals(null, combinedMap);
    }

}