package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.remoting.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

public class BridgeDownloadManagerTest {
    private BridgeDownloadManager bridgeDownloadManager;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final FilePath workspaceMock = Mockito.mock(FilePath.class);
    FilePath workspaceSpy = Mockito.spy(workspaceMock);

    @BeforeEach
    void setup() {
        bridgeDownloadManager = new BridgeDownloadManager(workspaceSpy, listenerMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    public void getInstalledBridgeVersionTest() {
        String versionFilePath = new File("src/test/resources/versions.txt").getAbsolutePath();

        String installedVersion = bridgeDownloadManager.getBridgeVersionFromVersionFile(versionFilePath);

        assertNotNull(versionFilePath, "version.txt file not found");
        assertEquals("0.3.1", installedVersion);
    }

    @Test
    void isSynopsysBridgeDownloadRequiredTest() {
        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspaceMock, listenerMock);
        bridgeDownloadParameters.setBridgeDownloadUrl("https://fake.url.com/bridge");
        bridgeDownloadParameters.setBridgeInstallationPath("/path/to/bridge");

        BridgeDownloadManager mockedBridgeDownloadManager = Mockito.mock(BridgeDownloadManager.class);

        Mockito.when(mockedBridgeDownloadManager.checkIfBridgeInstalled(anyString())).thenReturn(true);
        boolean isDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParameters);

        assertTrue(isDownloadRequired);
    }

    @Test
    public void getDirectoryUrlTest() {
        String downloadUrlWithoutTrailingSlash = "https://myown.artifactory.com/release/synopsys-bridge/0.3.59/synopsys-bridge-0.3.59-linux64.zip";
        String directoryUrl = "https://myown.artifactory.com/release/synopsys-bridge/0.3.59";

        assertEquals(directoryUrl, bridgeDownloadManager.getDirectoryUrl(downloadUrlWithoutTrailingSlash));

        String downloadUrlWithTrailingSlash = "https://myown.artifactory.com/release/synopsys-bridge/latest/synopsys-bridge-linux64.zip/";
        String expectedDirectoryUrl = "https://myown.artifactory.com/release/synopsys-bridge/latest";

        assertEquals(expectedDirectoryUrl, bridgeDownloadManager.getDirectoryUrl(downloadUrlWithTrailingSlash));
    }

    @Test
    public void versionFileAvailableTest() {
        String directoryUrlWithoutVersionFile = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/0.3.1/";
        String directoryUrlWithVersionFile = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest/";

        assertFalse(bridgeDownloadManager.versionFileAvailable(directoryUrlWithoutVersionFile));
        assertTrue(bridgeDownloadManager.versionFileAvailable(directoryUrlWithVersionFile));
    }

    @Test
    public void extractVersionFromUrlTest() {
        String urlWithVersion = "https://myown.artifactory.com/synopsys-bridge/0.3.59/synopsys-bridge-0.3.59-linux64.zip";
        String expectedVersionWithVersion = "0.3.59";

        assertEquals(expectedVersionWithVersion, bridgeDownloadManager.extractVersionFromUrl(urlWithVersion));

        String urlWithoutVersion = "https://myown.artifactory.com/synopsys-bridge/latest/synopsys-bridge-latest-linux64.zip";
        String expectedVersionWithLatest = "NA";

        assertEquals(expectedVersionWithLatest, bridgeDownloadManager.extractVersionFromUrl(urlWithoutVersion));
    }

    @Test
    public void downloadVersionFileTest() {
        String directoryUrl = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest";
        String tempVersionFilePath = bridgeDownloadManager.downloadVersionFile(directoryUrl);
        File tempVersionFile = new File(tempVersionFilePath);

        assertNotNull(tempVersionFilePath);
        assertTrue(tempVersionFile.exists());
    }

    @Test
    void getLatestBridgeVersionFromArtifactoryTest() {
        String urlWithVersion = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/0.3.1/synopsys-bridge-0.3.1-linux64.zip ";
        String resultWithVersion = bridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithVersion);

        assertEquals("0.3.1", resultWithVersion);

        String urlWithoutVersion = "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest/synopsys-bridge-linux64.zip";
        BridgeDownloadManager mockedBridgeDownloadManager = Mockito.mock(BridgeDownloadManager.class);
        String expectedVersion = "0.3.59";
        Mockito.when(mockedBridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithoutVersion)).thenReturn(expectedVersion);

        String resultWithoutVersion = mockedBridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithoutVersion);

        assertEquals(expectedVersion, resultWithoutVersion);
    }
}
