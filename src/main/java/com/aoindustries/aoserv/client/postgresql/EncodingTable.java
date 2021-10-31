/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Encoding
 *
 * @author  AO Industries, Inc.
 */
public final class EncodingTable extends GlobalTableIntegerKey<Encoding> {

	EncodingTable(AOServConnector connector) {
		super(connector, Encoding.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Encoding.COLUMN_ENCODING_name, ASCENDING),
		new OrderBy(Encoding.COLUMN_POSTGRES_VERSION_name+'.'+Version.COLUMN_MINOR_VERSION_name, ASCENDING),
		new OrderBy(Encoding.COLUMN_PKEY_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public Encoding get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Encoding.COLUMN_PKEY, pkey);
	}

	List<Encoding> getPostgresEncodings(Version version) throws IOException, SQLException {
		return getIndexedRows(Encoding.COLUMN_POSTGRES_VERSION, version.getPkey());
	}

	Encoding getPostgresEncoding(Version pv, String encoding) throws IOException, SQLException {
		// Use the index first
		List<Encoding> cached=getPostgresEncodings(pv);
		int cachedLen=cached.size();
		for (int c = 0; c < cachedLen; c++) {
			Encoding pe=cached.get(c);
			if (pe.getEncoding().equals(encoding)) return pe;
		}
		return null;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_ENCODINGS;
	}
}
