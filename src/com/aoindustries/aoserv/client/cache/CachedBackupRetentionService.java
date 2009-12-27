package com.aoindustries.aoserv.client.cache;

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
final class CachedBackupRetentionService extends CachedServiceShortKey<BackupRetention> implements BackupRetentionService<CachedConnector,CachedConnectorFactory> {

    CachedBackupRetentionService(CachedConnector connector, BackupRetentionService<?,?> wrapped) {
        super(connector, BackupRetention.class, wrapped);
    }
}
