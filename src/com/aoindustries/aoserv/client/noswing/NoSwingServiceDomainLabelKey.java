package com.aoindustries.aoserv.client.noswing;

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
abstract class NoSwingServiceDomainLabelKey<V extends AOServObjectDomainLabelKey<V>> extends NoSwingService<DomainLabel,V> implements AOServServiceDomainLabelKey<NoSwingConnector,NoSwingConnectorFactory,V> {

    NoSwingServiceDomainLabelKey(NoSwingConnector connector, Class<V> valueClass, AOServServiceDomainLabelKey<?,?,V> wrapped) {
        super(connector, DomainLabel.class, valueClass, wrapped);
    }
}
