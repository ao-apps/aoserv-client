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
public class MajordomoVersion {

    private String version;
    private long created;

    public MajordomoVersion() {
    }

    public MajordomoVersion(String version, long created) {
        this.version = version;
        this.created = created;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreated() {
        return new Date(created);
    }

    public void setCreated(Date created) {
        this.created = created.getTime();
    }
}
