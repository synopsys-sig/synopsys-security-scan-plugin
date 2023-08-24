package com.synopsys.integration.jenkins.scan.service.scan.strategy;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class ScanStrategyFactoryTest {
    private final TaskListener listener = Mockito.mock(TaskListener.class);
    private ScanStrategyFactory scanStrategyFactory;

    @BeforeEach
    void setUp() {
        scanStrategyFactory = new ScanStrategyFactory(listener);
    }

    @Test
    void getParametersServiceTest() {
        Map<String, Object> parameters = new HashMap<>();

        assertTrue(scanStrategyFactory.getParametersService(Collections.EMPTY_MAP) instanceof BlackDuckParametersService);
        
        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.BLACKDUCK.name());
        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof BlackDuckParametersService);

        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.COVERITY.name());
        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof CoverityParametersService);

        parameters.put(ApplicationConstants.SCAN_TYPE_KEY, ScanType.POLARIS.name());
        assertTrue(scanStrategyFactory.getParametersService(parameters) instanceof PolarisParametersService);
    }

}
