/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class DisableLog extends AOServObject {

    private int pkey;
    private long time;
    private AccountingCode accounting;
    private UserId disabledBy;
    private String disableReason;

    public DisableLog() {
    }

    public DisableLog(
        int pkey,
        long time,
        AccountingCode accounting,
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

    public Calendar getTime() {
        return DtoUtils.getCalendar(time);
    }

    public void setTime(Calendar time) {
        this.time = time.getTimeInMillis();
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
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
