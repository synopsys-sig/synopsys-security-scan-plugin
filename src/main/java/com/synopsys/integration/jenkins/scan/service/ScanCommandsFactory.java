package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.ScanPipelineCommands;
import com.synopsys.integration.jenkins.scan.SecurityScanner;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.TaskListener;

public class ScanCommandsFactory {
    private final TaskListener listener;
    private final EnvVars envVars;
    private final FilePath workspace;

    private ScanCommandsFactory(TaskListener listener, EnvVars envVars, FilePath workspace) throws AbortException {
        this.listener = listener;
        this.envVars = envVars;

        if (workspace == null) {
            throw new AbortException(ExceptionMessages.NULL_WORKSPACE);
        }
        this.workspace = workspace;
    }

    public static ScanPipelineCommands createPipelineCommand(TaskListener listener, EnvVars envVars, Launcher launcher, Node node, FilePath workspace) {
        return new ScanPipelineCommands(
            new SecurityScanner(listener, launcher, workspace, envVars, new ScannerArgumentService()));
    }
    
}
