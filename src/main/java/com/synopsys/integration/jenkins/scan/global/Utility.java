package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.model.TaskListener;
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
            listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_COPYING_REPO, e.getMessage());
        }
    }

    public static String getDirectorySeparator(FilePath workspace, TaskListener listener) {
        String os = getAgentOs(workspace, listener);

        if (os != null && os.contains("win")) {
            return "\\";
        }  else {
            return "/";
        }
    }

    public static String getAgentOs(FilePath workspace, TaskListener listener) {
        String os =  null;

        if (workspace.isRemote()) {
            try {
                os = workspace.act(new OsNameTask());
            } catch (IOException | InterruptedException e) {
                listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_GETTING_OS_INFO_FROM_AGENT_NODE, e.getMessage());
            }
        } else {
            os = System.getProperty("os.name").toLowerCase();
        }

        return os;
    }

    public static void verifyAndCreateInstallationPath(String bridgeInstallationPath, FilePath workspace, TaskListener listener) {
        FilePath directory = new FilePath(workspace.getChannel(), bridgeInstallationPath);
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
            listener.getLogger().printf(LogMessages.BRIDGE_INSTALLATION_DIRECTORY_CREATED, directory.getRemote());
        } catch (IOException | InterruptedException e) {
            listener.getLogger().printf(LogMessages.FAILED_TO_CREATE_DIRECTORY, directory.getRemote());

        }
    }

    public static void cleanupOtherFiles(FilePath workspace, TaskListener listener) {
        try {
            FilePath extensionsDir = workspace.child(ApplicationConstants.EXTENSIONS_DIRECTORY);
            if (extensionsDir.exists()) {
                extensionsDir.deleteRecursive();
            }

            FilePath licenseFile = workspace.child(ApplicationConstants.LICENSE_FILE);
            if (fileExistsIgnoreCase(licenseFile, listener)) {
                licenseFile.delete();
            }

            FilePath versionsFile = workspace.child(ApplicationConstants.VERSION_FILE);
            if (fileExistsIgnoreCase(versionsFile, listener)) {
                versionsFile.delete();
            }

            FilePath synopsysBridgeFile = workspace.child(ApplicationConstants.BRIDGE_BINARY);
            if (fileExistsIgnoreCase(synopsysBridgeFile, listener)) {
                synopsysBridgeFile.delete();
            } else {
                FilePath synopsysBridgeExeFile = workspace.child(ApplicationConstants.BRIDGE_BINARY_WINDOWS);
                if (fileExistsIgnoreCase(synopsysBridgeExeFile, listener)) {
                    synopsysBridgeExeFile.delete();
                }
            }
        } catch (Exception e) {
            listener.getLogger().printf(LogMessages.FAILED_TO_CLEAN_UP_FILES, e.getMessage());
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
            listener.getLogger().printf(LogMessages.FAILED_TO_CHECK_FILE_EXISTENCE, e.getMessage());
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
            listener.getLogger().printf(LogMessages.EXCEPTION_OCCURRED_WHILE_DELETING_FILE, e.getMessage());
        }
    }

    public static boolean isStringNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
