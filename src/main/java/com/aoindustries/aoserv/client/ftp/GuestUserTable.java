/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.ftp;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.CachedTableUserNameKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  GuestUser
 *
 * @author  AO Industries, Inc.
 */
final public class GuestUserTable extends CachedTableUserNameKey<GuestUser> {

	GuestUserTable(AOServConnector connector) {
		super(connector, GuestUser.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(GuestUser.COLUMN_USERNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public void addFTPGuestUser(User.Name username) throws IOException, SQLException {
		connector.requestUpdateIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.FTP_GUEST_USERS,
			username
		);
	}

	public List<GuestUser> getFTPGuestUsers(Server aoServer) throws IOException, SQLException {
		List<GuestUser> cached=getRows();
		int size=cached.size();
		List<GuestUser> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			GuestUser obj=cached.get(c);
			if(obj.getLinuxAccount().getLinuxServerAccount(aoServer)!=null) matches.add(obj);
		}
		return matches;
	}

	@Override
	public GuestUser get(User.Name username) throws IOException, SQLException {
		return getUniqueRow(GuestUser.COLUMN_USERNAME, username);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FTP_GUEST_USERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_FTP_GUEST_USER)) {
			if(AOSH.checkParamCount(Command.ADD_FTP_GUEST_USER, args, 1, err)) {
				connector.getSimpleAOClient().addFTPGuestUser(
					AOSH.parseLinuxUserName(args[1], "username")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_FTP_GUEST_USER)) {
			if(AOSH.checkParamCount(Command.REMOVE_FTP_GUEST_USER, args, 1, err)) {
				connector.getSimpleAOClient().removeFTPGuestUser(
					AOSH.parseLinuxUserName(args[1], "username")
				);
			}
			return true;
		}
		return false;
	}
}