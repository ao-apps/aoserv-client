package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetTicketBusinessCommand extends RemoteCommand<Boolean> {

    private static final long serialVersionUID = 1L;

    final private int ticketId;
    final private AccountingCode oldAccounting;
    final private AccountingCode newAccounting;

    public SetTicketBusinessCommand(
        @Param(name="ticketId") int ticketId,
        @Param(name="oldAccounting", nullable=true) AccountingCode oldAccounting,
        @Param(name="newAccounting", nullable=true) AccountingCode newAccounting
    ) {
        this.ticketId = ticketId;
        this.oldAccounting = oldAccounting;
        this.newAccounting = newAccounting;
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
    public boolean isReadOnlyCommand() {
        return false;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
