package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Username;
import com.aoindustries.aoserv.client.UsernameService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryUsernameService extends RetryServiceUserIdKey<Username> implements UsernameService<RetryConnector,RetryConnectorFactory> {

    RetryUsernameService(RetryConnector connector) {
        super(connector, Username.class);
    }
}
