package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.CvsRepository;
import com.aoindustries.aoserv.client.CvsRepositoryService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryCvsRepositoryService extends RetryServiceIntegerKey<CvsRepository> implements CvsRepositoryService<RetryConnector,RetryConnectorFactory> {

    RetryCvsRepositoryService(RetryConnector connector) {
        super(connector, CvsRepository.class);
    }
}
