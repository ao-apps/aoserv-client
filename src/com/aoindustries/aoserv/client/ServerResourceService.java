/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see  ServerResource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.server_resources)
final public class ServerResourceService extends UnionService<Integer,ServerResource> {

    public ServerResourceService(AOServConnector connector) {
        super(connector, Integer.class, ServerResource.class);
    }

    private final AtomicReference<List<AOServService<Integer, ? extends ServerResource>>> subservices = new AtomicReference<List<AOServService<Integer, ? extends ServerResource>>>();

    @Override
    protected List<AOServService<Integer, ? extends ServerResource>> getSubServices() throws RemoteException {
        List<AOServService<Integer, ? extends ServerResource>> ss = subservices.get();
        if(ss==null) {
            ss = new ArrayList<AOServService<Integer, ? extends ServerResource>>(1);
            ss.add(connector.getIpAddresses());
            ss = Collections.unmodifiableList(ss);
            // TODO: How to make this work? return Collections.singletonList((AOServService<Integer, ? extends ServerResource>)connector.getIpAddresses());
            if(!subservices.compareAndSet(null, ss)) ss = subservices.get(); // Created by another thread
        }
        return ss;
    }
}
