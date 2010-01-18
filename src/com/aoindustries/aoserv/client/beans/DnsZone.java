/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class DnsZone {

    private int resource;
    private DomainName zone;
    private String file;
    private DomainName hostmaster;
    private long serial;
    private int ttl;

    public DnsZone() {
    }

    public DnsZone(
        int resource,
        DomainName zone,
        String file,
        DomainName hostmaster,
        long serial,
        int ttl
    ) {
        this.resource = resource;
        this.zone = zone;
        this.file = file;
        this.hostmaster = hostmaster;
        this.serial = serial;
        this.ttl = ttl;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
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
