package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BackupRetention;
import com.aoindustries.aoserv.client.BackupRetentionService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryBackupRetentionService extends RetryServiceShortKey<BackupRetention> implements BackupRetentionService<RetryConnector,RetryConnectorFactory> {

    RetryBackupRetentionService(RetryConnector connector) {
        super(connector, BackupRetention.class);
    }
}
