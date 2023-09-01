/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.bridge;

import com.synopsys.integration.jenkins.scan.global.HomeDirectoryTask;
import com.synopsys.integration.jenkins.scan.global.LogMessages;
import com.synopsys.integration.jenkins.scan.global.LoggerWrapper;
import com.synopsys.integration.jenkins.scan.global.Utility;
import hudson.FilePath;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.IOException;

public class BridgeInstall {
    private final LoggerWrapper logger;
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeInstall(FilePath workspace, TaskListener listener) {
        this.workspace = workspace;
        this.listener = listener;
        this.logger = new LoggerWrapper(listener);
    }

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            bridgeZipPath.unzip(bridgeInstallationPath);
            bridgeZipPath.delete();
            logger.info("Synopsys Bridge zip path: %s and bridge installation path: %s", bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            logger.error("An exception occurred while unzipping Synopsys Bridge zip file: " + e.getMessage());
        }
    }

    public String defaultBridgeInstallationPath(FilePath workspace, TaskListener listener) {

        logger.println("-------------------------------- Connection to node --------------------------------");

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        String separator = Utility.getDirectorySeparator(workspace, listener);
        String defaultInstallationPath = null;

        if (jenkins != null && workspace.isRemote()) {
            logger.info("Jenkins job is running on agent node remotely");
        } else {
            logger.info("Jenkins job is running on master node");
        }

        try {
            defaultInstallationPath = workspace.act(new HomeDirectoryTask(separator));
        } catch (IOException | InterruptedException e) {
            logger.error(LogMessages.FAILED_TO_FETCH_PLUGINS_DEFAULT_INSTALLATION_PATH, e.getMessage());
        }

        return defaultInstallationPath;
    }

    public void verifyAndCreateInstallationPath(String bridgeInstallationPath) {
        FilePath directory = new FilePath(workspace.getChannel(), bridgeInstallationPath);
        try {
            if (!directory.exists()) {
                directory.mkdirs();
                logger.info("Created bridge installation directory at: " + directory.getRemote());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to create directory: " + directory.getRemote());
        }
    }
}
