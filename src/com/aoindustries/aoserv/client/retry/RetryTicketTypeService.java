package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TicketType;
import com.aoindustries.aoserv.client.TicketTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTicketTypeService extends RetryServiceStringKey<TicketType> implements TicketTypeService<RetryConnector,RetryConnectorFactory> {

    RetryTicketTypeService(RetryConnector connector) {
        super(connector, TicketType.class);
    }
}
