/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class BackupRetention {

    private short days;

    public BackupRetention() {
    }

    public BackupRetention(short days) {
        this.days = days;
    }

    public short getDays() {
        return days;
    }

    public void setDays(short days) {
        this.days = days;
    }
}
