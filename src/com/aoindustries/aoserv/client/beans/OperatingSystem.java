/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class OperatingSystem {

    private String name;
    private String display;
    private boolean isUnix;

    public OperatingSystem() {
    }

    public OperatingSystem(String name, String display, boolean is_unix) {
        this.name = name;
        this.display = display;
        this.isUnix = is_unix;
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

    public boolean isIsUnix() {
        return isUnix;
    }

    public void setIsUnix(boolean isUnix) {
        this.isUnix = isUnix;
    }
}
