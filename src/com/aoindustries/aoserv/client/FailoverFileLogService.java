/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  FailoverFileLog
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.failover_file_log)
public interface FailoverFileLogService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,FailoverFileLog> {
}
