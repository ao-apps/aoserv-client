/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each <code>Ticket</code> is of a specific <code>TicketType</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketType extends GlobalObjectStringKey<TicketType> {

	static final int COLUMN_TYPE=0;
	static final String COLUMN_TYPE_name = "type";

	/**
	 * The types of <code>Ticket</code>s.
	 */
	public static final String
		CONTACT="contact",
		LOGS="logs",
		SUPPORT="support",
		PROJECTS="projects",
		INTERNAL="internal"
	;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_TYPE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	String toStringImpl() {
		return accessor.getMessage("TicketType."+pkey+".toString");
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TICKET_TYPES;
	}

	public String getType() {
		return pkey;
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
		if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) {
			out.writeUTF(pkey); // description
			out.writeBoolean(false); // client_view
		}
	}
}
