/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class PostgresEncoding {

    private int pkey;
    private String encoding;
    private int postgresVersion;

    public PostgresEncoding() {
    }

    public PostgresEncoding(int pkey, String encoding, int postgresVersion) {
        this.pkey = pkey;
        this.encoding = encoding;
        this.postgresVersion = postgresVersion;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getPostgresVersion() {
        return postgresVersion;
    }

    public void setPostgresVersion(int postgresVersion) {
        this.postgresVersion = postgresVersion;
    }
}
