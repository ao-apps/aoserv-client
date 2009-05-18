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
 * @see TicketCategory
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategoryTable extends CachedTableIntegerKey<TicketCategory> {

    TicketCategoryTable(AOServConnector connector) {
        super(connector, TicketCategory.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketCategory.COLUMN_PARENT_name, ASCENDING),
        new OrderBy(TicketCategory.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public TicketCategory get(int pkey) throws IOException, SQLException {
        return getUniqueRow(TicketCategory.COLUMN_PKEY, pkey);
    }

    List<TicketCategory> getChildrenCategories(TicketCategory parent) throws IOException, SQLException {
        return getIndexedRows(TicketCategory.COLUMN_PARENT, parent.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_CATEGORIES;
    }

    /**
     * Gets the category with the provided parent and name.
     * Parent may be <code>null</code> for top-level categories.
     */
    public TicketCategory getTicketCategory(TicketCategory parent, String name) throws IOException, SQLException {
        if(parent==null) {
            // Search all top-level
            for(TicketCategory tc : getRows()) {
                if(tc.parent==-1 && tc.name.equals(name)) return tc;
            }
        } else {
            // Use indexing from above
            for(TicketCategory child : getChildrenCategories(parent)) {
                if(child.name.equals(name)) return child;
            }
        }
        return null;
    }
}
