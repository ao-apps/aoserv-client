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
public class TechnologyVersion {

    private int pkey;
    private String name;
    private String version;
    private Date updated;
    private UserId owner;
    private int operatingSystemVersion;

    public TechnologyVersion() {
    }

    public TechnologyVersion(
        int pkey,
        String name,
        String version,
        Date updated,
        UserId owner,
        int operatingSystemVersion
    ) {
        this.pkey = pkey;
        this.name = name;
        this.version = version;
        this.updated = updated;
        this.owner = owner;
        this.operatingSystemVersion = operatingSystemVersion;
    }

    public int getPkey() {
        return pkey;
    }

    public void setPkey(int pkey) {
        this.pkey = pkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public UserId getOwner() {
        return owner;
    }

    public void setOwner(UserId owner) {
        this.owner = owner;
    }

    public int getOperatingSystemVersion() {
        return operatingSystemVersion;
    }

    public void setOperatingSystemVersion(int operatingSystemVersion) {
        this.operatingSystemVersion = operatingSystemVersion;
    }
}
