package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.BusinessAdministratorService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingBusinessAdministratorService extends NoSwingServiceStringKey<BusinessAdministrator> implements BusinessAdministratorService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingBusinessAdministratorService(NoSwingConnector connector, BusinessAdministratorService<?,?> wrapped) {
        super(connector, BusinessAdministrator.class, wrapped);
    }
}
