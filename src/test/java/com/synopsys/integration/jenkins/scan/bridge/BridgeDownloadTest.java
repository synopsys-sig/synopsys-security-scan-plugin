package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.exception.PluginExceptionHandler;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import static org.junit.jupiter.api.Assertions.*;

public class BridgeDownloadTest {
    private final BridgeDownload bridgeDownloadMock = Mockito.mock(BridgeDownload.class);
    private TaskListener listenerMock;
    private FilePath workspace;
    private EnvVars envVarsMock;

    @BeforeEach
    public void setUp() {
        workspace = new FilePath(new File(System.getProperty("user.home")));
        listenerMock = Mockito.mock(TaskListener.class);
        envVarsMock = Mockito.mock(EnvVars.class);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    void downloadSynopsysBridgeTest() throws PluginExceptionHandler {
        String bridgeDownloadUrl = null;
        String bridgeInstallationPath = "/path/to/bridge";

        FilePath outputDownloadFilePath = new FilePath(new File(ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH
                .concat(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT)));

        Mockito.when(bridgeDownloadMock.downloadSynopsysBridge
                (bridgeDownloadUrl, bridgeInstallationPath)).thenReturn(outputDownloadFilePath);

        assertEquals(outputDownloadFilePath, bridgeDownloadMock.downloadSynopsysBridge
                (bridgeDownloadUrl, bridgeInstallationPath));
    }

    @Test
    public void getHttpStatusCodeTest() {
        String bridgeDownloadUrl = String.join("/",
                ApplicationConstants.BRIDGE_ARTIFACTORY_URL, "latest", "synopsys-bridge-linux64.zip");
        String invalidDownloadUrl = "https://invalid.bridge-download.url";

        BridgeDownload bridgeDownload = new BridgeDownload(workspace, listenerMock, envVarsMock);

        assertEquals(200, bridgeDownload.getHttpStatusCode(bridgeDownloadUrl));
        assertEquals(-1, bridgeDownload.getHttpStatusCode(invalidDownloadUrl));
    }

    @Test
    public void terminateRetryTest() {
        BridgeDownload bridgeDownload = new BridgeDownload(workspace, listenerMock, envVarsMock);

        assertTrue(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_UNAUTHORIZED));
        assertTrue(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_FORBIDDEN));
        assertTrue(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_OK));
        assertTrue(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_CREATED));
        assertTrue(bridgeDownload.terminateRetry(416));

        assertFalse(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_NOT_FOUND));
        assertFalse(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_BAD_REQUEST));
        assertFalse(bridgeDownload.terminateRetry(HttpURLConnection.HTTP_INTERNAL_ERROR));
    }

    @Test
    void checkIfBridgeUrlExistsTest() {
        String bridgeDownloadUrl = String.join("/",
                ApplicationConstants.BRIDGE_ARTIFACTORY_URL, "latest", "synopsys-bridge-linux64.zip");
        String invalidUrl = "https://invalid.bridge-download.url";

        BridgeDownload bridgeDownload = new BridgeDownload(workspace, listenerMock, envVarsMock);

        assertTrue(bridgeDownload.checkIfBridgeUrlExists(bridgeDownloadUrl));
        assertFalse(bridgeDownload.checkIfBridgeUrlExists(invalidUrl));
        assertFalse(bridgeDownload.checkIfBridgeUrlExists(null));
    }
}
