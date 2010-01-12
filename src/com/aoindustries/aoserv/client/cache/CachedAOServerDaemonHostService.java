package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServerDaemonHost;
import com.aoindustries.aoserv.client.AOServerDaemonHostService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedAOServerDaemonHostService extends CachedServiceIntegerKey<AOServerDaemonHost> implements AOServerDaemonHostService<CachedConnector,CachedConnectorFactory> {

    CachedAOServerDaemonHostService(CachedConnector connector, AOServerDaemonHostService<?,?> wrapped) {
        super(connector, AOServerDaemonHost.class, wrapped);
    }
}
