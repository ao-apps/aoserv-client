/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class Username {

    private UserId username;
    private String accounting;
    private Integer disableLog;

    public Username() {
    }

    public Username(UserId username, String accounting, Integer disableLog) {
        this.username = username;
        this.accounting = accounting;
        this.disableLog = disableLog;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
    }

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }
}
