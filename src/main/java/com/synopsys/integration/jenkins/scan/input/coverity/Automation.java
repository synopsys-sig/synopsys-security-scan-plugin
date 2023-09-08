/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Automation {
    @JsonProperty("prComment")
    private Boolean prComment;

    public Boolean getPrComment() {
        return prComment;
    }

    public void setPrComment(Boolean prComment) {
        this.prComment = prComment;
    }
}
