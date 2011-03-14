/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;

/**
 * A <code>BackupServer</code> is an off-site unmanaged server that is only in the system
 * for the purpose of minimal accounting.  It is neither a physical server or a virtual
 * server in the datacenter.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupServer extends Server implements DtoFactory<com.aoindustries.aoserv.client.dto.BackupServer> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 1479978360576533451L;

    public BackupServer(
        AOServConnector connector,
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
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, farm, description, operatingSystemVersion, name, monitoringEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BackupServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.BackupServer dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getFarm(),
            dto.getDescription(),
            dto.getOperatingSystemVersion(),
            dto.getName(),
            dto.isMonitoringEnabled()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.BackupServer getDto() {
        return new com.aoindustries.aoserv.client.dto.BackupServer(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            farm,
            description,
            operatingSystemVersion,
            name,
            monitoringEnabled
        );
    }
    // </editor-fold>
}
