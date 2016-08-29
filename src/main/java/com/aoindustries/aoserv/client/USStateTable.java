/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  USState
 *
 * @author  AO Industries, Inc.
 */
final public class USStateTable extends GlobalTableStringKey<USState> {

	USStateTable(AOServConnector connector) {
		super(connector, USState.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(USState.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.US_STATES;
	}

	@Override
	public USState get(String code) throws IOException, SQLException {
		return getUniqueRow(USState.COLUMN_CODE, code);
	}
}
