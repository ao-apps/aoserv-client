/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.mysql.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see MysqlReplication
 *
 * @author  AO Industries, Inc.
 */
public final class MysqlReplicationTable extends CachedTableIntegerKey<MysqlReplication> {

	MysqlReplicationTable(AOServConnector connector) {
		super(connector, MysqlReplication.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MysqlReplication.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_AO_SERVER_name+'.'+com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(MysqlReplication.COLUMN_MYSQL_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(MysqlReplication.COLUMN_AO_SERVER_name, ASCENDING),
		new OrderBy(MysqlReplication.COLUMN_REPLICATION_name, ASCENDING)
	};

	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MysqlReplication get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MysqlReplication.COLUMN_PKEY, pkey);
	}

	public List<MysqlReplication> getFailoverMySQLReplications(Package pk) throws IOException, SQLException {
		List<MysqlReplication> matches = new ArrayList<>();
		for(Bind nb : pk.getNetBinds()) {
			Server ms = nb.getMySQLServer();
			if(ms != null) {
				matches.addAll(ms.getFailoverMySQLReplications());
			}
		}
		return matches;
	}

	public List<MysqlReplication> getFailoverMySQLReplications(Server mysqlServer) throws IOException, SQLException {
		return getIndexedRows(MysqlReplication.COLUMN_MYSQL_SERVER, mysqlServer.getPkey());
	}

	public List<MysqlReplication> getFailoverMySQLReplications(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
		return getIndexedRows(MysqlReplication.COLUMN_AO_SERVER, aoServer.getPkey());
	}

	List<MysqlReplication> getFailoverMySQLReplications(FileReplication replication) throws IOException, SQLException {
		return getIndexedRows(MysqlReplication.COLUMN_REPLICATION, replication.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FAILOVER_MYSQL_REPLICATIONS;
	}
}
