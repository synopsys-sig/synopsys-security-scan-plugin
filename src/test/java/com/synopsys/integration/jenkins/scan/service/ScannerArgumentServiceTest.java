package com.synopsys.integration.jenkins.scan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.synopsys.integration.jenkins.scan.global.BridgeParams;
import com.synopsys.integration.jenkins.scan.input.BlackDuck;

import com.synopsys.integration.jenkins.scan.input.bitbucket.Bitbucket;
import hudson.EnvVars;
import hudson.FilePath;

import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ScannerArgumentServiceTest {
    private BlackDuck blackDuck;
    private Bitbucket bitBucket;
    private ScannerArgumentService scannerArgumentService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final EnvVars envVarsMock = Mockito.mock(EnvVars.class);
    private final FilePath workspaceMock = Mockito.mock(FilePath.class);

    @BeforeEach
    void setUp() {
        blackDuck = new BlackDuck();
        blackDuck.setUrl("https://fake.blackduck.url");
        blackDuck.setToken("MDJDSROSVC56FAKEKEY");

        bitBucket = new Bitbucket();

        scannerArgumentService = new ScannerArgumentService(listenerMock, envVarsMock, workspaceMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
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
