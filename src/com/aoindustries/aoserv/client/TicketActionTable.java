package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketActionTable extends CachedTableIntegerKey<TicketAction> {

    TicketActionTable(AOServConnector connector) {
        super(connector, TicketAction.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketAction.COLUMN_TICKET_name, ASCENDING),
        new OrderBy(TicketAction.COLUMN_TIME_name, ASCENDING),
        new OrderBy(TicketAction.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public TicketAction get(int pkey) throws IOException, SQLException {
        return getUniqueRow(TicketAction.COLUMN_PKEY, pkey);
    }

    List<TicketAction> getActions(Ticket ticket) throws IOException, SQLException {
        return getIndexedRows(TicketAction.COLUMN_TICKET, ticket.pkey);
    }

    List<TicketAction> getActions(BusinessAdministrator ba) throws IOException, SQLException {
        return getIndexedRows(TicketAction.COLUMN_ADMINISTRATOR, ba.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ACTIONS;
    }
}
