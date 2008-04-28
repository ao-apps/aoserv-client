package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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