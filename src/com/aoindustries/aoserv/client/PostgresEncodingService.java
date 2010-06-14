/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PostgresEncoding
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.postgres_encodings)
public interface PostgresEncodingService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PostgresEncoding> {

    /* TODO
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
     */
}