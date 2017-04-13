/*
 * aoserv-client - Java client for the AOServ Platform.
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

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Username
 *
 * @author  AO Industries, Inc.
 */
final public class UsernameTable extends CachedTableUserIdKey<Username> {

	UsernameTable(AOServConnector connector) {
		super(connector, Username.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Username.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addUsername(Package packageObject, UserId username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.USERNAMES,
			packageObject.name,
			username
		);
	}

	@Override
	public Username get(UserId username) throws IOException, SQLException {
		return getUniqueRow(Username.COLUMN_USERNAME, username);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.USERNAMES;
	}

	List<Username> getUsernames(Package pack) throws IOException, SQLException {
		return getIndexedRows(Username.COLUMN_PACKAGE, pack.name);
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_USERNAME, args, 2, err)) {
				connector.getSimpleAOClient().addUsername(
					AOSH.parseAccountingCode(args[1], "package"),
					AOSH.parseUserId(args[2], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ARE_USERNAME_PASSWORDS_SET)) {
			if(AOSH.checkParamCount(AOSHCommand.ARE_USERNAME_PASSWORDS_SET, args, 1, err)) {
				int result=connector.getSimpleAOClient().areUsernamePasswordsSet(
					AOSH.parseUserId(args[1], "username")
				);
				if(result==PasswordProtected.NONE) out.println("none");
				else if(result==PasswordProtected.SOME) out.println("some");
				else if(result==PasswordProtected.ALL) out.println("all");
				else throw new RuntimeException("Unexpected value for result: "+result);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_USERNAME, args, 1, err)) {
				ValidationResult validationResult = UserId.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_USERNAME+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_USERNAME_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_USERNAME_PASSWORD, args, 2, err)) {
				List<PasswordChecker.Result> results = connector.getSimpleAOClient().checkUsernamePassword(
					AOSH.parseUserId(args[1], "username"),
					args[2]
				);
				if(PasswordChecker.hasResults(results)) {
					PasswordChecker.printResults(results, out);
					out.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_USERNAME, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableUsername(
						AOSH.parseUserId(args[1], "username"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_USERNAME, args, 1, err)) {
				connector.getSimpleAOClient().enableUsername(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_USERNAME_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_USERNAME_AVAILABLE, args, 1, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isUsernameAvailable(
							AOSH.parseUserId(args[1], "username")
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_USERNAME_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_USERNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_USERNAME, args, 1, err)) {
				connector.getSimpleAOClient().removeUsername(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_USERNAME_PASSWORD)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_USERNAME_PASSWORD, args, 2, err)) {
				connector.getSimpleAOClient().setUsernamePassword(
					AOSH.parseUserId(args[1], "username"),
					args[2]
				);
			}
			return true;
		}
		return false;
	}

	public boolean isUsernameAvailable(UserId username) throws SQLException, IOException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_USERNAME_AVAILABLE, username);
	}
}
