/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class BackupServer extends Server {

    public BackupServer() {
    }

    public BackupServer(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int farm,
        String description,
        Integer operatingSystemVersion,
        String name,
        boolean monitoringEnabled
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, farm, description, operatingSystemVersion, name, monitoringEnabled);
    }
}
