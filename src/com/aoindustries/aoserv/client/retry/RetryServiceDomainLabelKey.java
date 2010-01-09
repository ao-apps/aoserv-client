package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServObjectDomainLabelKey;
import com.aoindustries.aoserv.client.AOServServiceDomainLabelKey;
import com.aoindustries.aoserv.client.validator.DomainLabel;

/**
 * @author  AO Industries, Inc.
 */
abstract class RetryServiceDomainLabelKey<V extends AOServObjectDomainLabelKey<V>> extends RetryService<DomainLabel,V> implements AOServServiceDomainLabelKey<RetryConnector,RetryConnectorFactory,V> {

    RetryServiceDomainLabelKey(RetryConnector connector, Class<V> valueClass) {
        super(connector, DomainLabel.class, valueClass);
    }
}
