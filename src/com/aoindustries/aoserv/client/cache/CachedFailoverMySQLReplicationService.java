package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverMySQLReplication;
import com.aoindustries.aoserv.client.FailoverMySQLReplicationService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedFailoverMySQLReplicationService extends CachedServiceIntegerKey<FailoverMySQLReplication> implements FailoverMySQLReplicationService<CachedConnector,CachedConnectorFactory> {

    CachedFailoverMySQLReplicationService(CachedConnector connector, FailoverMySQLReplicationService<?,?> wrapped) {
        super(connector, FailoverMySQLReplication.class, wrapped);
    }
}
