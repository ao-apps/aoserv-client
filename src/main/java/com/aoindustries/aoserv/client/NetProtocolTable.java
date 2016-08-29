/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  NetProtocol
 *
 * @author  AO Industries, Inc.
 */
final public class NetProtocolTable extends GlobalTableStringKey<NetProtocol> {

	NetProtocolTable(AOServConnector connector) {
		super(connector, NetProtocol.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetProtocol.COLUMN_PROTOCOL_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NetProtocol get(String protocol) throws IOException, SQLException {
		return getUniqueRow(NetProtocol.COLUMN_PROTOCOL, protocol);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_PROTOCOLS;
	}
}
