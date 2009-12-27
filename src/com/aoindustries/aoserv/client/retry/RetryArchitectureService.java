package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Architecture;
import com.aoindustries.aoserv.client.ArchitectureService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryArchitectureService extends RetryServiceStringKey<Architecture> implements ArchitectureService<RetryConnector,RetryConnectorFactory> {

    RetryArchitectureService(RetryConnector connector) {
        super(connector, Architecture.class);
    }
}
