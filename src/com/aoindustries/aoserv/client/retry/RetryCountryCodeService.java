package com.aoindustries.aoserv.client.retry;

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
final class RetryCountryCodeService extends RetryServiceStringKey<CountryCode> implements CountryCodeService<RetryConnector,RetryConnectorFactory> {

    RetryCountryCodeService(RetryConnector connector) {
        super(connector, CountryCode.class);
    }
}
