package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.model.TaskListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UtilityTest {
    private FilePath workspace;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);

    @BeforeEach
    void setup() {
        workspace = new FilePath(new File(getHomeDirectory()));
        when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    public void getDirectorySeparatorTest() {

        String separator = Utility.getDirectorySeparator(workspace, listenerMock);
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("win")) {
            assertEquals("\\", separator);
        } else {
            assertEquals("/", separator);
        }
    }

    @Test
    public void getAgentOsTest() {
        String os = Utility.getAgentOs(workspace, listenerMock);

        assertEquals(System.getProperty("os.name").toLowerCase(), os);
    }

    @Test
    public void testRemoveFile() throws IOException {
        File tempFile = new File(getHomeDirectory(), "testfile.txt");
        tempFile.createNewFile();
        String filePath = tempFile.getAbsolutePath();

        Utility.removeFile(filePath, workspace, listenerMock);

        assertFalse(tempFile.exists());
    }

    @Test
    public void isStringNullOrBlankTest() {
        String str = null;
        String emptyString = "";
        String emptyStringContainingSpace = "   ";
        String validString = " This is a valid string  ";

        assertTrue(Utility.isStringNullOrBlank(str));
        assertTrue(Utility.isStringNullOrBlank(emptyString));
        assertTrue(Utility.isStringNullOrBlank(emptyStringContainingSpace));
        assertFalse(Utility.isStringNullOrBlank(validString));
    }

    public String getHomeDirectory() {
        return System.getProperty("user.home");
    }
}
