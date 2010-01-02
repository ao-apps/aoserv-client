package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresUser;
import com.aoindustries.aoserv.client.PostgresUserService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedPostgresUserService extends CachedServiceIntegerKey<PostgresUser> implements PostgresUserService<CachedConnector,CachedConnectorFactory> {

    CachedPostgresUserService(CachedConnector connector, PostgresUserService<?,?> wrapped) {
        super(connector, PostgresUser.class, wrapped);
    }
}
