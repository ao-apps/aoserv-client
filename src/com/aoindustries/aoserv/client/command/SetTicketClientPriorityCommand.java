package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetTicketClientPriorityCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int ticketId;
    final private String clientPriority;

    public SetTicketClientPriorityCommand(
        @Param(name="ticketId") int ticketId,
        @Param(name="clientPriority") String clientPriority
    ) {
        this.ticketId = ticketId;
        this.clientPriority = clientPriority;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getClientPriority() {
        return clientPriority;
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
