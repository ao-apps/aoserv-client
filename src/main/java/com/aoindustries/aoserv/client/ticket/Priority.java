/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.ticket;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>Ticket</code>s are prioritized by both the client and
 * support personnel.  Each priority is set as a <code>TicketPriority</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class Priority extends GlobalObjectStringKey<Priority> implements Comparable<Priority> {

	static final int COLUMN_PRIORITY=0;
	static final String COLUMN_PRIORITY_name = "priority";

	/**
	 * The possible ticket priorities.
	 */
	public static final String
		LOW="0-Low",
		NORMAL="1-Normal",
		HIGH="2-High",
		URGENT="3-Urgent"
	;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_PRIORITY) return pkey;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public String getPriority() {
		return pkey;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TICKET_PRIORITIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
	}

	@Override
	public int compareTo(Priority o) {
		return pkey.compareTo(o.pkey);
	}
}
