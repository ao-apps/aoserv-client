package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TicketPriority
 *
 * @author  AO Industries, Inc.
 */
final public class TicketPriorityTable extends GlobalTableStringKey<TicketPriority> {

    TicketPriorityTable(AOServConnector connector) {
        super(connector, TicketPriority.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketPriority.COLUMN_PRIORITY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_PRIORITIES;
    }

    public TicketPriority get(String priority) throws IOException, SQLException {
        return getUniqueRow(TicketPriority.COLUMN_PRIORITY, priority);
    }
}