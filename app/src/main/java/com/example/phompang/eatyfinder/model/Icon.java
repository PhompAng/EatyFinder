package com.example.phompang.eatyfinder.model;

import java.io.Serializable;

/**
 * Created by phompang on 12/2/2016 AD.
 */

public class Icon implements Serializable {
    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
