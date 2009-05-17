package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
final public class BrandTable extends CachedTableStringKey<Brand> {

    BrandTable(AOServConnector connector) {
        super(connector, Brand.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Brand.COLUMN_ACCOUNTING_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    /**
     * Gets a <code>Brand</code> from the database.
     */
    public Brand get(String accounting) throws IOException, SQLException {
        return getUniqueRow(Brand.COLUMN_ACCOUNTING, accounting);
    }

    /**
     * Gets a <code>Brand</code> given its business.
     */
    Brand getBrand(Business business) throws IOException, SQLException {
        return getUniqueRow(Brand.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.BRANDS;
    }
}
