/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class TicketAssignment {

    private int pkey;
    private int ticket;
    private AccountingCode reseller;
    private UserId administrator;

    public TicketAssignment() {
    }

    public TicketAssignment(
        int pkey,
        int ticket,
        AccountingCode reseller,
        UserId administrator
    ) {
        this.pkey = pkey;
        this.ticket = ticket;
        this.reseller = reseller;
        this.administrator = administrator;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public AccountingCode getReseller() {
        return reseller;
    }

    public void setReseller(AccountingCode reseller) {
        this.reseller = reseller;
    }

    public UserId getAdministrator() {
        return administrator;
    }

    public void setAdministrator(UserId administrator) {
        this.administrator = administrator;
    }
}
