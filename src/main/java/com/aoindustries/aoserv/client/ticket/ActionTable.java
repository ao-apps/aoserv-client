/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.ticket;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see Action
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class ActionTable extends CachedTableIntegerKey<Action> {

	ActionTable(AOServConnector connector) {
		super(connector, Action.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Action.COLUMN_TICKET_name, ASCENDING),
		new OrderBy(Action.COLUMN_TIME_name, ASCENDING),
		new OrderBy(Action.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Action get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Action.COLUMN_PKEY, pkey);
	}

	List<Action> getActions(Ticket ticket) throws IOException, SQLException {
		return getIndexedRows(Action.COLUMN_TICKET, ticket.getTicketID());
	}

	public List<Action> getActions(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(Action.COLUMN_ADMINISTRATOR, ba.getUsername_userId());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TICKET_ACTIONS;
	}
}
