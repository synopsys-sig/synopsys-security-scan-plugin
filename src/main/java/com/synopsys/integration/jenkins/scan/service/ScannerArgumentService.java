package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;

import org.apache.tools.ant.types.Commandline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author akib @Date 6/19/23
 */
public class ScannerArgumentService {

    public List<String> getCommandLineArgs(String blackDuckArgs, String bridgeArgs) {
        List<String> commandLineArgs = new ArrayList<>();
        commandLineArgs.addAll(getInitialBridgeArgs());
        //need to parse blackduck args
        commandLineArgs.addAll(parseBridgeArgumentString(bridgeArgs));
        return commandLineArgs;
    }

    private List<String> parseBridgeArgumentString(String bridgeArgs) {
        return Arrays.stream(Commandline.translateCommandline(bridgeArgs))
            .map(arg -> arg.split("\\r?\\n"))
            .flatMap(Arrays::stream)
            .filter(Objects::nonNull).collect(Collectors.toList());
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
