package com.aoindustries.aoserv.client.retry;

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
final class RetryTicketStatusService extends RetryServiceStringKey<TicketStatus> implements TicketStatusService<RetryConnector,RetryConnectorFactory> {

    RetryTicketStatusService(RetryConnector connector) {
        super(connector, TicketStatus.class);
    }
}
