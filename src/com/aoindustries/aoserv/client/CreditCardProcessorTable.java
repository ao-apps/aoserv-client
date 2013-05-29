package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(CreditCardProcessor.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(CreditCardProcessor.COLUMN_PROVIDER_ID_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public CreditCardProcessor get(String providerId) throws IOException, SQLException {
        return getUniqueRow(CreditCardProcessor.COLUMN_PROVIDER_ID, providerId);
    }

    List<CreditCardProcessor> getCreditCardProcessors(Business business) throws IOException, SQLException {
        return getIndexedRows(CreditCardProcessor.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.CREDIT_CARD_PROCESSORS;
    }
}
