/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class ServerFarm extends Resource {

    private DomainLabel name;
    private String description;
    private boolean useRestrictedSmtpPort;

    public ServerFarm() {
    }

    public ServerFarm(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        DomainLabel name,
        String description,
        boolean useRestrictedSmtpPort
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.name = name;
        this.description = description;
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }

    public DomainLabel getName() {
        return name;
    }

    public void setName(DomainLabel name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUseRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }

    public void setUseRestrictedSmtpPort(boolean useRestrictedSmtpPort) {
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }
}
