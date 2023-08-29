/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.exception;

public class ScannerJenkinsException extends Exception {
    private static final long serialVersionUID = 3172941819259598261L;
    
    public ScannerJenkinsException() {
        super();
    }
    
    public ScannerJenkinsException(String message) {
        super(message);
    }
    
    public ScannerJenkinsException(Throwable cause) {
        super(cause);
    }
    
    public ScannerJenkinsException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
