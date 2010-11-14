/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Resource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.resources)
public class ResourceService<
    C extends AOServConnector<C,F>,
    F extends AOServConnectorFactory<C,F>
> extends UnionService<C,F,Integer,Resource> {

    public ResourceService(AOServConnector<C,F> connector) {
        super(connector, Integer.class, Resource.class);
    }

    @Override
    protected List<AOServService<C, F, Integer, ? extends Resource>> getSubServices() throws RemoteException {
        List<AOServService<C, F, Integer, ? extends Resource>> subservices = new ArrayList<AOServService<C, F, Integer, ? extends Resource>>(4);
        subservices.add(connector.getAoServerResources());
        subservices.add(connector.getDnsRecords());
        subservices.add(connector.getDnsZones());
        subservices.add(connector.getServerResources());
        return subservices;
    }
}
