package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.CountryCode;
import com.aoindustries.aoserv.client.CountryCodeService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedCountryCodeService extends CachedServiceStringKey<CountryCode> implements CountryCodeService<CachedConnector,CachedConnectorFactory> {

    CachedCountryCodeService(CachedConnector connector, CountryCodeService<?,?> wrapped) {
        super(connector, CountryCode.class, wrapped);
    }
}
