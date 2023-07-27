package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;

import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utility {
    public static FilePath createTempDir(String directoryName) throws IOException, InterruptedException {
        FilePath tempFilePath = new FilePath(Files.createTempDirectory(directoryName).toFile());
        tempFilePath.mkdirs();
        return tempFilePath;
    }

    public static void cleanupTempDir(FilePath tempDir){
        try {
            if (tempDir.exists()) {
                tempDir.deleteRecursive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }*/

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
            listener.getLogger().println("Method: defaultBridgeInstallationPath() Jenkins is running on agent node remotely.");
        } else {
            listener.getLogger().println("Method: defaultBridgeInstallationPath() Jenkins is running on the master node.");
        }

        try {
            defaultInstallationPath = workspace.act(new GetHomeDirectoryTask(separator));
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("Failed to fetch plugin's default installation path.");
        }

        listener.getLogger().println("Method: defaultBridgeInstallationPath() Plugin's default installation path for this build is : " + defaultInstallationPath);
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

   /* public static FilePath stringToFilePath(String path) {
        FilePath filePath = new FilePath(new File(path));
        return filePath;
    }

    public static File stringToFile(String path) {
        File file = new File(path);
        return file;
    }

    public static File filePathToFile(FilePath filePath) {
        String filePathString = filePath.getRemote();
        File file = new File(filePathString);
        return file;
    }*/

    public static void cleanupInputJson(String inputJsonPath, FilePath workspace, TaskListener listener) {
        listener.getLogger().println("Method: cleanupInputJson blackduck_input.json path: " + inputJsonPath);
        try {
            FilePath file = new FilePath(workspace.getChannel(), inputJsonPath);
            file = file.absolutize();

            if (file.exists()) {
                file.delete();
            }
        } catch (IOException | InterruptedException e) {
            listener.getLogger().println("An exception occurred while cleaning up the input JSON file: " + e.getMessage());
        }
    }

    public static boolean isStringNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
