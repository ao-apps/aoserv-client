/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetTicketBusinessCommand extends RemoteCommand<Boolean> {

    private static final long serialVersionUID = -8666786980181110930L;

    final private int ticketId;
    final private AccountingCode oldAccounting;
    final private AccountingCode newAccounting;

    public SetTicketBusinessCommand(
        @Param(name="ticket") Ticket ticket,
        @Param(name="oldBusiness", nullable=true) Business oldBusiness,
        @Param(name="newBusiness", nullable=true) Business newBusiness
    ) {
        this.ticketId = ticket.getTicketId();
        this.oldAccounting = oldBusiness==null ? null : oldBusiness.getAccounting();
        this.newAccounting = newBusiness==null ? null : newBusiness.getAccounting();
    }

    public int getTicketId() {
        return ticketId;
    }

    public AccountingCode getOldAccounting() {
        return oldAccounting;
    }

    public AccountingCode getNewAccounting() {
        return newAccounting;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
