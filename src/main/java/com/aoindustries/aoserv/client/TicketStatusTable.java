/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  TicketStatus
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatusTable extends GlobalTableStringKey<TicketStatus> {

	TicketStatusTable(AOServConnector connector) {
		super(connector, TicketStatus.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TicketStatus.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_STATI;
	}

	@Override
	public TicketStatus get(String status) throws IOException, SQLException {
		return getUniqueRow(TicketStatus.COLUMN_STATUS, status);
	}
}