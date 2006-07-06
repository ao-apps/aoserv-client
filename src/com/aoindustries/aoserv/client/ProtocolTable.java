package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
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

    public Protocol get(Object pkey) {
	return getUniqueRow(Protocol.COLUMN_PROTOCOL, pkey);
    }

    int getTableID() {
	return SchemaTable.PROTOCOLS;
    }
}