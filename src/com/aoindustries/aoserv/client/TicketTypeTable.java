package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * @see  TicketType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TicketTypeTable extends GlobalTableStringKey<TicketType> {

    TicketTypeTable(AOServConnector connector) {
	super(connector, TicketType.class);
    }

    public TicketType get(Object pkey) {
	return getUniqueRow(TicketType.COLUMN_TYPE, pkey);
    }

    public List<TicketType> getClientViewableTicketTypes() {
	List<TicketType> cached = getRows();
	int size = cached.size();
        List<TicketType> matches=new ArrayList<TicketType>(size);
	for (int i=0;i<size;i++) {
            TicketType tick = cached.get(i);
            if (tick.client_view) matches.add(tick);
	}
	return matches;
    }

    int getTableID() {
	return SchemaTable.TICKET_TYPES;
    }
}