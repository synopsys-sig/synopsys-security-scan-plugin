package com.synopsys.integration.jenkins.scan.service.scan.strategy;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import com.synopsys.integration.jenkins.scan.service.scan.blackduck.BlackDuckParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.coverity.CoverityParametersService;
import com.synopsys.integration.jenkins.scan.service.scan.polaris.PolarisParametersService;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScanStrategyFactoryTest {
    private final TaskListener listener = Mockito.mock(TaskListener.class);
    private ScanStrategyFactory scanStrategyFactory;

    @BeforeEach
    void setUp() {
        scanStrategyFactory = new ScanStrategyFactory(listener);
    }

    @Test
    void getBlackDuckParametersServiceTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.BLACKDUCK.name());

        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof BlackDuckParametersService);
        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof CoverityParametersService);
        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof PolarisParametersService);

        assertTrue(scanStrategyFactory.getParametersService(Collections.EMPTY_MAP) instanceof BlackDuckParametersService);
    }

    @Test
    void getCoverityParametersServiceTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.COVERITY.name());

        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof BlackDuckParametersService);
        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof CoverityParametersService);
        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof PolarisParametersService);
    }

    @Test
    void getPolarisParametersServiceTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.POLARIS.name());

        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof BlackDuckParametersService);
        assertFalse(scanStrategyFactory.getParametersService(parameters) instanceof CoverityParametersService);
        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof PolarisParametersService);
    }

}
