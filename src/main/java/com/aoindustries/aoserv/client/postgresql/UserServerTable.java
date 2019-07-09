/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
final public class UserServerTable extends CachedTableIntegerKey<UserServer> {

	UserServerTable(AOServConnector connector) {
		super(connector, UserServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(UserServer.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(UserServer.COLUMN_POSTGRES_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(UserServer.COLUMN_POSTGRES_SERVER_name+'.'+Server.COLUMN_AO_SERVER_name+'.'+com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addPostgresServerUser(User.Name username, Server postgresServer) throws IOException, SQLException {
		if(User.isSpecial(username)) throw new SQLException("Refusing to add special user: " + username + " on " + postgresServer);
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.POSTGRES_SERVER_USERS,
			username,
			postgresServer.getBind_id()
		);
	}

	@Override
	public UserServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(UserServer.COLUMN_PKEY, pkey);
	}

	UserServer getPostgresServerUser(User.Name username, Server postgresServer) throws IOException, SQLException {
		return getPostgresServerUser(username, postgresServer.getBind_id());
	}

	UserServer getPostgresServerUser(User.Name username, int postgresServer) throws IOException, SQLException {
		List<UserServer> table=getRows();
		int size=table.size();
		for(int c=0;c<size;c++) {
			UserServer psu=table.get(c);
			if(
				psu.getPostresUser_username().equals(username)
				&& psu.getPostgresServer_bind_id() == postgresServer
			) return psu;
		}
		return null;
	}

	List<UserServer> getPostgresServerUsers(User pu) throws IOException, SQLException {
		return getIndexedRows(UserServer.COLUMN_USERNAME, pu.getUsername_username_id());
	}

	List<UserServer> getPostgresServerUsers(Server postgresServer) throws IOException, SQLException {
		return getIndexedRows(UserServer.COLUMN_POSTGRES_SERVER, postgresServer.getBind_id());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_SERVER_USERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(Command.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addPostgresServerUser(
						AOSH.parsePostgresUserName(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(Command.DISABLE_POSTGRES_SERVER_USER, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().disablePostgresServerUser(
						AOSH.parsePostgresUserName(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3],
						args[4]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(Command.ENABLE_POSTGRES_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().enablePostgresServerUser(
					AOSH.parsePostgresUserName(args[1], "username"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_POSTGRES_SERVER_USER_PASSWORD_SET)) {
			if(AOSH.checkParamCount(Command.IS_POSTGRES_SERVER_USER_PASSWORD_SET, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().isPostgresServerUserPasswordSet(
						AOSH.parsePostgresUserName(args[1], "username"),
						AOSH.parsePostgresServerName(args[2], "postgres_server"),
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_POSTGRES_SERVER_USER)) {
			if(AOSH.checkParamCount(Command.REMOVE_POSTGRES_SERVER_USER, args, 3, err)) {
				connector.getSimpleAOClient().removePostgresServerUser(
					AOSH.parsePostgresUserName(args[1], "username"),
					AOSH.parsePostgresServerName(args[2], "postgres_server"),
					args[3]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_POSTGRES_SERVER_USER_PASSWORD)) {
			if(AOSH.checkParamCount(Command.SET_POSTGRES_SERVER_USER_PASSWORD, args, 4, err)) {
				connector.getSimpleAOClient().setPostgresServerUserPassword(
					AOSH.parsePostgresUserName(args[1], "username"),
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
