package com.synopsys.integration.jenkins.scan.bridge;

import hudson.FilePath;
import hudson.model.TaskListener;

import java.util.regex.Pattern;

public class BridgeInstall {
    private final TaskListener listener;
    private final FilePath workspace;

    public BridgeInstall(TaskListener listener, FilePath workspace) {
        this.listener = listener;
        this.workspace = workspace;
    }

    public void installSynopsysBridge(FilePath bridgeZipPath, FilePath bridgeInstallationPath) {
        try {
            listener.getLogger().println("Method: installSynopsysBridge() Bridge zip path: " + bridgeZipPath);
            listener.getLogger().println("Method: installSynopsysBridge() Bridge installation path: " + bridgeInstallationPath);

            listener.getLogger().println("Method: installSynopsysBridge() Temp unzipped Path: " + workspace.getRemote());

            bridgeZipPath.unzip(workspace);
            renameVersionFileIfNecessary();
            bridgeZipPath.delete();

            listener.getLogger().printf("Bridge zip path: %s and bridge installation path: %s%n",
                    bridgeZipPath.getRemote(), bridgeInstallationPath.getRemote());
        } catch (Exception e) {
            listener.getLogger().println("Synopsys bridge unzipping failed: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }

    void renameVersionFileIfNecessary() {
        try {
            FilePath[] files = workspace.list("versions\\d+\\.txt");
            Pattern pattern = Pattern.compile("versions\\d+\\.txt");

            if (files.length > 0) {
                for (FilePath file : files) {
                    String fileName = file.getName();
                    listener.getLogger().println(">>>>>>>>>>>>>>>>>>>>>. ............. Found version file: " + fileName);
                    if (pattern.matcher(fileName).matches()) {
                        listener.getLogger().println("======================= Changing versions file name.");
                        file.renameTo(file.getParent().child("versions.txt"));
                    }
                }
            }
        } catch (Exception e) {
            listener.getLogger().println("Exception occurred while renaming versions.txt file in the workspace directory.");
            e.printStackTrace();
        }
    }
}
