package com.synopsys.integration.jenkins.scan.service;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import hudson.FilePath;
import java.io.File;
import org.junit.jupiter.api.Test;

public class DiagnosticsServiceTest {
    private final DiagnosticsService diagnosticsServiceMock = mock(DiagnosticsService.class);

    @Test
    void archiveArtifactoryTest() {
        String diagnosticsDir = "/path/to/synopsys-bridge/.bridge";
        FilePath diagnosticsPath = new FilePath(new File(diagnosticsDir));

        diagnosticsServiceMock.archiveDiagnostics(diagnosticsPath);

        verify(diagnosticsServiceMock, times(1))
            .archiveDiagnostics(diagnosticsPath);
    }
    
}
