package com.synopsys.integration.jenkins.scan.service.scan;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.model.TaskListener;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

public class ScanParametersServiceTest {
    private ScanParametersService scanParametersService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);

    @BeforeEach
    void setUp() {
        scanParametersService = new ScanParametersService(listenerMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }
    
    @Test
    void validParametersForBlackDuckTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "blackduck");
        parameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertTrue(scanParametersService.isValidScanParameters(parameters));
    }

    @Test
    void invalidParametersForBlackDuckAndPolarisTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "blackduck, polaris");
        parameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertFalse(scanParametersService.isValidScanParameters(parameters));
    }

    @Test
    void validParametersForBlackDuckAndPolarisTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "blackduck, polaris");

        parameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        parameters.put(ApplicationConstants.POLARIS_SERVER_URL_KEY, "https://fake.polaris.url");
        parameters.put(ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY, "MDJDSRRTRDFYJGH66FAKEKEY");
        parameters.put(ApplicationConstants.POLARIS_APPLICATION_NAME_KEY, "test-application");
        parameters.put(ApplicationConstants.POLARIS_PROJECT_NAME_KEY, "test-project");
        parameters.put(ApplicationConstants.POLARIS_ASSESSMENT_TYPES_KEY, "SCA, SAST");

        assertTrue(scanParametersService.isValidScanParameters(parameters));
    }

    @Test
    public void getSynopsysSecurityPlatformsTest() {
        Map<String, Object> scanParametersWithMultiplePlatforms = new HashMap<>();
        Map<String, Object> scanParametersWithSinglePlatform = new HashMap<>();
        scanParametersWithMultiplePlatforms.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "blackduck, polaris");
        scanParametersWithSinglePlatform.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "");

        Set<String> multiplePlatforms = scanParametersService.getSynopsysSecurityProducts(scanParametersWithMultiplePlatforms);
        Set<String> singlePlatform = scanParametersService.getSynopsysSecurityProducts(scanParametersWithSinglePlatform);

        assertEquals(2, multiplePlatforms.size());
        assertEquals(1, singlePlatform.size());
    }
}
