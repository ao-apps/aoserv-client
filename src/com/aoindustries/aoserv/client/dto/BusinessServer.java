/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class BusinessServer extends AOServObject {

    private int pkey;
    private AccountingCode accounting;
    private int server;
    private boolean isDefault;
    private boolean canVncConsole;

    public BusinessServer() {
    }

    public BusinessServer(int pkey, AccountingCode accounting, int server, boolean isDefault, boolean canVncConsole) {
        this.pkey = pkey;
        this.accounting = accounting;
        this.server = server;
        this.isDefault = isDefault;
        this.canVncConsole = canVncConsole;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isCanVncConsole() {
        return canVncConsole;
    }

    public void setCanVncConsole(boolean canVncConsole) {
        this.canVncConsole = canVncConsole;
    }
}
