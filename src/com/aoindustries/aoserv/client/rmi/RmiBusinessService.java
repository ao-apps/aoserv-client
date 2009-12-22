package com.aoindustries.aoserv.client.rmi;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.BusinessService;

/**
 * @author  AO Industries, Inc.
 */
final class RmiBusinessService extends RmiServiceStringKey<Business> implements BusinessService<RmiConnector,RmiConnectorFactory> {

    RmiBusinessService(RmiConnector connector) {
        super(connector, Business.class);
    }
}
