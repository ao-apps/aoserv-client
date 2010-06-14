/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PaymentType {

    private String name;
    private boolean isActive;
    private boolean allowWeb;

    public PaymentType() {
    }

    public PaymentType(String name, boolean isActive, boolean allowWeb) {
        this.name = name;
        this.isActive = isActive;
        this.allowWeb = allowWeb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isAllowWeb() {
        return allowWeb;
    }

    public void setAllowWeb(boolean allowWeb) {
        this.allowWeb = allowWeb;
    }
}
