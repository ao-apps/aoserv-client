package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * @see  CreditCardTransaction
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardTransactionTable extends CachedTableIntegerKey<CreditCardTransaction> {

    CreditCardTransactionTable(AOServConnector connector) {
	super(connector, CreditCardTransaction.class);
    }

    public CreditCardTransaction get(Object pkey) {
	return getUniqueRow(CreditCardTransaction.COLUMN_PKEY, pkey);
    }

    public CreditCardTransaction get(int pkey) {
	return getUniqueRow(CreditCardTransaction.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_TRANSACTIONS;
    }
}
