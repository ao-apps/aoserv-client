package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresEncoding;
import com.aoindustries.aoserv.client.PostgresEncodingService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedPostgresEncodingService extends CachedServiceIntegerKey<PostgresEncoding> implements PostgresEncodingService<CachedConnector,CachedConnectorFactory> {

    CachedPostgresEncodingService(CachedConnector connector, PostgresEncodingService<?,?> wrapped) {
        super(connector, PostgresEncoding.class, wrapped);
    }
}
