package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetBind;
import com.aoindustries.aoserv.client.NetBindService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryNetBindService extends RetryServiceIntegerKey<NetBind> implements NetBindService<RetryConnector,RetryConnectorFactory> {

    RetryNetBindService(RetryConnector connector) {
        super(connector, NetBind.class);
    }
}
