/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * All of the operating systems referenced from other tables.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemTable extends GlobalTableStringKey<OperatingSystem> {

	OperatingSystemTable(AOServConnector connector) {
		super(connector, OperatingSystem.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(OperatingSystem.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public OperatingSystem get(String name) throws IOException, SQLException {
		return getUniqueRow(OperatingSystem.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.OPERATING_SYSTEMS;
	}
}
