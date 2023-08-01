package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

public class BridgeInstallTest {
//    private final BridgeInstall bridgeInstallMock = Mockito.mock(BridgeInstall.class);
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private FilePath workspace;
    private BridgeInstall bridgeInstall;
    //TODO: compleate this test
    // why blackduck_input json is copied to the home folder but not cleaned.

    @BeforeEach
    public void setup() {
        workspace = new FilePath(new File(getHomeDirectory()));
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
        bridgeInstall = new BridgeInstall(workspace, listenerMock);
    }

    @Test
    void installSynopsysBridgeTest() {
        FilePath sourceBridge = new FilePath(new File("src/test/resources/demo-bridge.zip"));
        try {
            sourceBridge.copyTo(workspace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        bridgeInstall.installSynopsysBridge(getFullZipPath(), workspace);

       /* try {
            Mockito.verify(bridgeZipPath, times(1)).unzip(workspace);
            Mockito.verify(bridgeZipPath, times(1)).delete();
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception occurred during testing for installSynopsysBridge method. " + e.getMessage());
        }*/
        System.out.println("workspace ?????? " + workspace);
        assertNotNull(workspace.getRemote().concat("/").concat("versions.txt"));
        assertNotNull(workspace.getRemote().concat("/").concat("LICENSE.txt"));
    }
    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }

    public FilePath getFullZipPath() {
        FilePath bridgeZipPath = null;
        if(getHomeDirectory().contains("\\")) {
            bridgeZipPath = new FilePath(new File(workspace.getRemote().concat("\\").concat("demo-bridge.zip")));
        } else {
            bridgeZipPath = new FilePath(new File(workspace.getRemote().concat("/").concat("demo-bridge.zip")));
        }
        return bridgeZipPath;
    }

}
