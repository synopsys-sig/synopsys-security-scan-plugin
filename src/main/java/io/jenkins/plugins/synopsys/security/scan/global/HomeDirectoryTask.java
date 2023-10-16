/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.global;

import hudson.remoting.VirtualChannel;
import java.io.File;
import jenkins.MasterToSlaveFileCallable;

public class HomeDirectoryTask extends MasterToSlaveFileCallable<String> {
    private final String separator;

    public HomeDirectoryTask(String separator) {
        this.separator = separator;
    }

    @Override
    public String invoke(File f, VirtualChannel channel) {
        return System.getProperty("user.home").concat(separator).concat(ApplicationConstants.DEFAULT_DIRECTORY_NAME);
    }
}
