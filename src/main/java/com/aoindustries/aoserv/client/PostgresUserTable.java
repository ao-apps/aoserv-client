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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PostgresUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUserTable extends CachedTableStringKey<PostgresUser> {

	PostgresUserTable(AOServConnector connector) {
		super(connector, PostgresUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresUser.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addPostgresUser(String username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.POSTGRES_USERS,
			username
		);
	}

	@Override
	public PostgresUser get(String username) throws IOException, SQLException {
		return getUniqueRow(PostgresUser.COLUMN_USERNAME, username);
	}

	List<PostgresUser> getPostgresUsers(Package pack) throws SQLException, IOException {
		AccountingCode name=pack.name;

		List<PostgresUser> cached=getRows();
		int size=cached.size();
		List<PostgresUser> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			PostgresUser psu=cached.get(c);
			if(psu.getUsername().packageName.equals(name)) matches.add(psu);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_USERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, IllegalArgumentException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_POSTGRES_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().addPostgresUser(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.ARE_POSTGRES_USER_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().arePostgresUserPasswordsSet(args[1]);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = SimpleAOClient.checkPostgresPassword(args[1], args[2]);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_POSTGRES_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_POSTGRES_USERNAME, args, 1, err)) {
				SimpleAOClient.checkPostgresUsername(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_POSTGRES_USER, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disablePostgresUser(
						args[1],
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().enablePostgresUser(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().removePostgresUser(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_POSTGRES_USER_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_POSTGRES_USER_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setPostgresUserPassword(
					args[1],
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_POSTGRES_USER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForPostgresUserRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.POSTGRES_USERS,
			aoServer.pkey
		);
	}
}
