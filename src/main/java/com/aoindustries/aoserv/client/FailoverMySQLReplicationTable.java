/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
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
		for(NetBind nb : pk.getNetBinds()) {
			MySQLServer ms = nb.getMySQLServer();
			if(ms != null) {
				matches.addAll(ms.getFailoverMySQLReplications());
			}
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
