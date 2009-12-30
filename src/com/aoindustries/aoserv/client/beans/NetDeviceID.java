/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class NetDeviceID {

    private String name;
    private boolean isLoopback;

    public NetDeviceID() {
    }

    public NetDeviceID(String name, boolean isLoopback) {
        this.name = name;
        this.isLoopback = isLoopback;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsLoopback() {
        return isLoopback;
    }

    public void setIsLoopback(boolean isLoopback) {
        this.isLoopback = isLoopback;
    }
}