package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * @see  CreditCardProcessor
 *
 * @author  AO Industries, Inc.
 */
final public class CreditCardProcessorTable extends CachedTableStringKey<CreditCardProcessor> {

    CreditCardProcessorTable(AOServConnector connector) {
	super(connector, CreditCardProcessor.class);
    }

    public CreditCardProcessor get(Object pkey) {
	return getUniqueRow(CreditCardProcessor.COLUMN_PKEY, pkey);
    }

    public CreditCardProcessor get(String pkey) {
	return getUniqueRow(CreditCardProcessor.COLUMN_PKEY, pkey);
    }

    List<CreditCardProcessor> getCreditCardProcessors(Business business) {
        return getIndexedRows(CreditCardProcessor.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CREDIT_CARD_PROCESSORS;
    }
}
