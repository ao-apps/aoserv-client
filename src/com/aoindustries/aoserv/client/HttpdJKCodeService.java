/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  HttpdJKCode
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.httpd_jk_codes)
public interface HttpdJKCodeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,HttpdJKCode> {
}