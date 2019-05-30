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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
final public class UserTable extends CachedTableUserNameKey<User> {

	UserTable(AOServConnector connector) {
		super(connector, User.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(User.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void addUsername(Package packageObject, User.Name username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.USERNAMES,
			packageObject.getName(),
			username
		);
	}

	@Override
	public User get(User.Name username) throws IOException, SQLException {
		return getUniqueRow(User.COLUMN_USERNAME, username);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.USERNAMES;
	}

	public List<User> getUsernames(Package pack) throws IOException, SQLException {
		return getIndexedRows(User.COLUMN_PACKAGE, pack.getName());
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_USERNAME)) {
			if(AOSH.checkParamCount(Command.ADD_USERNAME, args, 2, err)) {
				connector.getSimpleAOClient().addUsername(
					AOSH.parseAccountingCode(args[1], "package"),
					AOSH.parseUserName(args[2], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ARE_USERNAME_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(Command.ARE_USERNAME_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().areUsernamePasswordsSet(
					AOSH.parseUserName(args[1], "username")
				);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_USERNAME)) {
			if(AOSH.checkParamCount(Command.CHECK_USERNAME, args, 1, err)) {
				ValidationResult validationResult = User.Name.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+Command.CHECK_USERNAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_USERNAME_PASSWORD)) {
			if(AOSH.checkParamCount(Command.CHECK_USERNAME_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = connector.getSimpleAOClient().checkUsernamePassword(
					AOSH.parseUserName(args[1], "username"),
					args[2]
				);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_USERNAME)) {
			if(AOSH.checkParamCount(Command.DISABLE_USERNAME, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableUsername(
						AOSH.parseUserName(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_USERNAME)) {
			if(AOSH.checkParamCount(Command.ENABLE_USERNAME, args, 1, err)) {
				connector.getSimpleAOClient().enableUsername(
					AOSH.parseUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.IS_USERNAME_AVAILABLE)) {
			if(AOSH.checkParamCount(Command.IS_USERNAME_AVAILABLE, args, 1, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isUsernameAvailable(
							AOSH.parseUserName(args[1], "username")
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+Command.IS_USERNAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_USERNAME)) {
			if(AOSH.checkParamCount(Command.REMOVE_USERNAME, args, 1, err)) {
				connector.getSimpleAOClient().removeUsername(
					AOSH.parseUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_USERNAME_PASSWORD)) {
			if(AOSH.checkParamCount(Command.SET_USERNAME_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setUsernamePassword(
					AOSH.parseUserName(args[1], "username"),
					args[2]
				);
			}
			return true;
		}
		return false;
	}

	public boolean isUsernameAvailable(User.Name username) throws SQLException, IOException {
		return connector.requestBooleanQuery(true, AoservProtocol.CommandID.IS_USERNAME_AVAILABLE, username);
	}
}
