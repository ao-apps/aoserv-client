package com.aoindustries.aoserv.client.cache;

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
final class CachedTicketPriorityService extends CachedServiceStringKey<TicketPriority> implements TicketPriorityService<CachedConnector,CachedConnectorFactory> {

    CachedTicketPriorityService(CachedConnector connector, TicketPriorityService<?,?> wrapped) {
        super(connector, TicketPriority.class, wrapped);
    }
}
