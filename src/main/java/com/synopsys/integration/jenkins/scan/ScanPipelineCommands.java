package com.synopsys.integration.jenkins.scan;




import java.io.IOException;

/**
 * @author akib @Date 6/15/23
 */
public class ScanPipelineCommands {
    private final SecurityScanner scanner;

    public ScanPipelineCommands(SecurityScanner scanner) {
        this.scanner = scanner;
    }

    public int runScanner(String param1, String param2) throws IOException, InterruptedException {
        return scanner.runScanner();
    }

}
