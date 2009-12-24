package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingTicketTypeService extends NoSwingServiceStringKey<TicketType> implements TicketTypeService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingTicketTypeService(NoSwingConnector connector, TicketTypeService<?,?> wrapped) {
        super(connector, TicketType.class, wrapped);
    }
}
