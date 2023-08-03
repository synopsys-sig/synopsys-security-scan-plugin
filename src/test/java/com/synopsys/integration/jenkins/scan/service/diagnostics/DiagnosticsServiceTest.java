package com.synopsys.integration.jenkins.scan.service.diagnostics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.ArtifactArchiver;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DiagnosticsServiceTest {
    @Mock
    private Run<?, ?> runMock;
    @Mock
    private TaskListener listenerMock;
    @Mock
    private Launcher launcherMock;
    @Mock
    private EnvVars envVarsMock;
    @Mock
    private ArtifactArchiver artifactArchiverMock;
    private DiagnosticsService diagnosticsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(listenerMock.getLogger()).thenReturn(mock(PrintStream.class));
        when(artifactArchiverMock.getArtifacts()).thenReturn(ApplicationConstants.ALL_FILES_WILDCARD_SYMBOL);
        diagnosticsService = spy(new DiagnosticsService(runMock, listenerMock, launcherMock, envVarsMock, artifactArchiverMock));
    }

    @Test
    public void testArchiveDiagnosticsWhenPathExistsShouldArchiveSuccessfully() {
        FilePath homePath = new FilePath(new File(System.getProperty("user.home")));
        FilePath diagnosticsPath = homePath.child(ApplicationConstants.DEFAULT_DIRECTORY_NAME)
            .child(ApplicationConstants.BRIDGE_DIAGNOSTICS_DIRECTORY);

        try {
            assertTrue(diagnosticsPath.exists());
            
            doNothing().when(artifactArchiverMock).perform(eq(runMock), eq(diagnosticsPath), eq(envVarsMock), eq(launcherMock), eq(listenerMock));

            diagnosticsService.archiveDiagnostics(diagnosticsPath);
            verify(artifactArchiverMock).perform(runMock, diagnosticsPath, envVarsMock, launcherMock, listenerMock);
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception occurred during testing for archiveDiagnostics method: " + e.getMessage());
        }
    }

    @Test
    public void testArchiveDiagnosticsWhenPathDoesNotExistShouldPrintError() {
        String nonExistingPath = "/path/to/nonexistent";
        FilePath diagnosticsPath = new FilePath(new File(nonExistingPath));

        try{
            assertFalse(diagnosticsPath.exists());

            diagnosticsService.archiveDiagnostics(diagnosticsPath);
            verify(artifactArchiverMock, never()).perform(any(), any(), any(), any(), any());
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception occurred during testing for archiveDiagnostics method: " + e.getMessage());
        }
    }
}


