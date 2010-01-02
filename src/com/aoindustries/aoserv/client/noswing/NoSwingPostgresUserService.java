package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.PostgresUser;
import com.aoindustries.aoserv.client.PostgresUserService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingPostgresUserService extends NoSwingServiceIntegerKey<PostgresUser> implements PostgresUserService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingPostgresUserService(NoSwingConnector connector, PostgresUserService<?,?> wrapped) {
        super(connector, PostgresUser.class, wrapped);
    }
}
