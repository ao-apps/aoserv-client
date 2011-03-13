/*
 * Copyright 2001-2011 by AO Industries, Inc.,
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
public interface DnsTldService extends AOServService<DomainName,DnsTld> {
}
