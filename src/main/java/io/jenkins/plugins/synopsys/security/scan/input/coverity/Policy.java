/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Policy {
    @JsonProperty("view")
    private String view;

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
