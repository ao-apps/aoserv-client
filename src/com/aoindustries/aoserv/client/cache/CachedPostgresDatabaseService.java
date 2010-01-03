package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresDatabase;
import com.aoindustries.aoserv.client.PostgresDatabaseService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedPostgresDatabaseService extends CachedServiceIntegerKey<PostgresDatabase> implements PostgresDatabaseService<CachedConnector,CachedConnectorFactory> {

    CachedPostgresDatabaseService(CachedConnector connector, PostgresDatabaseService<?,?> wrapped) {
        super(connector, PostgresDatabase.class, wrapped);
    }
}
