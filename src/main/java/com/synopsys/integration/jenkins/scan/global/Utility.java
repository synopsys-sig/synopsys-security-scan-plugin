package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akib @Date 6/16/23
 */
public class Utility {

    public static FilePath createTempDir(String directoryName) throws IOException, InterruptedException {
        FilePath tempFilePath = new FilePath(Files.createTempDirectory(directoryName).toFile());
        tempFilePath.mkdirs();
        return tempFilePath;
    }

    public static void cleanupTempDir(FilePath tempDir) throws IOException, InterruptedException {
        tempDir.delete();
    }

    public static List<String> getInitialBridgeArgs() {
        List<String> initBridgeArgs = new ArrayList<>();
        initBridgeArgs.add(ApplicationConstants.SYNOPSYS_BRIDGE_RUN_COMMAND);
//        initBridgeArgs.add("--stage");
//        initBridgeArgs.add("blackduck");
//        initBridgeArgs.add("--input");
        return initBridgeArgs;
    }

}
