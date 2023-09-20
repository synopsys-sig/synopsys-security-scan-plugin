/*
 * synopsys-security-scan-plugin
 *
 * Copyright (c) 2023 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.jenkins.scan.input.polaris;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Branch {
    @JsonProperty("name")
    private String name;

//    @JsonProperty("parent")
//    private Parent parent;

//    public Branch() {
//        parent = new Parent();
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Parent getParent() {
//        return parent;
//    }

//    public void setParent(Parent parent) {
//        this.parent = parent;
//    }
}
