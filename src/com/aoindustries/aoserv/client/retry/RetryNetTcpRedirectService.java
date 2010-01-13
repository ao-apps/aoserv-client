package com.aoindustries.aoserv.client.retry;

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
final class RetryNetTcpRedirectService extends RetryServiceIntegerKey<NetTcpRedirect> implements NetTcpRedirectService<RetryConnector,RetryConnectorFactory> {

    RetryNetTcpRedirectService(RetryConnector connector) {
        super(connector, NetTcpRedirect.class);
    }
}
