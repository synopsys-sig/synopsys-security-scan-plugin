package com.synopsys.integration.jenkins.scan.service.parameters;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.model.TaskListener;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametersServiceTest {
    private ParametersService parametersService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);

    @BeforeEach
    void setUp() {
        parametersService = new ParametersService(listenerMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }
    
    @Test
    void validParametersForBlackDuckTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, "blackduck");
        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertTrue(parametersService.isValidParameters(parameters));
    }

    @Test
    void invalidParametersForBlackDuckAndPolarisTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, "blackduck, polaris");
        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertFalse(parametersService.isValidParameters(parameters));
    }

    @Test
    void validParametersForBlackDuckAndPolarisTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, "blackduck, polaris");

        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BRIDGE_BLACKDUCK_API_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        parameters.put(ApplicationConstants.BRIDGE_POLARIS_SERVER_URL_KEY, "https://fake.polaris.url");
        parameters.put(ApplicationConstants.BRIDGE_POLARIS_ACCESS_TOKEN_KEY, "MDJDSRRTRDFYJGH66FAKEKEY");
        parameters.put(ApplicationConstants.BRIDGE_POLARIS_APPLICATION_NAME_KEY, "test-application");
        parameters.put(ApplicationConstants.BRIDGE_POLARIS_PROJECT_NAME_KEY, "test-project");
        parameters.put(ApplicationConstants.BRIDGE_POLARIS_ASSESSMENT_TYPES_KEY, "SCA, SAST");

        assertTrue(parametersService.isValidParameters(parameters));
    }
}
