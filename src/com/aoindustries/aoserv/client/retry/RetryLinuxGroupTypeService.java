package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxGroupType;
import com.aoindustries.aoserv.client.LinuxGroupTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryLinuxGroupTypeService extends RetryServiceStringKey<LinuxGroupType> implements LinuxGroupTypeService<RetryConnector,RetryConnectorFactory> {

    RetryLinuxGroupTypeService(RetryConnector connector) {
        super(connector, LinuxGroupType.class);
    }
}
