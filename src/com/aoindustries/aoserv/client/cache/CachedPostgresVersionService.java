package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresVersion;
import com.aoindustries.aoserv.client.PostgresVersionService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedPostgresVersionService extends CachedServiceIntegerKey<PostgresVersion> implements PostgresVersionService<CachedConnector,CachedConnectorFactory> {

    CachedPostgresVersionService(CachedConnector connector, PostgresVersionService<?,?> wrapped) {
        super(connector, PostgresVersion.class, wrapped);
    }
}
