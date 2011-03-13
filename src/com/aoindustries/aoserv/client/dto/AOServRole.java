/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class AOServRole extends AOServObject {

    private int pkey;
    private AccountingCode accounting;
    private String name;

    public AOServRole() {
    }

    public AOServRole(int pkey, AccountingCode accounting, String name) {
        this.pkey = pkey;
        this.accounting = accounting;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
