/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
