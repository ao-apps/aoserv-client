/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.BusinessAdministrator;
import com.aoindustries.aoserv.client.reseller.Reseller;
import com.aoindustries.aoserv.client.schema.SchemaTable;
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

	public TicketAssignmentTable(AOServConnector connector) {
		super(connector, TicketAssignment.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(TicketAssignment.COLUMN_TICKET_name, ASCENDING),
		new OrderBy(TicketAssignment.COLUMN_RESELLER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public TicketAssignment get(int pkey) throws IOException, SQLException {
		return getUniqueRow(TicketAssignment.COLUMN_PKEY, pkey);
	}

	List<TicketAssignment> getTicketAssignments(Ticket ticket) throws IOException, SQLException {
		return getIndexedRows(TicketAssignment.COLUMN_TICKET, ticket.getTicketID());
	}

	public List<TicketAssignment> getTicketAssignments(Reseller reseller) throws IOException, SQLException {
		return getIndexedRows(TicketAssignment.COLUMN_RESELLER, reseller.getBrand_business_accounting());
	}

	public List<TicketAssignment> getTicketAssignments(BusinessAdministrator ba) throws IOException, SQLException {
		return getIndexedRows(TicketAssignment.COLUMN_ADMINISTRATOR, ba.getUsername_userId());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_ASSIGNMENTS;
	}
}
