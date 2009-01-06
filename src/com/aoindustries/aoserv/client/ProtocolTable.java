package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Protocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ProtocolTable extends GlobalTableStringKey<Protocol> {

    ProtocolTable(AOServConnector connector) {
	super(connector, Protocol.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Protocol.COLUMN_PORT_name, ASCENDING),
        new OrderBy(Protocol.COLUMN_NET_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Protocol get(Object pkey) {
	return getUniqueRow(Protocol.COLUMN_PROTOCOL, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PROTOCOLS;
    }
}