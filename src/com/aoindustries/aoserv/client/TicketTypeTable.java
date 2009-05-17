package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  TicketType
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

    public TicketType get(String type) throws IOException, SQLException {
        return getUniqueRow(TicketType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.TICKET_TYPES;
    }
}