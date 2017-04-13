/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
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
