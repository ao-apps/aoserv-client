package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccount;
import com.aoindustries.aoserv.client.LinuxAccountService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLinuxAccountService extends RetryServiceIntegerKey<LinuxAccount> implements LinuxAccountService<RetryConnector,RetryConnectorFactory> {

    RetryLinuxAccountService(RetryConnector connector) {
        super(connector, LinuxAccount.class);
    }
}
