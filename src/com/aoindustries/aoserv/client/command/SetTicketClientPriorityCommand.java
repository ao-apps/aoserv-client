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
final public class SetTicketClientPriorityCommand extends RemoteCommand<Void> {

    // TODO: private static final long serialVersionUID = 1L;

    final private int ticketId;
    final private String clientPriority;

    public SetTicketClientPriorityCommand(
        @Param(name="ticket") Ticket ticket,
        @Param(name="clientPriority") TicketPriority clientPriority
    ) {
        this.ticketId = ticket.getTicketId();
        this.clientPriority = clientPriority.getPriority();
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getClientPriority() {
        return clientPriority;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
