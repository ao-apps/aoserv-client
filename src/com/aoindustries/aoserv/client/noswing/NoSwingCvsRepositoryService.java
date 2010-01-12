package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingCvsRepositoryService extends NoSwingServiceIntegerKey<CvsRepository> implements CvsRepositoryService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingCvsRepositoryService(NoSwingConnector connector, CvsRepositoryService<?,?> wrapped) {
        super(connector, CvsRepository.class, wrapped);
    }
}
