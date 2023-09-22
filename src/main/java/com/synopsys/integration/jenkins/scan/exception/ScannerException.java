/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.exception;

public class ScannerException extends Exception {
    private static final long serialVersionUID = 3172941819259598261L;

    public ScannerException() {
        super();
    }

    public ScannerException(String message) {
        super(message);
    }
}
