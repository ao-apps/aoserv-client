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
import java.util.List;

/**
 * @see  PostgresEncoding
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresEncodingTable extends GlobalTableIntegerKey<PostgresEncoding> {

	PostgresEncodingTable(AOServConnector connector) {
		super(connector, PostgresEncoding.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresEncoding.COLUMN_ENCODING_name, ASCENDING),
		new OrderBy(PostgresEncoding.COLUMN_POSTGRES_VERSION_name+'.'+PostgresVersion.COLUMN_MINOR_VERSION_name, ASCENDING),
		new OrderBy(PostgresEncoding.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PostgresEncoding get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PostgresEncoding.COLUMN_PKEY, pkey);
	}

	List<PostgresEncoding> getPostgresEncodings(PostgresVersion version) throws IOException, SQLException {
		return getIndexedRows(PostgresEncoding.COLUMN_POSTGRES_VERSION, version.pkey);
	}

	PostgresEncoding getPostgresEncoding(PostgresVersion pv, String encoding) throws IOException, SQLException {
		// Use the index first
		List<PostgresEncoding> cached=getPostgresEncodings(pv);
		int cachedLen=cached.size();
		for (int c = 0; c < cachedLen; c++) {
			PostgresEncoding pe=cached.get(c);
			if (pe.encoding.equals(encoding)) return pe;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_ENCODINGS;
	}
}
