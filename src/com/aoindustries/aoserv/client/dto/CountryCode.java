/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class CountryCode extends AOServObject {

    private String code;
    private String name;
    private boolean chargeComSupported;
    private String chargeComName;

    public CountryCode() {
    }

    public CountryCode(String code, String name, boolean chargeComSupported, String chargeComName) {
        this.code = code;
        this.name = name;
        this.chargeComSupported = chargeComSupported;
        this.chargeComName = chargeComName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChargeComSupported() {
        return chargeComSupported;
    }

    public void setChargeComSupported(boolean chargeComSupported) {
        this.chargeComSupported = chargeComSupported;
    }

    public String getChargeComName() {
        return chargeComName;
    }

    public void setChargeComName(String chargeComName) {
        this.chargeComName = chargeComName;
    }
}
