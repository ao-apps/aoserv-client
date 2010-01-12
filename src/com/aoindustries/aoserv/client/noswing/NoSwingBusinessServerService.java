package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessServer;
import com.aoindustries.aoserv.client.BusinessServerService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingBusinessServerService extends NoSwingServiceIntegerKey<BusinessServer> implements BusinessServerService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingBusinessServerService(NoSwingConnector connector, BusinessServerService<?,?> wrapped) {
        super(connector, BusinessServer.class, wrapped);
    }
}
