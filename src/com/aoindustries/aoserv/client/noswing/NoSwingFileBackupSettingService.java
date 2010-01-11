package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingFileBackupSettingService extends NoSwingServiceIntegerKey<FileBackupSetting> implements FileBackupSettingService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingFileBackupSettingService(NoSwingConnector connector, FileBackupSettingService<?,?> wrapped) {
        super(connector, FileBackupSetting.class, wrapped);
    }
}
