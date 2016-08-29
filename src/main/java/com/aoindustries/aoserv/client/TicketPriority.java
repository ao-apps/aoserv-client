/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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
final public class TicketPriority extends GlobalObjectStringKey<TicketPriority> implements Comparable<TicketPriority> {

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
	Object getColumnImpl(int i) {
		if(i==COLUMN_PRIORITY) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getPriority() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_PRIORITIES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}

	@Override
	public int compareTo(TicketPriority o) {
		return pkey.compareTo(o.pkey);
	}
}
