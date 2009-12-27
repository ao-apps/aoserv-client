package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BackupPartition;
import com.aoindustries.aoserv.client.BackupPartitionService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedBackupPartitionService extends CachedServiceIntegerKey<BackupPartition> implements BackupPartitionService<CachedConnector,CachedConnectorFactory> {

    CachedBackupPartitionService(CachedConnector connector, BackupPartitionService<?,?> wrapped) {
        super(connector, BackupPartition.class, wrapped);
    }
}
