/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  LinuxAccountType
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountTypeTable extends GlobalTableStringKey<LinuxAccountType> {

	LinuxAccountTypeTable(AOServConnector connector) {
		super(connector, LinuxAccountType.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(LinuxAccountType.COLUMN_DESCRIPTION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public LinuxAccountType get(String name) throws IOException, SQLException {
		return getUniqueRow(LinuxAccountType.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACCOUNT_TYPES;
	}
}
