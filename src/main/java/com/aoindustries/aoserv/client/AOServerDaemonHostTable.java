/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  AOServerDaemonHost
 *
 * @author  AO Industries, Inc.
 */
public final class AOServerDaemonHostTable extends CachedTableIntegerKey<AOServerDaemonHost> {

	AOServerDaemonHostTable(AOServConnector connector) {
		super(connector, AOServerDaemonHost.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AOServerDaemonHost.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(AOServerDaemonHost.COLUMN_HOST_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public AOServerDaemonHost get(int pkey) throws IOException, SQLException {
		return getUniqueRow(AOServerDaemonHost.COLUMN_PKEY, pkey);
	}

	List<AOServerDaemonHost> getAOServerDaemonHosts(AOServer aoServer) throws IOException, SQLException {
		return getIndexedRows(AOServerDaemonHost.COLUMN_AO_SERVER, aoServer.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AO_SERVER_DAEMON_HOSTS;
	}
}
