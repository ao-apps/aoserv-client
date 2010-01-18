/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import java.rmi.RemoteException;

/**
 * An <code>HttpdJBossVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the JBoss
 * EJB Container.  Sites configured to use JBoss are called
 * HttpdJBossSites.
 * 
 * @see  HttpdJBossSite
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossVersion extends AOServObjectIntegerKey<HttpdJBossVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.HttpdJBossVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String TECHNOLOGY_NAME="JBoss";

    public static final String
        VERSION_2_2_2="2.2.2"
    ;

    public static final String DEFAULT_VERSION=VERSION_2_2_2;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int tomcatVersion;
    final private UnixPath templateDir;

    public HttpdJBossVersion(HttpdJBossVersionService<?,?> service, int version, int tomcatVersion, UnixPath templateDir) {
        super(service, version);
        this.tomcatVersion = tomcatVersion;
        this.templateDir = templateDir.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(HttpdJBossVersion other) throws RemoteException {
        return key==other.key ? 0 : getTechnologyVersion().compareTo(other.getTechnologyVersion());
    }
    // </editor-fold>

    public HttpdTomcatVersion getHttpdTomcatVersion() throws SQLException, IOException {
        return connector.getHttpdTomcatVersions().get(tomcatVersion);
    }

    public TechnologyVersion getTechnologyVersion() throws SQLException, IOException {
        return connector.getTechnologyVersions().get(pkey);
    }

    public String getTemplateDirectory() {
        return templateDir;
    }
}
