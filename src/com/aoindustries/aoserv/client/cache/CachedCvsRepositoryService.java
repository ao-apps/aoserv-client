package com.aoindustries.aoserv.client.cache;

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
final class CachedCvsRepositoryService extends CachedServiceIntegerKey<CvsRepository> implements CvsRepositoryService<CachedConnector,CachedConnectorFactory> {

    CachedCvsRepositoryService(CachedConnector connector, CvsRepositoryService<?,?> wrapped) {
        super(connector, CvsRepository.class, wrapped);
    }
}
