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
 * @see  AOServerResource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ao_server_resources)
final public class AOServerResourceService extends UnionService<Integer,AOServerResource> {

    public AOServerResourceService(AOServConnector connector) {
        super(connector, Integer.class, AOServerResource.class);
    }

    private final AtomicReference<List<AOServService<Integer, ? extends AOServerResource>>> subservices = new AtomicReference<List<AOServService<Integer, ? extends AOServerResource>>>();

    @Override
    protected List<AOServService<Integer, ? extends AOServerResource>> getSubServices() throws RemoteException {
        List<AOServService<Integer, ? extends AOServerResource>> ss = subservices.get();
        if(ss==null) {
            ss = new ArrayList<AOServService<Integer, ? extends AOServerResource>>(12);
            ss.add(connector.getCvsRepositories());
            ss.add(connector.getHttpdServers());
            ss.add(connector.getHttpdSites());
            ss.add(connector.getLinuxAccounts());
            ss.add(connector.getLinuxGroups());
            ss.add(connector.getMysqlDatabases());
            ss.add(connector.getMysqlServers());
            ss.add(connector.getMysqlUsers());
            ss.add(connector.getPostgresDatabases());
            ss.add(connector.getPostgresServers());
            ss.add(connector.getPostgresUsers());
            ss.add(connector.getPrivateFtpServers());
            ss = Collections.unmodifiableList(ss);
            if(!subservices.compareAndSet(null, ss)) ss = subservices.get(); // Created by another thread
        }
        return ss;
    }
}
