/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class MajordomoVersion extends AOServObject {

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

    public Calendar getCreated() {
        return DtoUtils.getCalendar(created);
    }

    public void setCreated(Calendar created) {
        this.created = created.getTimeInMillis();
    }
}
