/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Business;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.linux.LinuxServerAccount;
import com.aoindustries.aoserv.client.linux.LinuxServerGroup;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  EmailList
 *
 * @author  AO Industries, Inc.
 */
final public class EmailListTable extends CachedTableIntegerKey<EmailList> {

	public EmailListTable(AOServConnector connector) {
		super(connector, EmailList.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailList.COLUMN_PATH_name, ASCENDING),
		new OrderBy(EmailList.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+LinuxServerAccount.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addEmailList(
		UnixPath path,
		LinuxServerAccount lsa,
		LinuxServerGroup lsg
	) throws IllegalArgumentException, IOException, SQLException {
		AOServer lsaAO = lsa.getAOServer();
		AOServer lsgAO = lsg.getAOServer();
		if(!lsaAO.equals(lsgAO)) throw new IllegalArgumentException("Mismatched servers: " + lsaAO + " and " + lsgAO);
		if(
			!EmailList.isValidRegularPath(
				path,
				lsaAO.getServer().getOperatingSystemVersion_id()
			)
		) throw new IllegalArgumentException("Invalid list path: " + path);

		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.EMAIL_LISTS,
			path,
			lsa.getPkey(),
			lsg.getPkey()
		);
	}

	@Override
	public EmailList get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailList.COLUMN_PKEY, pkey);
	}

	public List<EmailList> getEmailLists(Business business) throws IOException, SQLException {
		AccountingCode accounting=business.getAccounting();
		List<EmailList> cached = getRows();
		int len = cached.size();
		List<EmailList> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			EmailList list = cached.get(c);
			if (
				list
				.getLinuxServerGroup()
				.getLinuxGroup()
				.getPackage()
				.getBusiness_accounting()
				.equals(accounting)
			) matches.add(list);
		}
		return matches;
	}

	public List<EmailList> getEmailLists(Package pack) throws IOException, SQLException {
		AccountingCode packName=pack.getName();

		List<EmailList> cached=getRows();
		int size=cached.size();
		List<EmailList> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			EmailList list=cached.get(c);
			if(list.getLinuxServerGroup().getLinuxGroup().getPackage_name().equals(packName)) matches.add(list);
		}
		return matches;
	}

	public List<EmailList> getEmailLists(LinuxServerAccount lsa) throws IOException, SQLException {
		return getIndexedRows(EmailList.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
	}

	public EmailList getEmailList(AOServer ao, UnixPath path) throws IOException, SQLException {
		int aoPKey=ao.getPkey();
		List<EmailList> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			EmailList list=cached.get(c);
			if(list.getLinuxServerGroup().getAoServer_server_id()==aoPKey && list.path.equals(path)) return list;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_LISTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_LIST, args, 4, err)) {
				out.println(
					connector.getSimpleAOClient().addEmailList(
						args[1],
						AOSH.parseUnixPath(args[2], "path"),
						AOSH.parseUserId(args[3], "username"),
						AOSH.parseGroupId(args[4], "group")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_LIST_PATH)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_EMAIL_LIST_PATH, args, 2, err)) {
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
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_LIST, args, 3, err)) {
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
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_LIST, args, 2, err)) {
				connector.getSimpleAOClient().enableEmailList(
					AOSH.parseUnixPath(args[1], "path"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GET_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_EMAIL_LIST, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().getEmailListAddressList(
						AOSH.parseUnixPath(args[1], "path"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_LIST, args, 2, err)) {
				connector.getSimpleAOClient().removeEmailList(
					AOSH.parseUnixPath(args[1], "path"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_EMAIL_LIST)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_EMAIL_LIST, args, 3, err)) {
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
