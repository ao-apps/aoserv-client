/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  ServerResource
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.server_resources)
public interface ServerResourceService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,ServerResource> {
}
