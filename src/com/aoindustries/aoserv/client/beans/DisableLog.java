/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class DisableLog {

    private int pkey;
    private Date time;
    private String accounting;
    private UserId disabledBy;
    private String disableReason;

    public DisableLog() {
    }

    public DisableLog(
        int pkey,
        Date time,
        String accounting,
        UserId disabledBy,
        String disableReason
    ) {
        this.pkey = pkey;
        this.time = time;
        this.accounting = accounting;
        this.disabledBy = disabledBy;
        this.disableReason = disableReason;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
    }

    public UserId getDisabledBy() {
        return disabledBy;
    }

    public void setDisabledBy(UserId disabledBy) {
        this.disabledBy = disabledBy;
    }

    public String getDisableReason() {
        return disableReason;
    }

    public void setDisableReason(String disableReason) {
        this.disableReason = disableReason;
    }
}
