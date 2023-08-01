package com.synopsys.integration.jenkins.scan.service;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.ArtifactArchiver;

public class DiagnosticsService {
    public static final String ALL_FILES_WILDCARD_SYMBOL = "**";
    private final Run<?, ?> run;
    private final TaskListener listener;
    private final Launcher launcher;
    private final EnvVars envVars;

    public DiagnosticsService( Run<?, ?> run, TaskListener listener, Launcher launcher, EnvVars envVars) {
        this.run = run;
        this.listener = listener;
        this.launcher = launcher;
        this.envVars = envVars;
    }

    public void archiveDiagnostics(FilePath diagnosticsPath) {
        listener.getLogger().println("Archiving diagnostics from: " + diagnosticsPath.getRemote());

        try {
            if (diagnosticsPath.exists()) {
                ArtifactArchiver artifactArchiver = new ArtifactArchiver(ALL_FILES_WILDCARD_SYMBOL);
                artifactArchiver.perform(run, diagnosticsPath, envVars, launcher, listener);
            } else {
                listener.getLogger().println("Diagnostics path not found at: " + diagnosticsPath.getRemote());
                return;
            }
        } catch (Exception e) {
            listener.getLogger().println("Archiving diagnostics failed! Reason: " + e.getMessage());
            return;
        }

        listener.getLogger().println("Diagnostics archived successfully in archived artifactory");
    }

}
