package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.List;

/**
 * @see  PostgresEncoding
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresEncodingTable extends GlobalTableIntegerKey<PostgresEncoding> {

    PostgresEncodingTable(AOServConnector connector) {
	super(connector, PostgresEncoding.class);
    }

    public PostgresEncoding get(Object pkey) {
	return getUniqueRow(PostgresEncoding.COLUMN_PKEY, pkey);
    }

    public PostgresEncoding get(int pkey) {
	return getUniqueRow(PostgresEncoding.COLUMN_PKEY, pkey);
    }

    List<PostgresEncoding> getPostgresEncodings(PostgresVersion version) {
        return getIndexedRows(PostgresEncoding.COLUMN_POSTGRES_VERSION, version.pkey);
    }

    PostgresEncoding getPostgresEncoding(PostgresVersion pv, String encoding) {
        // Use the index first
        List<PostgresEncoding> cached=getPostgresEncodings(pv);
        int cachedLen=cached.size();
        for (int c = 0; c < cachedLen; c++) {
            PostgresEncoding pe=cached.get(c);
            if (pe.encoding.equals(encoding)) return pe;
        }
        return null;
    }

    int getTableID() {
        return SchemaTable.POSTGRES_ENCODINGS;
    }
}