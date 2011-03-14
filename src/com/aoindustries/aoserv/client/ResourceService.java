/*
 * Copyright 2009-2011 by AO Industries, Inc.,
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
 * @see  Resource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.resources)
final public class ResourceService extends UnionService<Integer,Resource> {

    public ResourceService(AOServConnector connector) {
        super(connector, Integer.class, Resource.class);
    }

    private final AtomicReference<List<AOServService<Integer, ? extends Resource>>> subservices = new AtomicReference<List<AOServService<Integer, ? extends Resource>>>();

    @Override
    protected List<AOServService<Integer, ? extends Resource>> getSubServices() throws RemoteException {
        List<AOServService<Integer, ? extends Resource>> ss = subservices.get();
        if(ss==null) {
            List<AOServService<Integer,? extends AOServerResource>> asrs = connector.getAoServerResources().getSubServices();
            List<AOServService<Integer,? extends ServerResource>> srs = connector.getServerResources().getSubServices();
            List<AOServService<Integer,? extends Server>> ses = connector.getServers().getSubServices();

            ss = new ArrayList<AOServService<Integer, ? extends Resource>>(
                asrs.size()
                + 3
                + srs.size()
                + ses.size()
                + 1
            );
            ss.addAll(asrs);
            ss.add(connector.getDnsRecords());
            ss.add(connector.getDnsZones());
            ss.add(connector.getServerFarms());
            ss.addAll(srs);
            ss.addAll(ses);
            ss.add(connector.getRacks());
            ss = Collections.unmodifiableList(ss);
            if(!subservices.compareAndSet(null, ss)) ss = subservices.get(); // Created by another thread
        }
        return ss;
    }
}
