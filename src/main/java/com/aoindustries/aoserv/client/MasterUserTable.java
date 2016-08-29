/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  MasterUser
 *
 * @author  AO Industries, Inc.
 */
final public class MasterUserTable extends CachedTableStringKey<MasterUser> {

	MasterUserTable(AOServConnector connector) {
		super(connector, MasterUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MasterUser.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MasterUser get(String username) throws IOException, SQLException {
		return getUniqueRow(MasterUser.COLUMN_USERNAME, username);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_USERS;
	}
}
