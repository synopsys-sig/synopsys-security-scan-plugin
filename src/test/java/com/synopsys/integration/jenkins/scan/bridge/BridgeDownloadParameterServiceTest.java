package com.synopsys.integration.jenkins.scan.bridge;

import static org.junit.jupiter.api.Assertions.*;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.service.BridgeDownloadParametersService;
import hudson.FilePath;
import hudson.model.TaskListener;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BridgeDownloadParameterServiceTest {
    private BridgeDownloadParametersService bridgeDownloadParametersService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private FilePath workspace;

    @BeforeEach
    void setUp() {
        workspace = new FilePath(new File(getHomeDirectory()));
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
        bridgeDownloadParametersService = new BridgeDownloadParametersService(workspace, listenerMock);
    }

    @Test
    void isValidUrlTest() {
        String validUrl = "https://fake.url.com";
        assertTrue(bridgeDownloadParametersService.isValidUrl(validUrl));

        String ip = "https://102.118.100.102/";
        assertTrue(bridgeDownloadParametersService.isValidUrl(ip));

        String emptyUrl = "";
        assertFalse(bridgeDownloadParametersService.isValidUrl(emptyUrl));

        String invalidUrl = "invalid url";
        assertFalse(bridgeDownloadParametersService.isValidUrl(invalidUrl));
    }

    @Test
    void isValidVersionTest() {
        String validVersion = "1.2.3";
        assertTrue(bridgeDownloadParametersService.isValidVersion(validVersion));
        assertTrue(bridgeDownloadParametersService.isValidVersion("latest"));

        String invalidVersion = "x.x.x";
        assertFalse(bridgeDownloadParametersService.isValidVersion(invalidVersion));
    }

    @Test
    void isValidInstallationPathTest() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        String validPath = null;
        String invalidPath = null;
        if (os.contains("win")) {
            validPath = String.join("\\", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("\\", "\\path\\absent", ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            validPath = String.join("/", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("/", "/path/absent", ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }

        assertTrue(bridgeDownloadParametersService.isValidInstallationPath(validPath));
        assertFalse(bridgeDownloadParametersService.isValidInstallationPath(invalidPath));
    }

    @Test
    void getBridgeDownloadParamsTest() {
        Map<String, Object> scanParams = new HashMap<>();
        scanParams.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, "3.0.0");
        scanParams.put(ApplicationConstants.BRIDGE_INSTALLATION_PATH, "/path/to/bridge");
        scanParams.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, "https://fake.url.com");

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listenerMock);
        
        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        assertEquals("https://fake.url.com", result.getBridgeDownloadUrl());
        assertEquals("/path/to/bridge", result.getBridgeInstallationPath());
    }

    @Test
    void getBridgeDownloadParamsNullTest() {
        Map<String, Object> scanParamsNull = new HashMap<>();

        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listenerMock);
        
        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParamsNull, bridgeDownloadParameters);

        assertNotNull(result);
        assertNotNull(result.getBridgeDownloadUrl());
        assertNotNull(result.getBridgeDownloadVersion());
        assertNotNull(result.getBridgeInstallationPath());
    }

    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }
}
