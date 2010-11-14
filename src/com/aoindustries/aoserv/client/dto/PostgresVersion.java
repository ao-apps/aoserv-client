/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresVersion extends AOServObject {

    private int version;
    private String minorVersion;
    private Integer postgisVersion;

    public PostgresVersion() {
    }

    public PostgresVersion(int version, String minorVersion, Integer postgisVersion) {
        this.version = version;
        this.minorVersion = minorVersion;
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
