/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  MasterHost
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHostTable extends CachedTableIntegerKey<MasterHost> {

	MasterHostTable(AOServConnector connector) {
		super(connector, MasterHost.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MasterHost.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(MasterHost.COLUMN_HOST_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MasterHost get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_HOSTS;
	}
}
