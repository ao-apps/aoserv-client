/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class HttpdServer {

    private int aoServerResource;
    private int number;
    private int maxBinds;
    private int linuxAccountGroup;
    private int modPhpVersion;
    private boolean useSuexec;
    private boolean isShared;
    private boolean useModPerl;
    private int timeout;

    public HttpdServer() {
    }

    public HttpdServer(
        int aoServerResource,
        int number,
        int maxBinds,
        int linuxAccountGroup,
        int modPhpVersion,
        boolean useSuexec,
        boolean isShared,
        boolean useModPerl,
        int timeout
    ) {
        this.aoServerResource = aoServerResource;
        this.number = number;
        this.maxBinds = maxBinds;
        this.linuxAccountGroup = linuxAccountGroup;
        this.modPhpVersion = modPhpVersion;
        this.useSuexec = useSuexec;
        this.isShared = isShared;
        this.useModPerl = useModPerl;
        this.timeout = timeout;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
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

    public int getModPhpVersion() {
        return modPhpVersion;
    }

    public void setModPhpVersion(int modPhpVersion) {
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
