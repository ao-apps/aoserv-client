/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Logs which {@link Account accounts} were associated with a registrable domain at the time of whois lookup.
 *
 * @author  AO Industries, Inc.
 */
public final class WhoisHistoryAccount extends CachedObjectIntegerKey<WhoisHistoryAccount> {

	static final int
		COLUMN_id = 0,
		COLUMN_whoisHistory = 1,
		COLUMN_account = 2
	;
	static final String COLUMN_whoisHistory_name = "whoisHistory";
	static final String COLUMN_account_name = "account";

	private int whoisHistory;
	private Account.Name account;

	@Override
	protected Object getColumnImpl(int i) throws IOException, SQLException {
		switch(i) {
			case COLUMN_id: return pkey;
			case COLUMN_whoisHistory: return whoisHistory;
			case COLUMN_account: return account;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getId() {
		return pkey;
	}

	/**
	 * @see  #getWhoisHistory()
	 */
	public int getWhoisHistory_id() {
		return whoisHistory;
	}

	/**
	 * @see  WhoisHistory#getAccounts()
	 */
	public WhoisHistory getWhoisHistory() throws SQLException, IOException {
		WhoisHistory obj = table.getConnector().getBilling().getWhoisHistory().get(whoisHistory);
		if(obj == null) throw new SQLException("Unable to find WhoisHistory: " + whoisHistory);
		return obj;
	}

	/**
	 * @see  #getAccount()
	 */
	public Account.Name getAccount_id() {
		return account;
	}

	/**
	 * @see  Account#getWhoisHistoryAccounts()
	 */
	public Account getAccount() throws SQLException, IOException {
		Account obj = table.getConnector().getAccount().getAccount().get(account);
		if (obj == null) throw new SQLException("Unable to find Account: " + account);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.WhoisHistoryAccount;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			whoisHistory = result.getInt(pos++);
			account = Account.Name.valueOf(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			whoisHistory = in.readCompressedInt();
			account = Account.Name.valueOf(in.readUTF()).intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(whoisHistory);
		out.writeUTF(account.toString());
	}

	@Override
	public String toStringImpl() {
		return pkey+"|"+whoisHistory+"|"+account;
	}
}
