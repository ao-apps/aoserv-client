package com.aoindustries.aoserv.client.cache;

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
final class CachedNetBindService extends CachedServiceIntegerKey<NetBind> implements NetBindService<CachedConnector,CachedConnectorFactory> {

    CachedNetBindService(CachedConnector connector, NetBindService<?,?> wrapped) {
        super(connector, NetBind.class, wrapped);
    }
}
