/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.account;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  AccountHostTable
 *
 * @author  AO Industries, Inc.
 */
public final class AccountHostTable extends CachedTableIntegerKey<AccountHost> {

	AccountHostTable(AOServConnector connector) {
		super(connector, AccountHost.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AccountHost.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(AccountHost.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(AccountHost.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addAccountHost(Account business, Host server) throws IOException, SQLException {
		return connector.requestIntQueryIL(true, AoservProtocol.CommandID.ADD, Table.TableID.BUSINESS_SERVERS, business.getName().toString(), server.getPkey());
	}

	@Override
	public AccountHost get(int pkey) throws IOException, SQLException {
		return getUniqueRow(AccountHost.COLUMN_PKEY, pkey);
	}

	List<AccountHost> getAccountHosts(Account bu) throws IOException, SQLException {
		return getIndexedRows(AccountHost.COLUMN_ACCOUNTING, bu.getName());
	}

	List<AccountHost> getAccountHosts(Host server) throws IOException, SQLException {
		return getIndexedRows(AccountHost.COLUMN_SERVER, server.getPkey());
	}

	public List<Account> getAccounts(Host server) throws IOException, SQLException {
		// Use the cache and convert
		List<AccountHost> cached = getAccountHosts(server);
		int size=cached.size();
		List<Account> businesses=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			businesses.add(cached.get(c).getAccount());
		}
		return businesses;
	}

	AccountHost getAccountHost(Account account, Host host) throws IOException, SQLException {
		int host_id = host.getPkey();

		// Use the index first
		List<AccountHost> cached = getAccountHosts(account);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			AccountHost bs=cached.get(c);
			if(bs.getHost_id() == host_id) return bs;
		}
		return null;
	}

	Host getDefaultHost(Account business) throws IOException, SQLException {
		// Use index first
		List<AccountHost> cached = getAccountHosts(business);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			AccountHost bs=cached.get(c);
			if(bs.isDefault()) return bs.getHost();
		}
		return null;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BUSINESS_SERVERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(Command.ADD_BUSINESS_SERVER, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().addAccountHost(
						AOSH.parseAccountingCode(args[1], "business"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(Command.REMOVE_BUSINESS_SERVER, args, 2, err)) {
				connector.getSimpleAOClient().removeAccountHost(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_DEFAULT_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(Command.SET_DEFAULT_BUSINESS_SERVER, args, 2, err)) {
				connector.getSimpleAOClient().setDefaultAccountHost(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2]
				);
			}
			return true;
		} else return false;
	}
}
