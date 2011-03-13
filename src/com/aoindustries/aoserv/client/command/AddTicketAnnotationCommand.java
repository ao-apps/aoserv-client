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
final public class AddTicketAnnotationCommand extends RemoteCommand<Integer> {

    private static final long serialVersionUID = 1L;

    final private int ticketId;
    final private String summary;
    final private String details;

    public AddTicketAnnotationCommand(
        @Param(name="ticket") Ticket ticket,
        @Param(name="summary") String summary,
        @Param(name="details", nullable=true) String details
    ) {
        this.ticketId = ticket.getTicketId();
        this.summary = summary;
        this.details = nullIfEmpty(details);
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
