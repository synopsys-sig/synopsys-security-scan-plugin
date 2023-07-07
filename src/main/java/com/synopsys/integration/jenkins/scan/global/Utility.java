package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import org.apache.commons.io.FileUtils;

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

    public static void deleteDirectory(File directory) {
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
    }

    public static void copyRepository(String pluginWorkspaceDirectory, String targetDirectory) {
        File workspaceDirectory = Utility.stringToFile(pluginWorkspaceDirectory);
        File targetDir = Utility.stringToFile(targetDirectory);

        File gitDirectory = new File(targetDirectory, ".git");

        if (gitDirectory.exists() && gitDirectory.isDirectory()) {
            Utility.deleteDirectory(gitDirectory);
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        try {
            FileUtils.copyDirectory(workspaceDirectory, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String defaultBridgeInstallationPath() {
        String defaultInstallationPath = null;

        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            defaultInstallationPath = String.join("\\", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            defaultInstallationPath = String.join("/", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }

        verifyAndCreateInstallationPath(defaultInstallationPath);

        return defaultInstallationPath;
    }

    public static void verifyAndCreateInstallationPath(String bridgeInstallationPath) {
        File directory = new File(bridgeInstallationPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println("Failed to create directory: " + bridgeInstallationPath);
            }
        }
    }

    public static FilePath stringToFilePath(String path) {
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
    }

    public static void cleanupInputJson(String inputJsonPath) {
        File file = new File(inputJsonPath);

        if (file.exists()) {
            file.delete();
        }
    }
}
