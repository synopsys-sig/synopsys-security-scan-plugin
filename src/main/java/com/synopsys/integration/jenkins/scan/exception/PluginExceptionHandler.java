/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.exception;

public class PluginExceptionHandler extends Exception {
    private static final long serialVersionUID = 3172941819259598261L;

    public PluginExceptionHandler() {
        super();
    }

    public PluginExceptionHandler(String message) {
        super(message);
    }

    public PluginExceptionHandler(Throwable cause) {
        super(cause);
    }

    public PluginExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
