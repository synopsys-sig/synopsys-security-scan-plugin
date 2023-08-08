package com.synopsys.integration.jenkins.scan.bridge;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.FilePath;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.File;
import static com.synopsys.integration.jenkins.scan.global.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BridgeDownloadTest {
    private final BridgeDownload bridgeDownloadMock = Mockito.mock(BridgeDownload.class);

    @Test
    void downloadSynopsysBridgeTest() {
        String bridgeDownloadUrl = null;

        FilePath outputDownloadFilePath = new FilePath(new File(ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH
                .concat(ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT)));

        Mockito.when(bridgeDownloadMock.downloadSynopsysBridge
                (bridgeDownloadUrl)).thenReturn(outputDownloadFilePath);

        assertEquals(outputDownloadFilePath, bridgeDownloadMock.downloadSynopsysBridge
                (bridgeDownloadUrl));
    }

    @Test
    void validateBridgeUrlExistenceTest() {
        String bridgeUrl = BRIDGE_ARTIFACTORY_URL.concat(SYNOPSYS_BRIDGE_LATEST_VERSION)
                .concat("/synopsys-bridge-").concat(PLATFORM_LINUX).concat(".zip") ;

        Mockito.when(bridgeDownloadMock.checkIfBridgeUrlExists(bridgeUrl)).thenReturn(true);

        assertTrue(bridgeDownloadMock.checkIfBridgeUrlExists(bridgeUrl));
    }
}
