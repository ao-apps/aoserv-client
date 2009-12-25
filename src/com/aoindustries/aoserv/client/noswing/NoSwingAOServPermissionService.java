package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingAOServPermissionService extends NoSwingServiceStringKey<AOServPermission> implements AOServPermissionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingAOServPermissionService(NoSwingConnector connector, AOServPermissionService<?,?> wrapped) {
        super(connector, AOServPermission.class, wrapped);
    }
}
