/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  WhoisHistoryAccount
 *
 * @author  AO Industries, Inc.
 */
final public class WhoisHistoryAccountTable extends CachedTableIntegerKey<WhoisHistoryAccount> {

	WhoisHistoryAccountTable(AOServConnector connector) {
		super(connector, WhoisHistoryAccount.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(WhoisHistoryAccount.COLUMN_whoisHistory_name + "." + WhoisHistory.COLUMN_registrableDomain_name, ASCENDING),
		new OrderBy(WhoisHistoryAccount.COLUMN_whoisHistory_name + "." + WhoisHistory.COLUMN_time_name, ASCENDING),
		new OrderBy(WhoisHistoryAccount.COLUMN_account_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public WhoisHistoryAccount get(int id) throws IOException, SQLException {
		return getUniqueRow(WhoisHistoryAccount.COLUMN_id, id);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.WhoisHistoryAccount;
	}

	/**
	 * @see  WhoisHistory#getAccounts()
	 */
	List<WhoisHistoryAccount> getWhoisHistoryAccounts(WhoisHistory whoisHistory) throws IOException, SQLException {
		return getIndexedRows(WhoisHistoryAccount.COLUMN_whoisHistory, whoisHistory.getId());
	}

	/**
	 * @see  Account#getWhoisHistoryAccounts()
	 */
	public List<WhoisHistoryAccount> getWhoisHistoryAccounts(Account account) throws IOException, SQLException {
		return getIndexedRows(WhoisHistoryAccount.COLUMN_account, account.getName());
	}
}
