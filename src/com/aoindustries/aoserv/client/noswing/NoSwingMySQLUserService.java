package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.MySQLUser;
import com.aoindustries.aoserv.client.MySQLUserService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingMySQLUserService extends NoSwingServiceIntegerKey<MySQLUser> implements MySQLUserService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingMySQLUserService(NoSwingConnector connector, MySQLUserService<?,?> wrapped) {
        super(connector, MySQLUser.class, wrapped);
    }
}
