package com.synopsys.integration.jenkins.scan;




import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author akib @Date 6/15/23
 */
public class ScanPipelineCommands {
    private final SecurityScanner scanner;

    public ScanPipelineCommands(SecurityScanner scanner) {
        this.scanner = scanner;
    }

    public int runScanner(String param1, String param2) throws IOException, InterruptedException {
        // need to validate and prepare params to execute
        List<String> blackDuckArgs = Arrays.asList(param1.split(" "));
        List<String> bridgeArgs = Arrays.asList(param2.split(" "));
        return scanner.runScanner(blackDuckArgs, bridgeArgs);
    }

}
