/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see FailoverMySQLReplication
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplicationTable extends CachedTableIntegerKey<FailoverMySQLReplication> {

	FailoverMySQLReplicationTable(AOServConnector connector) {
		super(connector, FailoverMySQLReplication.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FailoverMySQLReplication.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(FailoverMySQLReplication.COLUMN_MYSQL_SERVER_name+'.'+MySQLServer.COLUMN_NAME_name, ASCENDING),
		new OrderBy(FailoverMySQLReplication.COLUMN_AO_SERVER_name, ASCENDING),
		new OrderBy(FailoverMySQLReplication.COLUMN_REPLICATION_name, ASCENDING)
	};

	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public FailoverMySQLReplication get(int pkey) throws IOException, SQLException {
		return getUniqueRow(FailoverMySQLReplication.COLUMN_PKEY, pkey);
	}

	List<FailoverMySQLReplication> getFailoverMySQLReplications(Package pk) throws IOException, SQLException {
		List<FailoverMySQLReplication> matches = new ArrayList<>();
		List<MySQLServer> mss = pk.getMySQLServers();
		for(MySQLServer ms : mss) {
			matches.addAll(ms.getFailoverMySQLReplications());
		}
		return matches;
	}

	List<FailoverMySQLReplication> getFailoverMySQLReplications(MySQLServer mysqlServer) throws IOException, SQLException {
		return getIndexedRows(FailoverMySQLReplication.COLUMN_MYSQL_SERVER, mysqlServer.pkey);
	}

	List<FailoverMySQLReplication> getFailoverMySQLReplications(AOServer aoServer) throws IOException, SQLException {
		return getIndexedRows(FailoverMySQLReplication.COLUMN_AO_SERVER, aoServer.pkey);
	}

	List<FailoverMySQLReplication> getFailoverMySQLReplications(FailoverFileReplication replication) throws IOException, SQLException {
		return getIndexedRows(FailoverMySQLReplication.COLUMN_REPLICATION, replication.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_MYSQL_REPLICATIONS;
	}
}
