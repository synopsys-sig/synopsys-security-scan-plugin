package com.synopsys.integration.jenkins.scan.extension.global;

import hudson.Extension;
import java.io.Serializable;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class ScannerGlobalConfig extends GlobalConfiguration implements Serializable {
    private static final long serialVersionUID = -3129542889827231427L;

    private String blackDuckUrl;

    private String blackDuckApiToken;

    private String synopsysBridgeDownloadUrl;

    @DataBoundConstructor
    public ScannerGlobalConfig() {
        load();
    }

    @DataBoundSetter
    public void setBlackDuckUrl(String blackDuckUrl) {
        this.blackDuckUrl = blackDuckUrl;
        save();
    }

    @DataBoundSetter
    public void setBlackDuckApiToken(String blackDuckCredentialsId) {
        this.blackDuckApiToken = blackDuckCredentialsId;
        save();
    }

    @DataBoundSetter
    public void setSynopsysBridgeDownloadUrl(String synopsysBridgeDownloadUrl) {
        this.synopsysBridgeDownloadUrl = synopsysBridgeDownloadUrl;
    }

    public String getBlackDuckUrl() {
        return blackDuckUrl;
    }

    public String getBlackDuckApiToken() {
        return blackDuckApiToken;
    }

    public String getSynopsysBridgeDownloadUrl() {
        return synopsysBridgeDownloadUrl;
    }

}
