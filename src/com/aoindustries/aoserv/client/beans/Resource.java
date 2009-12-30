/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class Resource {

    private int pkey;
    private String resourceType;
    private String accounting;
    private Date created;
    private String createdBy;
    private Integer disableLog;
    private Date lastEnabled;

    public Resource() {
    }

    public Resource(int pkey, String resourceType, String accounting, Date created, String createdBy, Integer disableLog, Date lastEnabled) {
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

    public String getAccounting() {
        return accounting;
    }

    public void setAccounting(String accounting) {
        this.accounting = accounting;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getDisableLog() {
        return disableLog;
    }

    public void setDisableLog(Integer disableLog) {
        this.disableLog = disableLog;
    }

    public Date getLastEnabled() {
        return lastEnabled;
    }

    public void setLastEnabled(Date lastEnabled) {
        this.lastEnabled = lastEnabled;
    }
}