package com.aoindustries.aoserv.client.cache;

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
final class CachedBusinessService extends CachedServiceAccountingCodeKey<Business> implements BusinessService<CachedConnector,CachedConnectorFactory> {

    CachedBusinessService(CachedConnector connector, BusinessService<?,?> wrapped) {
        super(connector, Business.class, wrapped);
    }
}
