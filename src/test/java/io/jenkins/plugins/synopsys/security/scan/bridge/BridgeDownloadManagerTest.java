package io.jenkins.plugins.synopsys.security.scan.bridge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.synopsys.security.scan.global.Utility;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BridgeDownloadManagerTest {
    private BridgeDownloadManager bridgeDownloadManager;
    private FilePath workspace;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final EnvVars envVarsMock = Mockito.mock(EnvVars.class);

    @BeforeEach
    void setup() {
        workspace = new FilePath(new File(getHomeDirectory()));
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
        bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);
    }

    @Test
    public void getInstalledBridgeVersionTest() {
        String versionFilePath = null;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            versionFilePath = new File("src\\test\\resources\\versions.txt").getAbsolutePath();

        } else {
            versionFilePath = new File("src/test/resources/versions.txt").getAbsolutePath();
        }

        String installedVersion = bridgeDownloadManager.getBridgeVersionFromVersionFile(versionFilePath);

        assertNotNull(versionFilePath, "version.txt file not found");
        assertEquals("0.3.1", installedVersion);
    }

    @Test
    void isSynopsysBridgeDownloadRequiredTest() {
        BridgeDownloadParameters bridgeDownloadParameters = new BridgeDownloadParameters(workspace, listenerMock);
        bridgeDownloadParameters.setBridgeDownloadUrl("https://fake.url.com/bridge");
        bridgeDownloadParameters.setBridgeInstallationPath("/path/to/bridge");

        BridgeDownloadManager mockedBridgeDownloadManager = Mockito.mock(BridgeDownloadManager.class);

        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        Mockito.when(mockedBridgeDownloadManager.checkIfBridgeInstalled(anyString()))
                .thenReturn(true);
        boolean isDownloadRequired = bridgeDownloadManager.isSynopsysBridgeDownloadRequired(bridgeDownloadParameters);

        assertTrue(isDownloadRequired);
    }

    @Test
    public void getDirectoryUrlTest() {
        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        String downloadUrlWithoutTrailingSlash =
                "https://myown.artifactory.com/release/synopsys-bridge/0.3.59/synopsys-bridge-0.3.59-linux64.zip";
        String directoryUrl = "https://myown.artifactory.com/release/synopsys-bridge/0.3.59";

        assertEquals(directoryUrl, bridgeDownloadManager.getDirectoryUrl(downloadUrlWithoutTrailingSlash));

        String downloadUrlWithTrailingSlash =
                "https://myown.artifactory.com/release/synopsys-bridge/latest/synopsys-bridge-linux64.zip/";
        String expectedDirectoryUrl = "https://myown.artifactory.com/release/synopsys-bridge/latest";

        assertEquals(expectedDirectoryUrl, bridgeDownloadManager.getDirectoryUrl(downloadUrlWithTrailingSlash));
    }

    @Test
    public void versionFileAvailableTest() {
        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        String directoryUrlWithoutVersionFile =
                "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/0.3.1/";
        String directoryUrlWithVersionFile =
                "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest/";

        assertFalse(bridgeDownloadManager.isVersionFileAvailableInArtifactory(directoryUrlWithoutVersionFile));
        assertTrue(bridgeDownloadManager.isVersionFileAvailableInArtifactory(directoryUrlWithVersionFile));
    }

    @Test
    public void extractVersionFromUrlTest() {
        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        String urlWithVersion =
                "https://myown.artifactory.com/synopsys-bridge/0.3.59/synopsys-bridge-0.3.59-linux64.zip";
        String expectedVersionWithVersion = "0.3.59";

        assertEquals(expectedVersionWithVersion, bridgeDownloadManager.extractVersionFromUrl(urlWithVersion));

        String urlWithoutVersion =
                "https://myown.artifactory.com/synopsys-bridge/latest/synopsys-bridge-latest-linux64.zip";
        String expectedVersionWithLatest = "NA";

        assertEquals(expectedVersionWithLatest, bridgeDownloadManager.extractVersionFromUrl(urlWithoutVersion));
    }

    @Test
    public void downloadVersionFileTest() {
        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        String directoryUrl =
                "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest";
        String tempVersionFilePath = bridgeDownloadManager.downloadVersionFileFromArtifactory(directoryUrl);
        FilePath tempVersionFile = new FilePath(new File(tempVersionFilePath));

        assertNotNull(tempVersionFilePath);
        try {
            assertTrue(tempVersionFile.exists());
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception while checking the existence of downloaded version file.");
        }
        Utility.removeFile(tempVersionFilePath, new FilePath(new File(getHomeDirectory())), listenerMock);
    }

    @Test
    void getLatestBridgeVersionFromArtifactoryTest() {
        BridgeDownloadManager bridgeDownloadManager = new BridgeDownloadManager(workspace, listenerMock, envVarsMock);

        String urlWithVersion =
                "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/0.3.1/synopsys-bridge-0.3.1-linux64.zip ";
        String resultWithVersion = bridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithVersion);

        assertEquals("0.3.1", resultWithVersion);

        String urlWithoutVersion =
                "https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest/synopsys-bridge-linux64.zip";
        BridgeDownloadManager mockedBridgeDownloadManager = Mockito.mock(BridgeDownloadManager.class);
        String expectedVersion = "0.3.59";
        Mockito.when(mockedBridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithoutVersion))
                .thenReturn(expectedVersion);

        String resultWithoutVersion =
                mockedBridgeDownloadManager.getLatestBridgeVersionFromArtifactory(urlWithoutVersion);

        assertEquals(expectedVersion, resultWithoutVersion);
    }

    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }
}
