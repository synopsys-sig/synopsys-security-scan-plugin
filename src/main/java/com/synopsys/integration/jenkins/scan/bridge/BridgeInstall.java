package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;

public class BridgeInstall {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeInstall(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
    }

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            listener.getLogger().println("Method: installSynopsysBridge() Bridge zip path: " + bridgeZipPath);
            listener.getLogger().println("Method: installSynopsysBridge() Bridge installation path: " + bridgeInstallationPath);

            listener.getLogger().println("Method: installSynopsysBridge() Temp unzipped Path: " + workspace.getRemote());

            bridgeZipPath.unzip(workspace);
            bridgeZipPath.delete();

            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s%n",
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }
}
