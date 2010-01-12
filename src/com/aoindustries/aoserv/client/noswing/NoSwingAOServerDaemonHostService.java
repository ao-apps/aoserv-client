package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingAOServerDaemonHostService extends NoSwingServiceIntegerKey<AOServerDaemonHost> implements AOServerDaemonHostService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingAOServerDaemonHostService(NoSwingConnector connector, AOServerDaemonHostService<?,?> wrapped) {
        super(connector, AOServerDaemonHost.class, wrapped);
    }
}
