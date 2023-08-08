package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.HomeDirectoryTask;
import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.FilePath;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.IOException;

public class BridgeInstall {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeInstall(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
    }

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            bridgeZipPath.unzip(workspace);
            bridgeZipPath.delete();
            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s%n",
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }

    public String defaultBridgeInstallationPath(FilePath workspace, TaskListener listener) {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        String separator = Utility.getDirectorySeparator(workspace, listener);
        String defaultInstallationPath = null;

        if (jenkins != null && workspace.isRemote()) {
            listener.getLogger().println("Jenkins is running on agent node remotely.");
        } else {
            listener.getLogger().println("Jenkins is running on the master node.");
        }

        try {
            defaultInstallationPath = workspace.act(new HomeDirectoryTask(separator));
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Failed to fetch plugin's default installation path.");
        }
        Utility.verifyAndCreateInstallationPath(defaultInstallationPath, workspace, listener);

        return defaultInstallationPath;
    }
}
