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
 * @see  AOServerResource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ao_server_resources)
public class AOServerResourceService extends UnionService<Integer,AOServerResource> {

    public AOServerResourceService(AOServConnector connector) {
        super(connector, Integer.class, AOServerResource.class);
    }

    @Override
    protected List<AOServService<Integer, ? extends AOServerResource>> getSubServices() throws RemoteException {
        List<AOServService<Integer, ? extends AOServerResource>> subservices =
            new ArrayList<AOServService<Integer, ? extends AOServerResource>>(12);
        subservices.add(connector.getCvsRepositories());
        subservices.add(connector.getHttpdServers());
        subservices.add(connector.getHttpdSites());
        subservices.add(connector.getLinuxAccounts());
        subservices.add(connector.getLinuxGroups());
        subservices.add(connector.getMysqlDatabases());
        subservices.add(connector.getMysqlServers());
        subservices.add(connector.getMysqlUsers());
        subservices.add(connector.getPostgresDatabases());
        subservices.add(connector.getPostgresServers());
        subservices.add(connector.getPostgresUsers());
        subservices.add(connector.getPrivateFtpServers());
        return subservices;
    }
}
