package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Resource;
import com.aoindustries.aoserv.client.ResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingResourceService extends NoSwingServiceIntegerKey<Resource> implements ResourceService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingResourceService(NoSwingConnector connector, ResourceService<?,?> wrapped) {
        super(connector, Resource.class, wrapped);
    }
}
