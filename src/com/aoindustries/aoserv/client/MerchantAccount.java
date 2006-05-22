package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MerchantAccount extends CachedObjectStringKey<MerchantAccount> {

    static final int COLUMN_NAME=0;

    String display, bankAccount, javaConnector, url, merchantID;
    private int depositDelay, withdrawalDelay;

    public BankAccount getBankAccount() {
        BankAccount bankAccountObject = table.connector.bankAccounts.get(bankAccount);
        if (bankAccountObject == null) throw new WrappedException(new SQLException("BankAccount not found: " + bankAccount));
        return bankAccountObject;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return display;
	if(i==2) return bankAccount;
	if(i==3) return javaConnector;
	if(i==4) return url;
	if(i==5) return merchantID;
	if(i==6) return Integer.valueOf(depositDelay);
	if(i==7) return Integer.valueOf(withdrawalDelay);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public int getDepositDelay() {
	return depositDelay;
    }

    public String getDisplay() {
	return display;
    }

    public String getJavaConnector() {
	return javaConnector;
    }

    public String getMerchantID() {
	return merchantID;
    }

    public String getName() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.MERCHANT_ACCOUNTS;
    }

    public String getURL() {
	return url;
    }

    public int getWithdrawalDelay() {
	return withdrawalDelay;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	display = result.getString(2);
	bankAccount = result.getString(3);
	javaConnector = result.getString(4);
	url = result.getString(5);
	merchantID = result.getString(6);
	depositDelay = result.getInt(7);
	withdrawalDelay = result.getInt(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	display=in.readUTF();
	bankAccount=in.readUTF();
	javaConnector=readNullUTF(in);
	url=readNullUTF(in);
	merchantID=in.readUTF();
	depositDelay=in.readCompressedInt();
	withdrawalDelay=in.readCompressedInt();
    }

    String toStringImpl() {
	return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(display);
	out.writeUTF(bankAccount);
	writeNullUTF(out, javaConnector);
	writeNullUTF(out, url);
	out.writeUTF(merchantID);
	out.writeCompressedInt(depositDelay);
	out.writeCompressedInt(withdrawalDelay);
    }
}