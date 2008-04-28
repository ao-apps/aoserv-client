package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TicketPriority
 *
 * @version  1.0a
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

    public TicketPriority get(Object pkey) {
	return getUniqueRow(TicketPriority.COLUMN_PRIORITY, pkey);
    }
}