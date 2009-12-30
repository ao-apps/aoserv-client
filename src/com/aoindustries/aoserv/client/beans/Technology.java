/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class Technology {

    private int pkey;
    private String name;
    private String technologyClass;

    public Technology() {
    }

    public Technology(int pkey, String name, String technologyClass) {
        this.pkey = pkey;
        this.name = name;
        this.technologyClass = technologyClass;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTechnologyClass() {
        return technologyClass;
    }

    public void setTechnologyClass(String clazz) {
        this.technologyClass = clazz;
    }
}
