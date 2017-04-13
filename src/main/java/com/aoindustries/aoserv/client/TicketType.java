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
