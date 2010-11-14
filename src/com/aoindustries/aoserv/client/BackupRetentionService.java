package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  BackupRetention
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.backup_retentions)
public interface BackupRetentionService extends AOServService<Short,BackupRetention> {
}
