package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.HomeDirectoryTask;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
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
            listener.getLogger().printf(LogMessages.SYNOPSYS_BRIDGE_ZIP_PATH_AND_INSTALLATION_PATH,
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_UNZIPPING_SYNOPSYS_BRIDGE, e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }

    public String defaultBridgeInstallationPath(FilePath workspace, TaskListener listener) {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        String separator = Utility.getDirectorySeparator(workspace, listener);
        String defaultInstallationPath = null;

        if (jenkins != null && workspace.isRemote()) {
            listener.getLogger().println(LogMessages.JOB_RUNNING_ON_AGENT_NODE);
        } else {
            listener.getLogger().println(LogMessages.JOB_RUNNING_ON_MASTER_NODE);
        }

        try {
            defaultInstallationPath = workspace.act(new HomeDirectoryTask(separator));
        } catch (IOException | InterruptedException e) {
            listener.getLogger().printf(LogMessages.FAILED_TO_FETCH_PLUGINS_DEFAULT_INSTALLATION_PATH, e.getMessage());
        }
        Utility.verifyAndCreateInstallationPath(defaultInstallationPath, workspace, listener);

        return defaultInstallationPath;
    }
}
