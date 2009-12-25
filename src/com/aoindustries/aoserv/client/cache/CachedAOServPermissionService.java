package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.AOServPermissionService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedAOServPermissionService extends CachedServiceStringKey<AOServPermission> implements AOServPermissionService<CachedConnector,CachedConnectorFactory> {

    CachedAOServPermissionService(CachedConnector connector, AOServPermissionService<?,?> wrapped) {
        super(connector, AOServPermission.class, wrapped);
    }
}
