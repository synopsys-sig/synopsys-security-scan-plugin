package com.synopsys.integration.jenkins.scan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import com.synopsys.integration.jenkins.scan.input.bitbucket.BitBucket;
import hudson.FilePath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ScannerArgumentServiceTest {
    private BlackDuck blackDuck;
    private BitBucket bitBucket;
    private ScannerArgumentService scannerArgumentService;

    @BeforeEach
    void setUp() {
        blackDuck = new BlackDuck();
        blackDuck.setUrl("https://fake.blackduck.url");
        blackDuck.setToken("MDJDSROSVC56FAKEKEY");

        bitBucket = new BitBucket();

        scannerArgumentService = new ScannerArgumentService();
    }

    @Test
    void createBlackDuckInputJsonTest() {
        String inputJsonPath =scannerArgumentService.createBlackDuckInputJson(blackDuck, bitBucket);
        Path filePath = Paths.get(inputJsonPath);

        assertTrue(Files.exists(filePath), String.format("File %s does not exist at the specified path.", BridgeParams.BLACKDUCK_JSON_FILE_NAME));

        cleanup();
    }

    @Test
    void writeBlackDuckJsonToFileTest() {
        String jsonString = "{\"data\":{\"blackduck\":{\"url\":\"https://fake.blackduck.url\",\"token\":\"MDJDSROSVC56FAKEKEY\"}}}";

        String jsonPath = scannerArgumentService.writeBlackDuckJsonToFile(jsonString);
        String fileContent = null;
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(Files.exists(Path.of(jsonPath)), String.format("%s does not exist at the specified path.", BridgeParams.BLACKDUCK_JSON_FILE_NAME));
        assertEquals(jsonString,fileContent);

        cleanup();
    }

    public FilePath workspacePath() {
        String pathString = System.getProperty("user.dir").concat("/tmp");
        Path path = Paths.get(pathString);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new FilePath(path.toFile());
    }

    public void cleanup() {
        Path directory = Paths.get(workspacePath().getRemote());

        if (Files.exists(directory)) {
            try {
                Files.walk(directory)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
