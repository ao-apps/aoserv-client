/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class MasterServer extends AOServObject {

    private int pkey;
    private UserId username;
    private int server;

    public MasterServer() {
    }

    public MasterServer(
        int pkey,
        UserId username,
        int server
    ) {
        this.pkey = pkey;
        this.username = username;
        this.server = server;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }
}
