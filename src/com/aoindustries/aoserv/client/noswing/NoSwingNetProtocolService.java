package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetProtocol;
import com.aoindustries.aoserv.client.NetProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingNetProtocolService extends NoSwingServiceStringKey<NetProtocol> implements NetProtocolService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingNetProtocolService(NoSwingConnector connector, NetProtocolService<?,?> wrapped) {
        super(connector, NetProtocol.class, wrapped);
    }
}
