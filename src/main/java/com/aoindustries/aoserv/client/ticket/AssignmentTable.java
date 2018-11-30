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
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.reseller.Reseller;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see Assignment
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class AssignmentTable extends CachedTableIntegerKey<Assignment> {

	public AssignmentTable(AOServConnector connector) {
		super(connector, Assignment.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Assignment.COLUMN_TICKET_name, ASCENDING),
		new OrderBy(Assignment.COLUMN_RESELLER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Assignment get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Assignment.COLUMN_PKEY, pkey);
	}

	List<Assignment> getTicketAssignments(Ticket ticket) throws IOException, SQLException {
		return getIndexedRows(Assignment.COLUMN_TICKET, ticket.getTicketID());
	}

	public List<Assignment> getTicketAssignments(Reseller reseller) throws IOException, SQLException {
		return getIndexedRows(Assignment.COLUMN_RESELLER, reseller.getBrand_business_accounting());
	}

	public List<Assignment> getTicketAssignments(Administrator ba) throws IOException, SQLException {
		return getIndexedRows(Assignment.COLUMN_ADMINISTRATOR, ba.getUsername_userId());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TICKET_ASSIGNMENTS;
	}
}
