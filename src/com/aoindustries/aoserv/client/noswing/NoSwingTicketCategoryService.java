package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingTicketCategoryService extends NoSwingServiceIntegerKey<TicketCategory> implements TicketCategoryService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTicketCategoryService(NoSwingConnector connector, TicketCategoryService<?,?> wrapped) {
        super(connector, TicketCategory.class, wrapped);
    }
}
