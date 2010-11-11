/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;

/**
 * @see  DnsTld
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.dns_tlds)
public interface DnsTldService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,DomainName,DnsTld> {
}
