/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	@Override
	public TicketActionType get(String type) throws IOException, SQLException {
		return getUniqueRow(TicketActionType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_ACTION_TYPES;
	}
}
