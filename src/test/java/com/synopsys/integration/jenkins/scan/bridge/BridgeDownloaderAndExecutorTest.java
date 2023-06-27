package com.synopsys.integration.jenkins.scan.bridge;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.FilePath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;

import static com.synopsys.integration.jenkins.scan.global.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class BridgeDownloaderAndExecutorTest {
    private final BridgeDownloaderAndExecutor bridgeDownloaderAndExecutorMock = Mockito.mock(BridgeDownloaderAndExecutor.class);
    private BridgeDownloaderAndExecutor bridgeDownloaderAndExecutor;

    @BeforeEach
    public void setup() {
        bridgeDownloaderAndExecutor = new BridgeDownloaderAndExecutor(null, null);
    }

    @Test
    void testDownloadSynopsysBridge() {
        FilePath inputDownloadFilePath = new FilePath(new File(ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH));
        String bridgeVersion = null;
        String bridgeDownloadUrl = null;

        FilePath outputDownloadFilePath = new FilePath(new File(ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH
                .concat(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT)));

        Mockito.when(bridgeDownloaderAndExecutorMock.downloadSynopsysBridge
                (inputDownloadFilePath, bridgeVersion, bridgeDownloadUrl)).thenReturn(outputDownloadFilePath);

        assertEquals(outputDownloadFilePath, bridgeDownloaderAndExecutorMock.downloadSynopsysBridge
                (inputDownloadFilePath, bridgeVersion, bridgeDownloadUrl));

    }

    @Test
    void TestUnzipSynopsysBridge() {
        String bridgeZipPath = BRIDGE_DOWNLOAD_FILE_PATH.concat("/").concat(BRIDGE_ZIP_FILE_FORMAT);
        String bridgeUnzipPath = "/synopsys-security-scan-plugin/work/workspace/First_Job_main";

        bridgeDownloaderAndExecutorMock.unzipSynopsysBridge
                (new FilePath(new File(bridgeZipPath)), new FilePath(new File(bridgeUnzipPath)));

        verify(bridgeDownloaderAndExecutorMock, times(1))
                .unzipSynopsysBridge(new FilePath(new File(bridgeZipPath)), new FilePath(new File(bridgeUnzipPath)));
    }
    @Test
   void testValidateBridgeVersion() {
        String bridgeVersion = "0.3.1";

        assertEquals(true, bridgeDownloaderAndExecutor.isValidVersion(bridgeVersion));
    }

    @Test
    void testValidateBridgeDownloadUrl() {
        String bridgeDownloadUrl = BRIDGE_ARTIFACTORY_URL.concat(SYNOPSYS_BRIDGE_LATEST_VERSION)
                .concat("/synopsys-bridge-").concat(PLATFORM_LINUX).concat(".zip") ;

        assertEquals(true, bridgeDownloaderAndExecutor.isValidBridgeDownloadUrl(bridgeDownloadUrl));

    }

    @Test
    void testValidateBridgeUrlExistence() {
        String bridgeUrl = BRIDGE_ARTIFACTORY_URL.concat(SYNOPSYS_BRIDGE_LATEST_VERSION)
                .concat("/synopsys-bridge-").concat(PLATFORM_LINUX).concat(".zip") ;

        Mockito.when(bridgeDownloaderAndExecutorMock.checkIfBridgeUrlExists(bridgeUrl)).thenReturn(true);

        assertEquals(true, bridgeDownloaderAndExecutorMock.checkIfBridgeUrlExists(bridgeUrl));
    }

}
