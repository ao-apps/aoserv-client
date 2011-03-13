/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class HttpdServer extends AOServerResource {

    private int number;
    private int maxBinds;
    private int linuxAccountGroup;
    private Integer modPhpVersion;
    private boolean useSuexec;
    private boolean isShared;
    private boolean useModPerl;
    private int timeout;

    public HttpdServer() {
    }

    public HttpdServer(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        int number,
        int maxBinds,
        int linuxAccountGroup,
        Integer modPhpVersion,
        boolean useSuexec,
        boolean isShared,
        boolean useModPerl,
        int timeout
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.number = number;
        this.maxBinds = maxBinds;
        this.linuxAccountGroup = linuxAccountGroup;
        this.modPhpVersion = modPhpVersion;
        this.useSuexec = useSuexec;
        this.isShared = isShared;
        this.useModPerl = useModPerl;
        this.timeout = timeout;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMaxBinds() {
        return maxBinds;
    }

    public void setMaxBinds(int maxBinds) {
        this.maxBinds = maxBinds;
    }

    public int getLinuxAccountGroup() {
        return linuxAccountGroup;
    }

    public void setLinuxAccountGroup(int linuxAccountGroup) {
        this.linuxAccountGroup = linuxAccountGroup;
    }

    public Integer getModPhpVersion() {
        return modPhpVersion;
    }

    public void setModPhpVersion(Integer modPhpVersion) {
        this.modPhpVersion = modPhpVersion;
    }

    public boolean isUseSuexec() {
        return useSuexec;
    }

    public void setUseSuexec(boolean useSuexec) {
        this.useSuexec = useSuexec;
    }

    public boolean isIsShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }

    public boolean isUseModPerl() {
        return useModPerl;
    }

    public void setUseModPerl(boolean useModPerl) {
        this.useModPerl = useModPerl;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
