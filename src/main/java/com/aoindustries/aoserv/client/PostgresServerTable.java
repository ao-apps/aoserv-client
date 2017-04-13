/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  PostgresServer
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerTable extends CachedTableIntegerKey<PostgresServer> {

	PostgresServerTable(AOServConnector connector) {
		super(connector, PostgresServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresServer.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PostgresServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPostgresServer(
		PostgresServerName name,
		AOServer aoServer,
		PostgresVersion version,
		int maxConnections,
		int sortMem,
		int sharedBuffers,
		boolean fsync
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.POSTGRES_SERVERS,
			name,
			aoServer.pkey,
			version.pkey,
			maxConnections,
			sortMem,
			sharedBuffers,
			fsync
		);
	}

	@Override
	public PostgresServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PostgresServer.COLUMN_PKEY, pkey);
	}

	PostgresServer getPostgresServer(NetBind nb) throws IOException, SQLException {
		return getUniqueRow(PostgresServer.COLUMN_NET_BIND, nb.pkey);
	}

	List<PostgresServer> getPostgresServers(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(PostgresServer.COLUMN_AO_SERVER, ao.pkey);
	}

	PostgresServer getPostgresServer(PostgresServerName name, AOServer ao) throws IOException, SQLException {
		// Use the index first
		List<PostgresServer> table=getPostgresServers(ao);
		int size=table.size();
		for(int c=0;c<size;c++) {
			PostgresServer ps=table.get(c);
			if(ps.name.equals(name)) return ps;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_SERVERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_SERVER_NAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_SERVER_NAME, args, 1, err)) {
				ValidationResult validationResult = PostgresServerName.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_POSTGRES_SERVER_NAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE, args, 2, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isPostgresServerNameAvailable(
							AOSH.parsePostgresServerName(args[1], "server_name"),
							args[2]
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_POSTGRES_SERVER_NAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_POSTGRESQL)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_POSTGRESQL, args, 2, err)) {
				connector.getSimpleAOClient().restartPostgreSQL(
					AOSH.parsePostgresServerName(args[1], "postgres_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_POSTGRESQL)) {
			if(AOSH.checkParamCount(AOSHCommand.START_POSTGRESQL, args, 2, err)) {
				connector.getSimpleAOClient().startPostgreSQL(
					AOSH.parsePostgresServerName(args[1], "postgres_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_POSTGRESQL)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_POSTGRESQL, args, 2, err)) {
				connector.getSimpleAOClient().stopPostgreSQL(
					AOSH.parsePostgresServerName(args[1], "postgres_server"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_SERVER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForPostgresServerRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	boolean isPostgresServerNameAvailable(PostgresServerName name, AOServer ao) throws IOException, SQLException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_POSTGRES_SERVER_NAME_AVAILABLE, name, ao.pkey);
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.POSTGRES_SERVERS,
			aoServer.pkey
		);
	}
}
