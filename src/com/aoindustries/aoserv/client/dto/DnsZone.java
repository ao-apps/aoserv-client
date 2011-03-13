/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class DnsZone extends Resource {

    private DomainName zone;
    private String file;
    private DomainName hostmaster;
    private long serial;
    private int ttl;

    public DnsZone() {
    }

    public DnsZone(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        DomainName zone,
        String file,
        DomainName hostmaster,
        long serial,
        int ttl
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.zone = zone;
        this.file = file;
        this.hostmaster = hostmaster;
        this.serial = serial;
        this.ttl = ttl;
    }

    public DomainName getZone() {
        return zone;
    }

    public void setZone(DomainName zone) {
        this.zone = zone;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public DomainName getHostmaster() {
        return hostmaster;
    }

    public void setHostmaster(DomainName hostmaster) {
        this.hostmaster = hostmaster;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
