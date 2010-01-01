package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingPostgresVersionService extends NoSwingServiceIntegerKey<PostgresVersion> implements PostgresVersionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingPostgresVersionService(NoSwingConnector connector, PostgresVersionService<?,?> wrapped) {
        super(connector, PostgresVersion.class, wrapped);
    }
}
