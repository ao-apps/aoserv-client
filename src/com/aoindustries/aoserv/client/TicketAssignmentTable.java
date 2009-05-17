package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see TicketAssignment
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAssignmentTable extends CachedTableIntegerKey<TicketAssignment> {

    TicketAssignmentTable(AOServConnector connector) {
        super(connector, TicketAssignment.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TicketAssignment.COLUMN_TICKET_name, ASCENDING),
        new OrderBy(TicketAssignment.COLUMN_RESELLER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public TicketAssignment get(Object pkey) {
        try {
            return getUniqueRow(TicketAssignment.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    List<TicketAssignment> getTicketAssignments(Ticket ticket) throws IOException, SQLException {
        return getIndexedRows(TicketAssignment.COLUMN_TICKET, ticket.pkey);
    }

    List<TicketAssignment> getTicketAssignments(Reseller reseller) throws IOException, SQLException {
        return getIndexedRows(TicketAssignment.COLUMN_RESELLER, reseller.pkey);
    }

    List<TicketAssignment> getTicketAssignments(BusinessAdministrator ba) throws IOException, SQLException {
        return getIndexedRows(TicketAssignment.COLUMN_ADMINISTRATOR, ba.pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ASSIGNMENTS;
    }
}
