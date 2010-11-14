/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class PrivateFtpServer extends AOServerResource {

    private int netBind;
    private UnixPath logfile;
    private DomainName hostname;
    private Email email;
    private int linuxAccountGroup;
    private boolean allowAnonymous;

    public PrivateFtpServer() {
    }

    public PrivateFtpServer(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        int netBind,
        UnixPath logfile,
        DomainName hostname,
        Email email,
        int linuxAccountGroup,
        boolean allowAnonymous
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.netBind = netBind;
        this.logfile = logfile;
        this.hostname = hostname;
        this.email = email;
        this.linuxAccountGroup = linuxAccountGroup;
        this.allowAnonymous = allowAnonymous;
    }

    public int getNetBind() {
        return netBind;
    }

    public void setNetBind(int netBind) {
        this.netBind = netBind;
    }

    public UnixPath getLogfile() {
        return logfile;
    }

    public void setLogfile(UnixPath logfile) {
        this.logfile = logfile;
    }

    public DomainName getHostname() {
        return hostname;
    }

    public void setHostname(DomainName hostname) {
        this.hostname = hostname;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public int getLinuxAccountGroup() {
        return linuxAccountGroup;
    }

    public void setLinuxAccountGroup(int linuxAccountGroup) {
        this.linuxAccountGroup = linuxAccountGroup;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }
}
