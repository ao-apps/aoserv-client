package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BankTransactionTable extends AOServTable<Integer,BankTransaction> {

    BankTransactionTable(AOServConnector connector) {
	super(connector, BankTransaction.class);
    }

    public BankTransaction get(Object transid) {
        return get(((Integer)transid).intValue());
    }

    public BankTransaction get(int transid) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.BANK_TRANSACTIONS, transid);
    }

    List<BankTransaction> getBankTransactions(BankAccount account) {
	return getObjects(AOServProtocol.GET_BANK_TRANSACTIONS_ACCOUNT, account.getName());
    }

    public List<BankTransaction> getRows() {
        List<BankTransaction> list=new ArrayList<BankTransaction>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.BANK_TRANSACTIONS);
        return list;
    }

    int getTableID() {
	return SchemaTable.BANK_TRANSACTIONS;
    }

    protected BankTransaction getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
