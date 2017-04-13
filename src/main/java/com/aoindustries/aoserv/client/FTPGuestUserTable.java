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
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  FTPGuestUser
 *
 * @author  AO Industries, Inc.
 */
final public class FTPGuestUserTable extends CachedTableUserIdKey<FTPGuestUser> {

	FTPGuestUserTable(AOServConnector connector) {
		super(connector, FTPGuestUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(FTPGuestUser.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	void addFTPGuestUser(UserId username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.FTP_GUEST_USERS,
			username
		);
	}

	List<FTPGuestUser> getFTPGuestUsers(AOServer aoServer) throws IOException, SQLException {
		List<FTPGuestUser> cached=getRows();
		int size=cached.size();
		List<FTPGuestUser> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			FTPGuestUser obj=cached.get(c);
			if(obj.getLinuxAccount().getLinuxServerAccount(aoServer)!=null) matches.add(obj);
		}
		return matches;
	}

	@Override
	public FTPGuestUser get(UserId username) throws IOException, SQLException {
		return getUniqueRow(FTPGuestUser.COLUMN_USERNAME, username);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FTP_GUEST_USERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_FTP_GUEST_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_FTP_GUEST_USER, args, 1, err)) {
				connector.getSimpleAOClient().addFTPGuestUser(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_FTP_GUEST_USER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_FTP_GUEST_USER, args, 1, err)) {
				connector.getSimpleAOClient().removeFTPGuestUser(
					AOSH.parseUserId(args[1], "username")
				);
			}
			return true;
		}
		return false;
	}
}