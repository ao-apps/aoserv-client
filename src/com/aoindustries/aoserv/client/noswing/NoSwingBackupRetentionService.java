package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingBackupRetentionService extends NoSwingServiceShortKey<BackupRetention> implements BackupRetentionService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingBackupRetentionService(NoSwingConnector connector, BackupRetentionService<?,?> wrapped) {
        super(connector, BackupRetention.class, wrapped);
    }
}
