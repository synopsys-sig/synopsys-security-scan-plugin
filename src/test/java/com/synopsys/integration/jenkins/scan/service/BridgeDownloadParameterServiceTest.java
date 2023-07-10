package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadParameters;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class BridgeDownloadParameterServiceTest {
    private BridgeDownloadParametersService bridgeDownloadParametersService;
    private  BridgeDownloadParameters bridgeDownloadParameters;

    @BeforeEach
    void setUp() {
        bridgeDownloadParameters = new BridgeDownloadParameters();
        bridgeDownloadParametersService = new BridgeDownloadParametersService();
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

        String validPath = userHome;
        String invalidPath = "/path/absent";
        if (os.contains("win")) {
            validPath = String.join("\\", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("\\", invalidPath, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            validPath = String.join("/", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("/", invalidPath, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }

        assertTrue(bridgeDownloadParametersService.isValidInstallationPath(validPath));
        assertFalse(bridgeDownloadParametersService.isValidInstallationPath(invalidPath));
    }

    @Test
    void getBridgeDownloadParamsTest() {
        Map<String, Object> scanParams = new HashMap<>();

        scanParams.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, "3.0.0");
        scanParams.put(ApplicationConstants.BRIDGE_INSTALLATION_PATH, "/path/to/bridge");

        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        assertEquals(ApplicationConstants.BRIDGE_ARTIFACTORY_URL, result.getBridgeDownloadUrl());
        assertEquals("3.0.0", result.getBridgeDownloadVersion());
        assertEquals("/path/to/bridge", result.getBridgeInstallationPath());
    }

    @Test
    void getBridgeDownloadParamsNullTest() {
        Map<String, Object> scanParamsNull = new HashMap<>();

        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParamsNull, bridgeDownloadParameters);

        assertNotNull(result);
        assertNotNull(result.getBridgeDownloadVersion());
    }
}
