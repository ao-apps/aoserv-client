/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Reseller extends AOServObject {

    private AccountingCode accounting;
    private boolean ticketAutoEscalate;

    public Reseller() {
    }

    public Reseller(AccountingCode accounting, boolean ticketAutoEscalate) {
        this.accounting = accounting;
        this.ticketAutoEscalate = ticketAutoEscalate;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public boolean isTicketAutoEscalate() {
        return ticketAutoEscalate;
    }

    public void setTicketAutoEscalate(boolean ticketAutoEscalate) {
        this.ticketAutoEscalate = ticketAutoEscalate;
    }
}
