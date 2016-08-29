/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  HttpdJKProtocol
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKProtocolTable extends GlobalTableStringKey<HttpdJKProtocol> {

	HttpdJKProtocolTable(AOServConnector connector) {
		super(connector, HttpdJKProtocol.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdJKProtocol.COLUMN_PROTOCOL_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public HttpdJKProtocol get(String protocol) throws IOException, SQLException {
		return getUniqueRow(HttpdJKProtocol.COLUMN_PROTOCOL, protocol);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_JK_PROTOCOLS;
	}
}
