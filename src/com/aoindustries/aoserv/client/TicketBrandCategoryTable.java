package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see TicketBrandCategory
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketBrandCategoryTable extends CachedTableIntegerKey<TicketBrandCategory> {

    TicketBrandCategoryTable(AOServConnector connector) {
        super(connector, TicketBrandCategory.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketBrandCategory.COLUMN_BRAND_name, ASCENDING),
        new OrderBy(TicketBrandCategory.COLUMN_CATEGORY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public TicketBrandCategory get(int pkey) throws IOException, SQLException {
        return getUniqueRow(TicketBrandCategory.COLUMN_PKEY, pkey);
    }

    List<TicketBrandCategory> getTicketBrandCategories(Brand brand) throws IOException, SQLException {
        return getIndexedRows(TicketBrandCategory.COLUMN_BRAND, brand.pkey);
    }

    List<TicketBrandCategory> getTicketBrandCategories(TicketCategory category) throws IOException, SQLException {
        return getIndexedRows(TicketBrandCategory.COLUMN_CATEGORY, category.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_BRAND_CATEGORIES;
    }
}
