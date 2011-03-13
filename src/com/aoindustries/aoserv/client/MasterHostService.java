/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  MasterHost
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.master_hosts)
public interface MasterHostService extends AOServService<Integer,MasterHost> {
}