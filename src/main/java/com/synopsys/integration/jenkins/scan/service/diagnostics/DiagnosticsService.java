package com.synopsys.integration.jenkins.scan.service.diagnostics;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.ArtifactArchiver;

public class DiagnosticsService {
    private final Run<?, ?> run;
    private final TaskListener listener;
    private final Launcher launcher;
    private final EnvVars envVars;
    private final ArtifactArchiver artifactArchiver;

    public DiagnosticsService(Run<?, ?> run, TaskListener listener, Launcher launcher, EnvVars envVars,
                              ArtifactArchiver artifactArchiver) {
        this.run = run;
        this.listener = listener;
        this.launcher = launcher;
        this.envVars = envVars;
        this.artifactArchiver = artifactArchiver;
    }

    public void archiveDiagnostics(FilePath diagnosticsPath) {
        try {
            if (diagnosticsPath.exists()) {
                listener.getLogger().println("Archiving diagnostics from: " + diagnosticsPath.getRemote());

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
