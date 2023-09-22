package com.synopsys.integration.jenkins.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.synopsys.integration.jenkins.scan.exception.PluginExceptionHandler;
import com.synopsys.integration.jenkins.scan.exception.ScannerException;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.service.scan.ScanParametersService;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ScanPipelineCommandsTest {
    private SecurityScanner securityScannerMock;
    private TaskListener listenerMock;
    private FilePath workspace;
    private EnvVars envVarsMock;
    private ScanPipelineCommands scanPipelineCommands;

    @BeforeEach
    void setUp() {
        securityScannerMock = Mockito.mock(SecurityScanner.class);
        workspace = new FilePath(new File(System.getProperty("user.home")));
        listenerMock = Mockito.mock(TaskListener.class);
        envVarsMock = Mockito.mock(EnvVars.class);
        scanPipelineCommands = new ScanPipelineCommands(securityScannerMock, workspace, envVarsMock, listenerMock);
        
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    public void initializeScannerValidParametersTest() throws PluginExceptionHandler, ScannerException {
        Map<String, Object> scanParameters = new HashMap<>();
        scanParameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "BLACKDUCK");
        scanParameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        scanParameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        int exitCode = scanPipelineCommands.initializeScanner(scanParameters);

        assertEquals(0, exitCode);
    }

    @Test
    public void initializeScannerInvalidParametersTest() {
        ScanParametersService mockScanParametersService = Mockito.mock(ScanParametersService.class);

        Map<String, Object> scanParameters = new HashMap<>();
        scanParameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "BLACKDUCK");

        Mockito.when(mockScanParametersService.isValidScanParameters(scanParameters)).thenReturn(false);

        assertThrows(PluginExceptionHandler.class, () -> scanPipelineCommands.initializeScanner(scanParameters));
    }

    @Test
    public void initializeScannerAirGapFailureTest() {
        Map<String, Object> scanParameters = new HashMap<>();
        scanParameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "BLACKDUCK");
        scanParameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        scanParameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");
        scanParameters.put(ApplicationConstants.NETWORK_AIRGAP_KEY, true);
        scanParameters.put(ApplicationConstants.SYNOPSYS_BRIDGE_INSTALL_DIRECTORY, "/path/to/bridge");

        assertThrows(PluginExceptionHandler.class, () -> scanPipelineCommands.initializeScanner(scanParameters));
    }

    @Test
    public void initializeScannerAirGapSuccessTest() throws PluginExceptionHandler, ScannerException {
        Map<String, Object> scanParameters = new HashMap<>();
        scanParameters.put(ApplicationConstants.SYNOPSYS_SECURITY_PRODUCT_KEY, "BLACKDUCK");
        scanParameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, "https://fake.blackduck.url");
        scanParameters.put(ApplicationConstants.BLACKDUCK_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");
        scanParameters.put(ApplicationConstants.NETWORK_AIRGAP_KEY, true);

        int exitCode = scanPipelineCommands.initializeScanner(scanParameters);

        assertEquals(0, exitCode);
    }

}
