package com.synopsys.integration.jenkins.scan.bridge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class BridgeDownloadManagerTest {
    private BridgeDownloadManager bridgeDownloadManager;
    @BeforeEach
    void setup() {
        bridgeDownloadManager = new BridgeDownloadManager();
    }

    @Test
    public void getInstalledBridgeVersionTest() {
        String directoryPath = getClass().getResource("/versions.txt").getPath();
        File directory = new File(directoryPath).getParentFile();
        String directoryAbsolutePath = directory.getAbsolutePath();

        String installedVersion = bridgeDownloadManager.getInstalledBridgeVersion(directoryAbsolutePath);

        assertNotNull(directoryAbsolutePath, "version.txt file not found");
        assertEquals("0.3.1", installedVersion);
    }

    @Test
    void getLatestVersionTest() {
        List<String> versions = new ArrayList<>();
        versions.add("0.0.0");
        versions.add("2.0.0");
        versions.add("1.5.0");
        versions.add("3.0.0");

        String latestVersion = bridgeDownloadManager.getLatestVersion(versions);

        assertEquals("3.0.0", latestVersion);
    }

    @Test
    void isSynopsysBridgeDownloadRequiredTest() {
        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters();
        bridgeDownloadParameters.setBridgeDownloadUrl("https://fake.url.com/bridge");
        bridgeDownloadParameters.setBridgeInstallationPath("/path/to/bridge");
        bridgeDownloadParameters.setBridgeDownloadVersion("1.0.0");

        BridgeDownloadManager mockedBridgeDownloadManager = Mockito.mock(BridgeDownloadManager.class);

        Mockito.when(mockedBridgeDownloadManager.checkIfBridgeInstalled(anyString())).thenReturn(true);
        Mockito.when(mockedBridgeDownloadManager.getInstalledBridgeVersion(anyString())).thenReturn("0.9.0");
        Mockito.when(mockedBridgeDownloadManager.getAllAvailableBridgeVersionsFromArtifactory(anyString())).thenReturn(
                Arrays.asList("0.9.0", "1.0.0", "1.1.0")
        );
        boolean isDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParameters);

        assertTrue(isDownloadRequired);
    }
}
