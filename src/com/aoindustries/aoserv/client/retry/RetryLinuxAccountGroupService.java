package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccountGroup;
import com.aoindustries.aoserv.client.LinuxAccountGroupService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLinuxAccountGroupService extends RetryServiceIntegerKey<LinuxAccountGroup> implements LinuxAccountGroupService<RetryConnector,RetryConnectorFactory> {

    RetryLinuxAccountGroupService(RetryConnector connector) {
        super(connector, LinuxAccountGroup.class);
    }
}
