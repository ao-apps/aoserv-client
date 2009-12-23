package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.ResourceType;
import com.aoindustries.aoserv.client.ResourceTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingResourceTypeService extends NoSwingServiceStringKey<ResourceType> implements ResourceTypeService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingResourceTypeService(NoSwingConnector connector, ResourceTypeService<?,?> wrapped) {
        super(connector, ResourceType.class, wrapped);
    }
}
