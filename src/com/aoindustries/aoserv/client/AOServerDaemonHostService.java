/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  AOServerDaemonHost
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.ao_server_daemon_hosts)
public interface AOServerDaemonHostService extends AOServService<Integer,AOServerDaemonHost> {
}
