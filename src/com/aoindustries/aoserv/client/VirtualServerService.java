/*
 * Copyright 2008-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  VirtualServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.virtual_servers)
public interface VirtualServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,VirtualServer> {
}
