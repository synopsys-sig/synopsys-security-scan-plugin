package com.synopsys.integration.jenkins.scan.input;
/**
 * @author akib @Date 6/20/23
 */
public class Automation {

    private boolean fixpr;
    private boolean prcomment;

    public boolean isFixpr() {
        return fixpr;
    }

    public void setFixpr(boolean fixpr) {
        this.fixpr = fixpr;
    }

    public boolean isPrcomment() {
        return prcomment;
    }

    public void setPrcomment(boolean prcomment) {
        this.prcomment = prcomment;
    }
}
