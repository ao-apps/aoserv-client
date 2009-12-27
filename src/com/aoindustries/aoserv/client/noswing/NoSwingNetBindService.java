package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.NetBind;
import com.aoindustries.aoserv.client.NetBindService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingNetBindService extends NoSwingServiceIntegerKey<NetBind> implements NetBindService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingNetBindService(NoSwingConnector connector, NetBindService<?,?> wrapped) {
        super(connector, NetBind.class, wrapped);
    }
}
