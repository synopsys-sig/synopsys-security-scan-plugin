package com.synopsys.integration.jenkins.scan.global;

import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import java.io.File;
import java.io.IOException;

public class HomeDirectoryTask extends MasterToSlaveFileCallable<String> {
    private final String separator;

    public HomeDirectoryTask(String separator) {
        this.separator = separator;
    }

    @Override
    public String invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
        return System.getProperty("user.home").concat(separator).concat(ApplicationConstants.DEFAULT_DIRECTORY_NAME);
    }
}