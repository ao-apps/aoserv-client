/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresVersion {

    private int version;
    private String minorVersion;
    private Integer postgisVersion;

    public PostgresVersion() {
    }

    public PostgresVersion(int version, String minorVersion, Integer postgisVersion) {
        this.version = version;
        this.minorVersion = minorVersion.intern();
        this.postgisVersion = postgisVersion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Integer getPostgisVersion() {
        return postgisVersion;
    }

    public void setPostgisVersion(Integer postgisVersion) {
        this.postgisVersion = postgisVersion;
    }
}
