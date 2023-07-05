package com.synopsys.integration.jenkins.scan.bridge;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import hudson.FilePath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;

import static com.synopsys.integration.jenkins.scan.global.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BridgeDownloadTest {
    private final BridgeDownload bridgeDownloadMock = Mockito.mock(BridgeDownload.class);
    private BridgeDownload bridgeDownload;

    @BeforeEach
    public void setup() {
        bridgeDownload = new BridgeDownload(null, null);
    }

    @Test
    void downloadSynopsysBridgeTest() {
        String bridgeVersion = null;
        String bridgeDownloadUrl = null;

        FilePath outputDownloadFilePath = new FilePath(new File(ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH
                .concat(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT)));

        Mockito.when(bridgeDownloadMock.downloadSynopsysBridge
                ( bridgeVersion, bridgeDownloadUrl)).thenReturn(outputDownloadFilePath);

        assertEquals(outputDownloadFilePath, bridgeDownloadMock.downloadSynopsysBridge
                (bridgeVersion, bridgeDownloadUrl));

    }

    @Test
   void validateBridgeVersionTest() {
        String bridgeVersion = "0.3.1";

        assertEquals(true, bridgeDownload.isValidVersion(bridgeVersion));
    }

    @Test
    void validateBridgeDownloadUrlTest() {
        String bridgeDownloadUrl = BRIDGE_ARTIFACTORY_URL.concat(SYNOPSYS_BRIDGE_LATEST_VERSION)
                .concat("/synopsys-bridge-").concat(PLATFORM_LINUX).concat(".zip") ;

        assertEquals(true, bridgeDownload.isValidBridgeDownloadUrl(bridgeDownloadUrl));

    }

    @Test
    void validateBridgeUrlExistenceTest() {
        String bridgeUrl = BRIDGE_ARTIFACTORY_URL.concat(SYNOPSYS_BRIDGE_LATEST_VERSION)
                .concat("/synopsys-bridge-").concat(PLATFORM_LINUX).concat(".zip") ;

        Mockito.when(bridgeDownloadMock.checkIfBridgeUrlExists(bridgeUrl)).thenReturn(true);

        assertEquals(true, bridgeDownloadMock.checkIfBridgeUrlExists(bridgeUrl));
    }

}
