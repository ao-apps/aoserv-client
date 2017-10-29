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

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends GlobalObjectStringKey<TicketStatus> implements Comparable<TicketStatus> {

	static final int COLUMN_STATUS = 0;
	static final int COLUMN_SORT_ORDER = 1;
	static final String COLUMN_STATUS_name = "status";
	static final String COLUMN_SORT_ORDER_name = "sort_order";

	/**
	 * The different ticket statuses.
	 */
	public static final String
		JUNK="junk",
		DELETED="deleted",
		CLOSED="closed",
		BOUNCED="bounced",
		HOLD="hold",
		OPEN="open"
	;

	private short sort_order;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_STATUS) return pkey;
		if(i==1) return sort_order;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public short getSortOrder() {
		return sort_order;
	}

	public String getStatus() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_STATI;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		sort_order = result.getShort(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readUTF().intern();
		sort_order = in.readShort();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeUTF(pkey);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeShort(sort_order);
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("TicketStatus."+pkey+".toString");
	}

	/**
	 * Localized description.
	 */
	public String getDescription() {
		return accessor.getMessage("TicketStatus."+pkey+".description");
	}

	@Override
	public int compareTo(TicketStatus o) {
		short sortOrder1 = sort_order;
		short sortOrder2 = o.sort_order;
		if(sortOrder1<sortOrder2) return -1;
		if(sortOrder1>sortOrder2) return 1;
		return 0;
	}
}
