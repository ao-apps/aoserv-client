package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Protocol;
import com.aoindustries.aoserv.client.ProtocolService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingProtocolService extends NoSwingServiceStringKey<Protocol> implements ProtocolService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingProtocolService(NoSwingConnector connector, ProtocolService<?,?> wrapped) {
        super(connector, Protocol.class, wrapped);
    }
}
