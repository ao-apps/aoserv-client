/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.postgresql;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.SimpleAOClient;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
public final class UserTable extends CachedTableUserNameKey<User> {

	UserTable(AOServConnector connector) {
		super(connector, User.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(User.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void addPostgresUser(User.Name username) throws IOException, SQLException {
		if(User.isSpecial(username)) throw new SQLException("Refusing to add special PostgreSQL user: " + username);
		connector.requestUpdateIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.POSTGRES_USERS,
			username
		);
	}

	@Override
	public User get(User.Name username) throws IOException, SQLException {
		return getUniqueRow(User.COLUMN_USERNAME, username);
	}

	public List<User> getPostgresUsers(Package pack) throws SQLException, IOException {
		Account.Name name=pack.getName();

		List<User> cached=getRows();
		int size=cached.size();
		List<User> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			User psu=cached.get(c);
			if(psu.getUsername().getPackage_name().equals(name)) matches.add(psu);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_USERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, IllegalArgumentException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_POSTGRES_USER)) {
			if(AOSH.checkParamCount(Command.ADD_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().addPostgresUser(
					AOSH.parsePostgresUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ARE_POSTGRES_USER_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(Command.ARE_POSTGRES_USER_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().arePostgresUserPasswordsSet(
					AOSH.parsePostgresUserName(args[1], "username")
				);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_POSTGRES_PASSWORD)) {
			if(AOSH.checkParamCount(Command.CHECK_POSTGRES_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = SimpleAOClient.checkPostgresPassword(
					AOSH.parsePostgresUserName(args[1], "username"),
					args[2]
				);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_POSTGRES_USERNAME)) {
			if(AOSH.checkParamCount(Command.CHECK_POSTGRES_USERNAME, args, 1, err)) {
				ValidationResult validationResult = User.Name.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+Command.CHECK_POSTGRES_USERNAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(Command.DISABLE_POSTGRES_USER, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disablePostgresUser(
						AOSH.parsePostgresUserName(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(Command.ENABLE_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().enablePostgresUser(
					AOSH.parsePostgresUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_POSTGRES_USER)) {
			if(AOSH.checkParamCount(Command.REMOVE_POSTGRES_USER, args, 1, err)) {
				connector.getSimpleAOClient().removePostgresUser(
					AOSH.parsePostgresUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_POSTGRES_USER_PASSWORD)) {
			if(AOSH.checkParamCount(Command.SET_POSTGRES_USER_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setPostgresUserPassword(
					AOSH.parsePostgresUserName(args[1], "username"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.WAIT_FOR_POSTGRES_USER_REBUILD)) {
			if(AOSH.checkParamCount(Command.WAIT_FOR_POSTGRES_USER_REBUILD, args, 1, err)) {
				connector.getSimpleAOClient().waitForPostgresUserRebuild(args[1]);
			}
			return true;
		}
		return false;
	}

	public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
		connector.requestUpdate(
			true,
			AoservProtocol.CommandID.WAIT_FOR_REBUILD,
			Table.TableID.POSTGRES_USERS,
			aoServer.getPkey()
		);
	}
}
