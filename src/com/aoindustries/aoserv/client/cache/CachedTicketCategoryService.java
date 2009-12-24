package com.aoindustries.aoserv.client.cache;

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
final class CachedTicketCategoryService extends CachedServiceIntegerKey<TicketCategory> implements TicketCategoryService<CachedConnector,CachedConnectorFactory> {

    CachedTicketCategoryService(CachedConnector connector, TicketCategoryService<?,?> wrapped) {
        super(connector, TicketCategory.class, wrapped);
    }
}
