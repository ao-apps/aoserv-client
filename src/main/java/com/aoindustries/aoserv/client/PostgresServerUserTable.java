/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2012, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServerUserTable extends CachedTableIntegerKey<PostgresServerUser> {

	PostgresServerUserTable(AOServConnector connector) {
		super(connector, PostgresServerUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresServerUser.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(PostgresServerUser.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PostgresServerUser.COLUMN_POSTGRES_SERVER_name+'.'+PostgresServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPostgresServerUser(PostgresUserId username, PostgresServer postgresServer) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.POSTGRES_SERVER_USERS,
			username,
			postgresServer.pkey
		);
		return pkey;
	}

	@Override
	public PostgresServerUser get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PostgresServerUser.COLUMN_PKEY, pkey);
	}

	PostgresServerUser getPostgresServerUser(PostgresUserId username, PostgresServer postgresServer) throws IOException, SQLException {
		return getPostgresServerUser(username, postgresServer.pkey);
	}

	PostgresServerUser getPostgresServerUser(PostgresUserId username, int postgresServer) throws IOException, SQLException {
		List<PostgresServerUser> table=getRows();
		int size=table.size();
		for(int c=0;c<size;c++) {
			PostgresServerUser psu=table.get(c);
			if(
				psu.username.equals(username)
				&& psu.postgres_server==postgresServer
			) return psu;
		}
		return null;
	}

	List<PostgresServerUser> getPostgresServerUsers(PostgresUser pu) throws IOException, SQLException {
		return getIndexedRows(PostgresServerUser.COLUMN_USERNAME, pu.pkey);
	}

	List<PostgresServerUser> getPostgresServerUsers(PostgresServer postgresServer) throws IOException, SQLException {
		return getIndexedRows(PostgresServerUser.COLUMN_POSTGRES_SERVER, postgresServer.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_SERVER_USERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addPostgresServerUser(
						AOSH.parsePostgresUserId(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_SERVER_USER, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().disablePostgresServerUser(
						AOSH.parsePostgresUserId(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3],
						args[4]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().enablePostgresServerUser(
					AOSH.parsePostgresUserId(args[1], "username"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_POSTGRES_SERVER_USER_PASSWORD_SET, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().isPostgresServerUserPasswordSet(
						AOSH.parsePostgresUserId(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().removePostgresServerUser(
					AOSH.parsePostgresUserId(args[1], "username"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_SERVER_USER_PASSWORD, args, 4, err)) {
				connector.getSimpleAOClient().setPostgresServerUserPassword(
					AOSH.parsePostgresUserId(args[1], "username"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3],
					args[4]
				);
			}
			return true;
		}
		return false;
	}
}
