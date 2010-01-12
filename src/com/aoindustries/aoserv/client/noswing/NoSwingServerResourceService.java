package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ServerResource;
import com.aoindustries.aoserv.client.ServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingServerResourceService extends NoSwingServiceIntegerKey<ServerResource> implements ServerResourceService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingServerResourceService(NoSwingConnector connector, ServerResourceService<?,?> wrapped) {
        super(connector, ServerResource.class, wrapped);
    }
}
