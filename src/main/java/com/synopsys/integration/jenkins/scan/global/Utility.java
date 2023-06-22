package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;

import java.io.IOException;
import java.nio.file.Files;

public class Utility {

    public static FilePath createTempDir(String directoryName) throws IOException, InterruptedException {
        FilePath tempFilePath = new FilePath(Files.createTempDirectory(directoryName).toFile());
        tempFilePath.mkdirs();
        return tempFilePath;
    }

    public static void cleanupTempDir(FilePath tempDir) throws IOException, InterruptedException {
        tempDir.delete();
    }

}
