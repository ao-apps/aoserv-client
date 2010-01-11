package com.aoindustries.aoserv.client.retry;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FileBackupSetting;
import com.aoindustries.aoserv.client.FileBackupSettingService;

/**
 * @author  AO Industries, Inc.
 */
final class RetryFileBackupSettingService extends RetryServiceIntegerKey<FileBackupSetting> implements FileBackupSettingService<RetryConnector,RetryConnectorFactory> {

    RetryFileBackupSettingService(RetryConnector connector) {
        super(connector, FileBackupSetting.class);
    }
}
