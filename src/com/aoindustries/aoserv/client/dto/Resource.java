/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class Resource {

    private int pkey;
    private String resourceType;
    private AccountingCode accounting;
    private long created;
    private UserId createdBy;
    private Integer disableLog;
    private long lastEnabled;

    public Resource() {
    }

    public Resource(int pkey, String resourceType, AccountingCode accounting, long created, UserId createdBy, Integer disableLog, long lastEnabled) {
        this.pkey = pkey;
        this.resourceType = resourceType;
        this.accounting = accounting;
        this.created = created;
        this.createdBy = createdBy;
        this.disableLog = disableLog;
        this.lastEnabled = lastEnabled;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public void setCreated(Date created) {
        this.created = created.getTime();
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserId createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }

    public Date getLastEnabled() {
        return new Date(lastEnabled);
    }

    public void setLastEnabled(Date lastEnabled) {
        this.lastEnabled = lastEnabled.getTime();
    }
}
