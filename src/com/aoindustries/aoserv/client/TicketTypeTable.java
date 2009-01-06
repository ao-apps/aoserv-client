package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketType.COLUMN_TYPE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TICKET_TYPES;
    }
}