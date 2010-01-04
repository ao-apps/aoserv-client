package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccountType;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLinuxAccountTypeService extends RetryServiceStringKey<LinuxAccountType> implements LinuxAccountTypeService<RetryConnector,RetryConnectorFactory> {

    RetryLinuxAccountTypeService(RetryConnector connector) {
        super(connector, LinuxAccountType.class);
    }
}
