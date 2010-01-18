/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class DnsRecord {

    private int resource;
    private int zone;
    private String domain;
    private String type;
    private Integer mxPriority;
    private InetAddress dataIpAddress;
    private DomainName dataDomainName;
    private String dataText;
    private Integer dhcpAddress;
    private Integer ttl;

    public DnsRecord() {
    }

    public DnsRecord(
        int resource,
        int zone,
        String domain,
        String type,
        Integer mxPriority,
        InetAddress dataIpAddress,
        DomainName dataDomainName,
        String dataText,
        Integer dhcpAddress,
        Integer ttl
    ) {
        this.resource = resource;
        this.zone = zone;
        this.domain = domain;
        this.type = type;
        this.mxPriority = mxPriority;
        this.dataIpAddress = dataIpAddress;
        this.dataDomainName = dataDomainName;
        this.dataText = dataText;
        this.dhcpAddress = dhcpAddress;
        this.ttl = ttl;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMxPriority() {
        return mxPriority;
    }

    public void setMxPriority(Integer mxPriority) {
        this.mxPriority = mxPriority;
    }

    public InetAddress getDataIpAddress() {
        return dataIpAddress;
    }

    public void setDataIpAddress(InetAddress dataIpAddress) {
        this.dataIpAddress = dataIpAddress;
    }

    public DomainName getDataDomainName() {
        return dataDomainName;
    }

    public void setDataDomainName(DomainName dataDomainName) {
        this.dataDomainName = dataDomainName;
    }

    public String getDataText() {
        return dataText;
    }

    public void setDataText(String dataText) {
        this.dataText = dataText;
    }

    public Integer getDhcpAddress() {
        return dhcpAddress;
    }

    public void setDhcpAddress(Integer dhcpAddress) {
        this.dhcpAddress = dhcpAddress;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }
}
