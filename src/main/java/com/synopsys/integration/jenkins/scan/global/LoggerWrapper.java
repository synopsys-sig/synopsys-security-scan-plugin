/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.global;

import hudson.model.TaskListener;

public class LoggerWrapper {
    private final TaskListener listener;

    public LoggerWrapper(TaskListener listener) {
        this.listener = listener;
    }

    private void printMessage(String message) {
        listener.getLogger().println("[Security Scan] " + message);
    }

    public void info(String format, Object... args) {
        String message = String.format(format, args);
        printMessage("INFO: " + message);
    }

    public void warn(String format, Object... args) {
        String message = String.format(format, args);
        printMessage("WARN: " + message);
    }

    public void error(String format, Object... args) {
        String message = String.format(format, args);
        printMessage("ERROR: " + message);
    }

    public void println(String format, Object... args) {
        String message = String.format(format, args);
        listener.getLogger().println(message);
    }

    public void println() {
        listener.getLogger().println();
    }

}

