package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import java.io.IOException;

public class Utility {
    public static void copyRepository(String targetDirectory, FilePath workspace, TaskListener listener) {
        FilePath targetDir = new FilePath(workspace.getChannel(), targetDirectory);
        try {
            FilePath gitDirectory = targetDir.child(".git");

            if (gitDirectory.exists()) {
                gitDirectory.deleteRecursive();
            }
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            workspace.copyRecursiveTo(targetDir);
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while copying the repository: " + e.getMessage());
        }
    }

    public static String defaultBridgeInstallationPath(FilePath workspace, TaskListener listener) {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        String separator = getDirectorySeparator(workspace, listener);
        String defaultInstallationPath = null;

        if (jenkins != null && workspace.isRemote()) {
            listener.getLogger().println("Jenkins is running on agent node remotely.");
        } else {
            listener.getLogger().println("Jenkins is running on the master node.");
        }

        try {
            defaultInstallationPath = workspace.act(new GetHomeDirectoryTask(separator));
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Failed to fetch plugin's default installation path.");
        }
        verifyAndCreateInstallationPath(defaultInstallationPath, workspace, listener);

        return defaultInstallationPath;
    }

    public static String getDirectorySeparator(FilePath workspace, TaskListener listener) {
        String os = null;
        if (workspace.isRemote()) {
            try {
                os = workspace.act(new GetOsNameTask());
            } catch (IOException | InterruptedException e) {
                listener.getLogger().println("Exception occurred while getting directory separator for the agent node: " + e.getMessage());
            }
        } else {
            os = System.getProperty("os.name").toLowerCase();
        }

        if (os.contains("win")) {
            return "\\";
        }  else {
            return "/";
        }
    }

    public static void verifyAndCreateInstallationPath(String bridgeInstallationPath, FilePath workspace, TaskListener listener) {
        FilePath directory = new FilePath(workspace.getChannel(), bridgeInstallationPath);
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            listener.getLogger().println("Created bridge installation directory at: " + directory.getRemote());
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Failed to create directory: " + directory.getRemote());

        }
    }

    public static void cleanupOtherFiles(FilePath workspace, TaskListener listener) {
        try {
            FilePath extensionsDir = workspace.child("extensions");
            if (extensionsDir.exists()) {
                extensionsDir.deleteRecursive();
            }

            FilePath licenseFile = workspace.child("LICENSE.txt");
            if (fileExistsIgnoreCase(licenseFile, listener)) {
                licenseFile.delete();
            }

            FilePath versionsFile = workspace.child("versions.txt");
            if (fileExistsIgnoreCase(versionsFile, listener)) {
                versionsFile.delete();
            }

            FilePath synopsysBridgeFile = workspace.child("synopsys-bridge");
            if (fileExistsIgnoreCase(synopsysBridgeFile, listener)) {
                synopsysBridgeFile.delete();
            } else {
                FilePath synopsysBridgeExeFile = workspace.child("synopsys-bridge.exe");
                if (fileExistsIgnoreCase(synopsysBridgeExeFile, listener)) {
                    synopsysBridgeExeFile.delete();
                }
            }
        } catch (Exception e) {
            listener.getLogger().println("Failed to clean up files: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }
    }

    private static boolean fileExistsIgnoreCase(FilePath workspace, TaskListener listener) {
        try {
            FilePath[] files = workspace.getParent().list(workspace.getName().toLowerCase());

            for (FilePath f : files) {
                if (f.getName().equalsIgnoreCase(workspace.getName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            listener.getLogger().println("Failed to check file existence: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
        }

        return false;
    }

    public static void removeFile(String filePath, FilePath workspace, TaskListener listener) {
        try {
            FilePath file = new FilePath(workspace.getChannel(), filePath);
            file = file.absolutize();

            if (file.exists()) {
                file.delete();
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while cleaning up file: " + e.getMessage());
        }
    }

    public static boolean isStringNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
