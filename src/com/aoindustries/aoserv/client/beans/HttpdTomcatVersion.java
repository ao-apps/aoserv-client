/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class HttpdTomcatVersion {

    private int version;
    private UnixPath installDir;
    private boolean requiresModJk;

    public HttpdTomcatVersion() {
    }

    public HttpdTomcatVersion(int version, UnixPath installDir, boolean requiresModJk) {
        this.version = version;
        this.installDir = installDir;
        this.requiresModJk = requiresModJk;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public UnixPath getInstallDir() {
        return installDir;
    }

    public void setInstallDir(UnixPath installDir) {
        this.installDir = installDir;
    }

    public boolean isRequiresModJk() {
        return requiresModJk;
    }

    public void setRequiresModJk(boolean requiresModJk) {
        this.requiresModJk = requiresModJk;
    }
}
