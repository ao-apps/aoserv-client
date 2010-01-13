/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  NetTcpRedirect
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.net_tcp_redirects)
public interface NetTcpRedirectService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,NetTcpRedirect> {
}
