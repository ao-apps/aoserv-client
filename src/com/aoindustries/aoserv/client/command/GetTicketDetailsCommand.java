package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class GetTicketDetailsCommand extends RemoteCommand<String> {

    // TODO: private static final long serialVersionUID = 1L;

    final private int ticketId;

    public GetTicketDetailsCommand(
        @Param(name="ticket") Ticket ticket
    ) {
        this.ticketId = ticket.getTicketId();
    }

    public int getTicketId() {
        return ticketId;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
