package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BankAccount extends CachedObjectStringKey<BankAccount> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    private String display, bank;

    private int depositDelay, withdrawalDelay;

    public Bank getBank(long maximumCacheAge) {
        Bank bankObject = table.connector.banks.get(bank);
        if (bankObject == null) throw new WrappedException(new SQLException("Bank not found: " + bank));
        return bankObject;
    }

    public List<BankTransaction> getBankTransactions() {
	return table.connector.bankTransactions.getBankTransactions(this);
    }

    public Object getColumn(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return display;
	if(i==2) return bank;
	if(i==3) return Integer.valueOf(depositDelay);
	if(i==4) return Integer.valueOf(withdrawalDelay);
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANK_ACCOUNTS;
    }

    public int getWithdrawalDelay() {
	return withdrawalDelay;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	display = result.getString(2);
	bank = result.getString(3);
	depositDelay = result.getInt(4);
	withdrawalDelay = result.getInt(5);
    }

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

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(display);
	out.writeUTF(bank);
	out.writeCompressedInt(depositDelay);
	out.writeCompressedInt(withdrawalDelay);
    }
}