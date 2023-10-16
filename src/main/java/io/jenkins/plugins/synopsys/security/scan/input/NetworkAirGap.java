/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.input;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkAirGap {
    @JsonProperty("airgap")
    private Boolean airgap;

    public Boolean getAirgap() {
        return airgap;
    }

    public void setAirgap(final Boolean airgap) {
        this.airgap = airgap;
    }
}
