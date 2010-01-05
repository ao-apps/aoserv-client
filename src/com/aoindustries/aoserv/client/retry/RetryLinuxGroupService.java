package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxGroup;
import com.aoindustries.aoserv.client.LinuxGroupService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLinuxGroupService extends RetryServiceIntegerKey<LinuxGroup> implements LinuxGroupService<RetryConnector,RetryConnectorFactory> {

    RetryLinuxGroupService(RetryConnector connector) {
        super(connector, LinuxGroup.class);
    }
}
