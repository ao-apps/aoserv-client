package com.aoindustries.aoserv.client.rmi;

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
final class RmiResourceTypeService extends RmiServiceStringKey<ResourceType> implements ResourceTypeService<RmiConnector,RmiConnectorFactory> {

    RmiResourceTypeService(RmiConnector connector) {
        super(connector, ResourceType.class);
    }
}
