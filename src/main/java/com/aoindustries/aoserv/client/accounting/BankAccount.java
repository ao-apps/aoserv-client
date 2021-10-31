/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.accounting;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class BankAccount extends CachedObjectStringKey<BankAccount> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	private String display, bank;

	private int depositDelay, withdrawalDelay;

	public Bank getBank(long maximumCacheAge) throws SQLException, IOException {
		Bank bankObject = table.getConnector().getAccounting().getBank().get(bank);
		if (bankObject == null) throw new SQLException("Bank not found: " + bank);
		return bankObject;
	}

	public List<BankTransaction> getBankTransactions() throws IOException, SQLException {
		return table.getConnector().getAccounting().getBankTransaction().getBankTransactions(this);
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return display;
		if(i==2) return bank;
		if(i==3) return depositDelay;
		if(i==4) return withdrawalDelay;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public int getDepositDelay() {
		return depositDelay;
	}

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BANK_ACCOUNTS;
	}

	public int getWithdrawalDelay() {
		return withdrawalDelay;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		display = result.getString(2);
		bank = result.getString(3);
		depositDelay = result.getInt(4);
		withdrawalDelay = result.getInt(5);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF();
		display=in.readUTF();
		bank=in.readUTF();
		depositDelay=in.readCompressedInt();
		withdrawalDelay=in.readCompressedInt();
	}

	@Override
	public String toStringImpl() {
		return display;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
		out.writeUTF(bank);
		out.writeCompressedInt(depositDelay);
		out.writeCompressedInt(withdrawalDelay);
	}
}
