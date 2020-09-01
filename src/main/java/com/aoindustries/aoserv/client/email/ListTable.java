/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @see  List
 *
 * @author  AO Industries, Inc.
 */
final public class ListTable extends CachedTableIntegerKey<List> {

	ListTable(AOServConnector connector) {
		super(connector, List.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(List.COLUMN_PATH_name, ASCENDING),
		new OrderBy(List.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+UserServer.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addEmailList(
		PosixPath path,
		UserServer lsa,
		GroupServer lsg
	) throws IllegalArgumentException, IOException, SQLException {
		Server lsaAO = lsa.getServer();
		Server lsgAO = lsg.getServer();
		if(!lsaAO.equals(lsgAO)) throw new IllegalArgumentException("Mismatched servers: " + lsaAO + " and " + lsgAO);
		if(
			!List.isValidRegularPath(
				path,
				lsaAO.getHost().getOperatingSystemVersion_id()
			)
		) throw new IllegalArgumentException("Invalid list path: " + path);

		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_LISTS,
			path,
			lsa.getPkey(),
			lsg.getPkey()
		);
	}

	@Override
	public List get(int pkey) throws IOException, SQLException {
		return getUniqueRow(List.COLUMN_PKEY, pkey);
	}

	public java.util.List<List> getEmailLists(Account business) throws IOException, SQLException {
		Account.Name accounting=business.getName();
		java.util.List<List> cached = getRows();
		int len = cached.size();
		java.util.List<List> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			List list = cached.get(c);
			if (
				list
				.getLinuxServerGroup()
				.getLinuxGroup()
				.getPackage()
				.getAccount_name()
				.equals(accounting)
			) matches.add(list);
		}
		return matches;
	}

	public java.util.List<List> getEmailLists(Package pack) throws IOException, SQLException {
		Account.Name packName=pack.getName();

		java.util.List<List> cached=getRows();
		int size=cached.size();
		java.util.List<List> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			List list=cached.get(c);
			if(list.getLinuxServerGroup().getLinuxGroup().getPackage_name().equals(packName)) matches.add(list);
		}
		return matches;
	}

	public java.util.List<List> getEmailLists(UserServer lsa) throws IOException, SQLException {
		return getIndexedRows(List.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
	}

	public List getEmailList(Server ao, PosixPath path) throws IOException, SQLException {
		int aoPKey = ao.getServer_pkey();
		java.util.List<List> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			List list=cached.get(c);
			if(list.getLinuxServerGroup().getServer_host_id() == aoPKey && list.getPath().equals(path)) return list;
		}
		return null;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_LISTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.ADD_EMAIL_LIST, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().addEmailList(
						args[1],
						AOSH.parseUnixPath(args[2], "path"),
						AOSH.parseLinuxUserName(args[3], "username"),
						AOSH.parseGroupName(args[4], "group")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_EMAIL_LIST_PATH)) {
			if(AOSH.checkParamCount(Command.CHECK_EMAIL_LIST_PATH, args, 2, err)) {
				try {
					connector.getSimpleAOClient().checkEmailListPath(
						args[1],
						AOSH.parseUnixPath(args[2], "path")
					);
					out.print(args[2]);
					out.print(": ");
					out.println("true");
				} catch(IllegalArgumentException ia) {
					out.print(args[2]);
					out.print(": ");
					out.println(ia.getMessage());
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.DISABLE_EMAIL_LIST, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().disableEmailList(
						AOSH.parseUnixPath(args[1], "path"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.ENABLE_EMAIL_LIST, args, 2, err)) {
				connector.getSimpleAOClient().enableEmailList(
					AOSH.parseUnixPath(args[1], "path"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.GET_EMAIL_LIST, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().getEmailListAddressList(
						AOSH.parseUnixPath(args[1], "path"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.REMOVE_EMAIL_LIST, args, 2, err)) {
				connector.getSimpleAOClient().removeEmailList(
					AOSH.parseUnixPath(args[1], "path"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_EMAIL_LIST)) {
			if(AOSH.checkParamCount(Command.SET_EMAIL_LIST, args, 3, err)) {
				connector.getSimpleAOClient().setEmailListAddressList(
					AOSH.parseUnixPath(args[1], "path"),
					args[2],
					args[3]
				);
			}
			return true;
		}
		return false;
	}
}
