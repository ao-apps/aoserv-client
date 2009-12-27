package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingServerFarmService extends NoSwingServiceStringKey<ServerFarm> implements ServerFarmService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingServerFarmService(NoSwingConnector connector, ServerFarmService<?,?> wrapped) {
        super(connector, ServerFarm.class, wrapped);
    }
}
