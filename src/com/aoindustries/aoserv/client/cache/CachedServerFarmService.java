package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServerFarm;
import com.aoindustries.aoserv.client.ServerFarmService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedServerFarmService extends CachedServiceStringKey<ServerFarm> implements ServerFarmService<CachedConnector,CachedConnectorFactory> {

    CachedServerFarmService(CachedConnector connector, ServerFarmService<?,?> wrapped) {
        super(connector, ServerFarm.class, wrapped);
    }
}