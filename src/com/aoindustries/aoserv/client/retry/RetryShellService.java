package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Shell;
import com.aoindustries.aoserv.client.ShellService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryShellService extends RetryServiceUnixPathKey<Shell> implements ShellService<RetryConnector,RetryConnectorFactory> {

    RetryShellService(RetryConnector connector) {
        super(connector, Shell.class);
    }
}
