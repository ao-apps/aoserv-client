package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TicketStatus;
import com.aoindustries.aoserv.client.TicketStatusService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingTicketStatusService extends NoSwingServiceStringKey<TicketStatus> implements TicketStatusService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTicketStatusService(NoSwingConnector connector, TicketStatusService<?,?> wrapped) {
        super(connector, TicketStatus.class, wrapped);
    }
}
