/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.global;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import java.io.File;
import jenkins.MasterToSlaveFileCallable;

public class OsNameTask extends MasterToSlaveFileCallable<String> implements FilePath.FileCallable<String> {
    @Override
    public String invoke(File workspace, VirtualChannel channel) {
        return System.getProperty("os.name").toLowerCase();
    }
}