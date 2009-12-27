package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingBackupPartitionService extends NoSwingServiceIntegerKey<BackupPartition> implements BackupPartitionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingBackupPartitionService(NoSwingConnector connector, BackupPartitionService<?,?> wrapped) {
        super(connector, BackupPartition.class, wrapped);
    }
}
