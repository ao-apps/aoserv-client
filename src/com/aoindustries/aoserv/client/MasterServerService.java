/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  MasterServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.master_servers)
public interface MasterServerService extends AOServService<Integer,MasterServer> {
}