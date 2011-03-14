/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
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
final public class HttpdJBossVersion extends AOServObjectIntegerKey implements Comparable<HttpdJBossVersion>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdJBossVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    public static final String TECHNOLOGY_NAME="JBoss";

    public static final String
        VERSION_2_2_2="2.2.2"
    ;

    public static final String DEFAULT_VERSION=VERSION_2_2_2;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -2369128966548022532L;

    final private int tomcatVersion;
    private UnixPath templateDir;

    public HttpdJBossVersion(AOServConnector connector, int version, int tomcatVersion, UnixPath templateDir) {
        super(connector, version);
        this.tomcatVersion = tomcatVersion;
        this.templateDir = templateDir;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        templateDir = intern(templateDir);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdJBossVersion other) {
        try {
            return key==other.key ? 0 : getVersion().compareTo(other.getVersion());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_VERSION = getMethodColumn(HttpdJBossVersion.class, "version");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="jboss version designator")
    public TechnologyVersion getVersion() throws RemoteException {
        return getConnector().getTechnologyVersions().get(key);
    }

    public static final MethodColumn COLUMN_TOMCAT_VERSION = getMethodColumn(HttpdJBossVersion.class, "tomcatVersion");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="version of tomcat associated with this jboss version")
    public HttpdTomcatVersion getTomcatVersion() throws RemoteException {
        return getConnector().getHttpdTomcatVersions().get(tomcatVersion);
    }

    @SchemaColumn(order=2, description="directory containing the install template")
    public UnixPath getTemplateDir() {
        return templateDir;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public HttpdJBossVersion(AOServConnector connector, com.aoindustries.aoserv.client.dto.HttpdJBossVersion dto) throws ValidationException {
        this(
            connector,
            dto.getVersion(),
            dto.getTomcatVersion(),
            getUnixPath(dto.getTemplateDir())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdJBossVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdJBossVersion(key, tomcatVersion, getDto(templateDir));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public IndexedSet<HttpdJBossSite> getHttpdJBossSites() throws RemoteException {
        return getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}
