/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>DNSForbiddenZone</code> is a zone that may not be hosted by
 * AO Industries' name servers.  These domains are forbidden to protect
 * systems that require accurate name resolution for security or reliability.
 *
 * @see  DNSZone
 *
 * @author  AO Industries, Inc.
 */
final public class DNSForbiddenZone extends GlobalObjectStringKey<DNSForbiddenZone> {

	static final int COLUMN_ZONE=0;
	static final String COLUMN_ZONE_name = "zone";

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_ZONE) return pkey;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_FORBIDDEN_ZONES;
	}

	public String getZone() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getString(1);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
	}
}
