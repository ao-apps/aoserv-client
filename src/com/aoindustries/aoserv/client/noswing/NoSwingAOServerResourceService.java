package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServerResource;
import com.aoindustries.aoserv.client.AOServerResourceService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingAOServerResourceService extends NoSwingServiceIntegerKey<AOServerResource> implements AOServerResourceService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingAOServerResourceService(NoSwingConnector connector, AOServerResourceService<?,?> wrapped) {
        super(connector, AOServerResource.class, wrapped);
    }
}
