/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class OperatingSystem extends AOServObject {

    private String name;
    private String display;
    private boolean unix;

    public OperatingSystem() {
    }

    public OperatingSystem(String name, String display, boolean is_unix) {
        this.name = name;
        this.display = display;
        this.unix = is_unix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isUnix() {
        return unix;
    }

    public void setUnix(boolean unix) {
        this.unix = unix;
    }
}
