/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package io.jenkins.plugins.synopsys.security.scan.input.bitbucket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pull {
    @JsonProperty("number")
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
