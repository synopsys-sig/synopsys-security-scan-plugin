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
