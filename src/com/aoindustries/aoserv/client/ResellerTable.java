package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
final public class ResellerTable extends CachedTableStringKey<Reseller> {

    ResellerTable(AOServConnector connector) {
        super(connector, Reseller.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Reseller.COLUMN_ACCOUNTING_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    /**
     * Gets a <code>Reseller</code> from the database.
     */
    public Reseller get(String accounting) throws IOException, SQLException {
        return getUniqueRow(Reseller.COLUMN_ACCOUNTING, accounting);
    }

    /**
     * Gets a <code>Reseller</code> given its brand.
     */
    Reseller getReseller(Brand brand) throws IOException, SQLException {
        return getUniqueRow(Reseller.COLUMN_ACCOUNTING, brand.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RESELLERS;
    }
}
