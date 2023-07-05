package com.synopsys.integration.jenkins.scan.bridge;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;

public class BridgeInstall {
    private final TaskListener listener;
    private final EnvVars envVars;

    public BridgeInstall(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            bridgeZipPath.unzip(bridgeInstallationPath);

            //Have to call delete *explicitly* as JVM doesn't erase this file.
            bridgeZipPath.delete();

            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s \n", bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed");
            e.printStackTrace();
        }
    }
}
