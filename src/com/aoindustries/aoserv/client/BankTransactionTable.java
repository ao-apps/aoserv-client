package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(BankTransaction.COLUMN_TIME_name+"::"+SchemaType.DATE_name, ASCENDING),
        new OrderBy(BankTransaction.COLUMN_TRANSID_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public BankTransaction get(Object transid) {
        try {
            return get(((Integer)transid).intValue());
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public BankTransaction get(int transid) throws IOException, SQLException {
        return getObject(AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.BANK_TRANSACTIONS, transid);
    }

    List<BankTransaction> getBankTransactions(BankAccount account) throws IOException, SQLException {
	return getObjects(AOServProtocol.CommandID.GET_BANK_TRANSACTIONS_ACCOUNT, account.getName());
    }

    public List<BankTransaction> getRows() throws IOException, SQLException {
        List<BankTransaction> list=new ArrayList<BankTransaction>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.BANK_TRANSACTIONS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BANK_TRANSACTIONS;
    }

    protected BankTransaction getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
