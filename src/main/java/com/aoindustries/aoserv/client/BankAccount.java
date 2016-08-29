/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankAccount extends CachedObjectStringKey<BankAccount> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	private String display, bank;

	private int depositDelay, withdrawalDelay;

	public Bank getBank(long maximumCacheAge) throws SQLException, IOException {
		Bank bankObject = table.connector.getBanks().get(bank);
		if (bankObject == null) throw new SQLException("Bank not found: " + bank);
		return bankObject;
	}

	public List<BankTransaction> getBankTransactions() throws IOException, SQLException {
		return table.connector.getBankTransactions().getBankTransactions(this);
	}

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return display;
		if(i==2) return bank;
		if(i==3) return depositDelay;
		if(i==4) return withdrawalDelay;
		throw new IllegalArgumentException("Invalid index: "+i);
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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BANK_ACCOUNTS;
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
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF();
		display=in.readUTF();
		bank=in.readUTF();
		depositDelay=in.readCompressedInt();
		withdrawalDelay=in.readCompressedInt();
	}

	@Override
	String toStringImpl() {
		return display;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(display);
		out.writeUTF(bank);
		out.writeCompressedInt(depositDelay);
		out.writeCompressedInt(withdrawalDelay);
	}
}
