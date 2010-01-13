package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetTcpRedirect;
import com.aoindustries.aoserv.client.NetTcpRedirectService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingNetTcpRedirectService extends NoSwingServiceIntegerKey<NetTcpRedirect> implements NetTcpRedirectService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingNetTcpRedirectService(NoSwingConnector connector, NetTcpRedirectService<?,?> wrapped) {
        super(connector, NetTcpRedirect.class, wrapped);
    }
}
