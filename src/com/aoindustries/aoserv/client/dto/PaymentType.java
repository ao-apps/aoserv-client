/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PaymentType extends AOServObject {

    private String name;
    private boolean active;
    private boolean allowWeb;

    public PaymentType() {
    }

    public PaymentType(String name, boolean active, boolean allowWeb) {
        this.name = name;
        this.active = active;
        this.allowWeb = allowWeb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAllowWeb() {
        return allowWeb;
    }

    public void setAllowWeb(boolean allowWeb) {
        this.allowWeb = allowWeb;
    }
}
