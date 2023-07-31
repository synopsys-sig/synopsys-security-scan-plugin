package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;

public class GetOsNameTask implements FilePath.FileCallable<String> {
    @Override
    public String invoke(File workspace, VirtualChannel channel) throws IOException, InterruptedException {
        return System.getProperty("os.name").toLowerCase();
    }
    @Override
    public void checkRoles(RoleChecker checker) throws SecurityException {
    }
}