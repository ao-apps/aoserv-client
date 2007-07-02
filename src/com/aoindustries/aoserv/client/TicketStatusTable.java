package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TicketStatus
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatusTable extends GlobalTableStringKey<TicketStatus> {

    TicketStatusTable(AOServConnector connector) {
	super(connector, TicketStatus.class);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKET_STATI;
    }

    public TicketStatus get(Object pkey) {
	return getUniqueRow(TicketStatus.COLUMN_STATUS, pkey);
    }
}