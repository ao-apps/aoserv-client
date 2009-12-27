package com.aoindustries.aoserv.client.retry;

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
final class RetryBackupPartitionService extends RetryServiceIntegerKey<BackupPartition> implements BackupPartitionService<RetryConnector,RetryConnectorFactory> {

    RetryBackupPartitionService(RetryConnector connector) {
        super(connector, BackupPartition.class);
    }
}
