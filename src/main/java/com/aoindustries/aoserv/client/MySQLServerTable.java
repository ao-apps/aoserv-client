/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2006-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  MySQLServer
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServerTable extends CachedTableIntegerKey<MySQLServer> {

	MySQLServerTable(AOServConnector connector) {
		super(connector, MySQLServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MySQLServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(MySQLServer.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addMySQLServer(
		MySQLServerName name,
		AOServer aoServer,
		TechnologyVersion version,
		int maxConnections
	) throws SQLException, IOException {
		if(!version.name.equals(TechnologyName.MYSQL)) throw new SQLException("TechnologyVersion must have name of "+TechnologyName.MYSQL+": "+version.name);
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.MYSQL_SERVERS,
			name,
			aoServer.pkey,
			version.pkey,
			maxConnections
		);
	}

	@Override
	public MySQLServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MySQLServer.COLUMN_PKEY, pkey);
	}

	MySQLServer getMySQLServer(NetBind nb) throws IOException, SQLException {
		return getUniqueRow(MySQLServer.COLUMN_NET_BIND, nb.pkey);
	}

	List<MySQLServer> getMySQLServers(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(MySQLServer.COLUMN_AO_SERVER, ao.pkey);
	}

	MySQLServer getMySQLServer(MySQLServerName name, AOServer ao) throws IOException, SQLException {
		// Use the index first
		List<MySQLServer> table=getMySQLServers(ao);
		int size=table.size();
		for(int c=0;c<size;c++) {
			MySQLServer ms=table.get(c);
			if(ms.name.equals(name)) return ms;
		}
		return null;
	}

	List<MySQLServer> getMySQLServers(Package pk) throws IOException, SQLException {
		return getIndexedRows(MySQLServer.COLUMN_PACKAGE, pk.name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_SERVERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_SERVER_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_SERVER_NAME, args, 1, err)) {
				ValidationResult validationResult = MySQLServerName.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_MYSQL_SERVER_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE, args, 2, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isMySQLServerNameAvailable(
							AOSH.parseMySQLServerName(args[1], "server_name"),
							args[2]
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_MYSQL_SERVER_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_MYSQL)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_MYSQL, args, 2, err)) {
				connector.getSimpleAOClient().restartMySQL(
					AOSH.parseMySQLServerName(args[1], "mysql_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_MYSQL)) {
			if(AOSH.checkParamCount(AOSHCommand.START_MYSQL, args, 2, err)) {
				connector.getSimpleAOClient().startMySQL(
					AOSH.parseMySQLServerName(args[1], "mysql_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_MYSQL)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_MYSQL, args, 2, err)) {
				connector.getSimpleAOClient().stopMySQL(
					AOSH.parseMySQLServerName(args[1], "mysql_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_SERVER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForMySQLServerRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isMySQLServerNameAvailable(MySQLServerName name, AOServer ao) throws IOException, SQLException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_MYSQL_SERVER_NAME_AVAILABLE, name, ao.pkey);
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.MYSQL_SERVERS,
			aoServer.pkey
		);
	}
}
