/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class ServerFarm {

    private DomainLabel name;
    private String description;
    private AccountingCode owner;
    private boolean useRestrictedSmtpPort;

    public ServerFarm() {
    }

    public ServerFarm(DomainLabel name, String description, AccountingCode owner, boolean useRestrictedSmtpPort) {
        this.name = name;
        this.description = description;
        this.owner = owner;
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

    public AccountingCode getOwner() {
        return owner;
    }

    public void setOwner(AccountingCode owner) {
        this.owner = owner;
    }

    public boolean isUseRestrictedSmtpPort() {
        return useRestrictedSmtpPort;
    }

    public void setUseRestrictedSmtpPort(boolean useRestrictedSmtpPort) {
        this.useRestrictedSmtpPort = useRestrictedSmtpPort;
    }
}
