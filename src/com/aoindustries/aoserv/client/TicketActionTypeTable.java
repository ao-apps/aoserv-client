package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The table containing all of the possible types of actions that may
 * be performed on a ticket.
 *
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketActionTypeTable extends GlobalTableStringKey<TicketActionType> {

    TicketActionTypeTable(AOServConnector connector) {
        super(connector, TicketActionType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketActionType.COLUMN_TYPE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public TicketActionType get(Object type) {
        try {
            return getUniqueRow(TicketActionType.COLUMN_TYPE, type);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ACTION_TYPES;
    }
}