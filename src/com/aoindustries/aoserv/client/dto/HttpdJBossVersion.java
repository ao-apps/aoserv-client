/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class HttpdJBossVersion extends AOServObject {

    private int version;
    private int tomcatVersion;
    private UnixPath templateDir;

    public HttpdJBossVersion() {
    }

    public HttpdJBossVersion(int version, int tomcatVersion, UnixPath templateDir) {
        this.version = version;
        this.tomcatVersion = tomcatVersion;
        this.templateDir = templateDir;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getTomcatVersion() {
        return tomcatVersion;
    }

    public void setTomcatVersion(int tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
    }

    public UnixPath getTemplateDir() {
        return templateDir;
    }

    public void setTemplateDir(UnixPath templateDir) {
        this.templateDir = templateDir;
    }
}
