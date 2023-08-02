package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BridgeInstallTest {
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private FilePath workspace;
    private BridgeInstall bridgeInstall;

    @BeforeEach
    public void setup() {
        workspace = new FilePath(new File(getHomeDirectory()));
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
        bridgeInstall = new BridgeInstall(workspace, listenerMock);
    }

    @Test
    void installSynopsysBridgeTest() {
        FilePath sourceBridge = null;
        String os = System.getProperty("os.name").toLowerCase();
        FilePath destinationBridge = workspace.child("demo-bridge.zip");

        if(os.contains("win")) {
            sourceBridge = new FilePath(new File("src\\test\\resources\\demo-bridge.zip"));
        } else {
            sourceBridge = new FilePath(new File("src/test/resources/demo-bridge.zip"));
        }

        try {
            sourceBridge.copyTo(destinationBridge);
            bridgeInstall.installSynopsysBridge(getFullZipPath(), workspace);

            assertFalse(destinationBridge.exists());
            assertTrue(workspace.child("demo-bridge-extensions").isDirectory());
            assertTrue(workspace.child("demo-bridge-versions.txt").exists());
            assertTrue(workspace.child("demo-bridge-LICENSE.txt").exists());

            cleanupWorkspace(workspace);

        } catch (IOException | InterruptedException e) {
            System.out.println("Exception occurred during testing for installSynopsysBridge method. " + e.getMessage());
        }
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

    public static void cleanupWorkspace(FilePath workspace) {
        try {
            FilePath versionsFile = workspace.child("demo-bridge-versions.txt");
            if (versionsFile.exists()) {
                versionsFile.delete();
            }

            FilePath licenseFile = workspace.child("demo-bridge-LICENSE.txt");
            if (licenseFile.exists()) {
                licenseFile.delete();
            }

            FilePath extensionsDirectory = workspace.child("demo-bridge-extensions");
            if (extensionsDirectory.isDirectory()) {
                extensionsDirectory.deleteRecursive();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error while cleaning up workspace: " + e.getMessage());
        }
    }
}
