/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetTicketSummaryCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1561706547408516262L;

    final private int ticketId;
    final private String summary;

    public SetTicketSummaryCommand(
        @Param(name="ticket") Ticket ticket,
        @Param(name="summary") String summary
    ) {
        this.ticketId = ticket.getTicketId();
        this.summary = summary;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
