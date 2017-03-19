/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2012-2009, 2016, 2017  AO Industries, Inc.
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
 * @see  MySQLUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLUserTable extends CachedTableStringKey<MySQLUser> {

	MySQLUserTable(AOServConnector connector) {
		super(connector, MySQLUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MySQLUser.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addMySQLUser(String username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.MYSQL_USERS,
			username
		);
	}

	@Override
	public MySQLUser get(String username) throws IOException, SQLException {
		return getUniqueRow(MySQLUser.COLUMN_USERNAME, username);
	}

	List<MySQLUser> getMySQLUsers(Package pack) throws IOException, SQLException {
		AccountingCode name=pack.name;
		List<MySQLUser> cached=getRows();
		int size=cached.size();
		List<MySQLUser> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			MySQLUser msu=cached.get(c);
			if(msu.getUsername().packageName.equals(name)) matches.add(msu);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MYSQL_USERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_MYSQL_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_MYSQL_USER, args, 1, err)) {
				connector.getSimpleAOClient().addMySQLUser(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ARE_MYSQL_USER_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.ARE_MYSQL_USER_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().areMySQLUserPasswordsSet(args[1]);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results=SimpleAOClient.checkMySQLPassword(args[1], args[2]);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_MYSQL_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_MYSQL_USERNAME, args, 1, err)) {
				SimpleAOClient.checkMySQLUsername(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_MYSQL_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_MYSQL_USER, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableMySQLUser(
						args[1],
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_MYSQL_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_MYSQL_USER, args, 1, err)) {
				connector.getSimpleAOClient().enableMySQLUser(args[1]);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_MYSQL_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_MYSQL_USER, args, 1, err)) {
				connector.getSimpleAOClient().removeMySQLUser(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_MYSQL_USER_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_MYSQL_USER_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setMySQLUserPassword(
					args[1],
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD)) {
			if(AOSH.checkParamCount(AOSHCommand.WAIT_FOR_MYSQL_USER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForMySQLUserRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	void waitForRebuild(AOServer aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AOServProtocol.CommandID.WAIT_FOR_REBUILD,
			SchemaTable.TableID.MYSQL_USERS,
			aoServer.pkey
		);
	}
}
