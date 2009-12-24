package com.aoindustries.aoserv.client.cache;

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
final class CachedTicketTypeService extends CachedServiceStringKey<TicketType> implements TicketTypeService<CachedConnector,CachedConnectorFactory> {

    CachedTicketTypeService(CachedConnector connector, TicketTypeService<?,?> wrapped) {
        super(connector, TicketType.class, wrapped);
    }
}
