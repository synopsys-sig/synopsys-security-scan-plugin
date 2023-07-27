package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static com.synopsys.integration.jenkins.scan.global.ApplicationConstants.BRIDGE_DOWNLOAD_FILE_PATH;
import static com.synopsys.integration.jenkins.scan.global.ApplicationConstants.BRIDGE_ZIP_FILE_FORMAT;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BridgeInstallTest {
    private final BridgeInstall bridgeInstallMock = Mockito.mock(BridgeInstall.class);
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final FilePath workspaceMock = Mockito.mock(FilePath.class);
    private BridgeInstall BridgeInstall;

    @BeforeEach
    public void setup() {
        BridgeInstall = new BridgeInstall(listenerMock, workspaceMock);
    }

    @Test
    void installSynopsysBridgeTest() {
        String bridgeZipPath = BRIDGE_DOWNLOAD_FILE_PATH.concat("/").concat(BRIDGE_ZIP_FILE_FORMAT);
        String bridgeUnzipPath = "/bridge/unzip/path";

        bridgeInstallMock.installSynopsysBridge(new FilePath(new File(bridgeZipPath)), new FilePath(new File(bridgeUnzipPath)));

        verify(bridgeInstallMock, times(1))
                .installSynopsysBridge(new FilePath(new File(bridgeZipPath)), new FilePath(new File(bridgeUnzipPath)));
    }
}
