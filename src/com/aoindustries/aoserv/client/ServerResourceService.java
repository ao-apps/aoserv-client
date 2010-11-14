/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  ServerResource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.server_resources)
public class ServerResourceService extends UnionService<Integer,ServerResource> {

    public ServerResourceService(AOServConnector connector) {
        super(connector, Integer.class, ServerResource.class);
    }

    @Override
    protected List<AOServService<Integer, ? extends ServerResource>> getSubServices() throws RemoteException {
        List<AOServService<Integer, ? extends ServerResource>> subservices =
            new ArrayList<AOServService<Integer, ? extends ServerResource>>(1);
        subservices.add(connector.getIpAddresses());
        return subservices;
    }
}
