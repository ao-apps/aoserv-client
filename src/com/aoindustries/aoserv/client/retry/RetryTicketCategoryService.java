package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.TicketCategory;
import com.aoindustries.aoserv.client.TicketCategoryService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryTicketCategoryService extends RetryServiceIntegerKey<TicketCategory> implements TicketCategoryService<RetryConnector,RetryConnectorFactory> {

    RetryTicketCategoryService(RetryConnector connector) {
        super(connector, TicketCategory.class);
    }
}
