/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class FtpGuestUser extends AOServObject {

    private int linuxAccount;

    public FtpGuestUser() {
    }

    public FtpGuestUser(int linuxAccount) {
        this.linuxAccount = linuxAccount;
    }

    public int getLinuxAccount() {
        return linuxAccount;
    }

    public void setLinuxAccount(int linuxAccount) {
        this.linuxAccount = linuxAccount;
    }
}
