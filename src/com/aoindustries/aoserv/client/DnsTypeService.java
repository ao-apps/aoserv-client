/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  DnsType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.dns_types)
public interface DnsTypeService extends AOServService<String,DnsType> {
}