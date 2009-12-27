package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Server;
import com.aoindustries.aoserv.client.ServerService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingServerService extends NoSwingServiceIntegerKey<Server> implements ServerService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingServerService(NoSwingConnector connector, ServerService<?,?> wrapped) {
        super(connector, Server.class, wrapped);
    }
}
