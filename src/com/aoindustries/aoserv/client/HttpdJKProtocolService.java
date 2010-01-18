/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  HttpdJKProtocol
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.httpd_jk_protocols)
public interface HttpdJKProtocolService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,HttpdJKProtocol> {
}