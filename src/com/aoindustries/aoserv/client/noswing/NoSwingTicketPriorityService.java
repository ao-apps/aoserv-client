package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.TicketPriorityService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingTicketPriorityService extends NoSwingServiceStringKey<TicketPriority> implements TicketPriorityService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTicketPriorityService(NoSwingConnector connector, TicketPriorityService<?,?> wrapped) {
        super(connector, TicketPriority.class, wrapped);
    }
}
