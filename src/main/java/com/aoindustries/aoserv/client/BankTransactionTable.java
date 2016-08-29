/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * For AO Industries use only.
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

	@Override
	public BankTransaction get(Object transid) throws IOException, SQLException {
		return get(((Integer)transid).intValue());
	}

	public BankTransaction get(int transid) throws IOException, SQLException {
		return getObject(true, AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.BANK_TRANSACTIONS, transid);
	}

	List<BankTransaction> getBankTransactions(BankAccount account) throws IOException, SQLException {
		return getObjects(true, AOServProtocol.CommandID.GET_BANK_TRANSACTIONS_ACCOUNT, account.getName());
	}

	@Override
	public List<BankTransaction> getRows() throws IOException, SQLException {
		List<BankTransaction> list=new ArrayList<>();
		getObjects(true, list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.BANK_TRANSACTIONS);
		return list;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BANK_TRANSACTIONS;
	}

	@Override
	protected BankTransaction getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
		return get(value);
	}
}
