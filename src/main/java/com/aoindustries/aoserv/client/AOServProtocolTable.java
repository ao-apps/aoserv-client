/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  AOServProtocol
 *
 * @author  AO Industries, Inc.
 */
final public class AOServProtocolTable extends GlobalTableStringKey<AOServProtocol> {

	AOServProtocolTable(AOServConnector connector) {
		super(connector, AOServProtocol.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AOServProtocol.COLUMN_CREATED_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public AOServProtocol get(String version) throws IOException, SQLException {
		return getUniqueRow(AOServProtocol.COLUMN_VERSION, version);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AOSERV_PROTOCOLS;
	}
}
