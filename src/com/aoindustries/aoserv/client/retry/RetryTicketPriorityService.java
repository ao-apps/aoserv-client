package com.aoindustries.aoserv.client.retry;

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
final class RetryTicketPriorityService extends RetryServiceStringKey<TicketPriority> implements TicketPriorityService<RetryConnector,RetryConnectorFactory> {

    RetryTicketPriorityService(RetryConnector connector) {
        super(connector, TicketPriority.class);
    }
}
