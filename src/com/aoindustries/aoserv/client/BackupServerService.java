/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  BackupServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.backup_servers)
public interface BackupServerService extends AOServService<Integer,BackupServer> {
}