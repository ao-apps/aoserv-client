/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
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
final public class HttpdJBossVersion extends AOServObjectIntegerKey<HttpdJBossVersion> implements DtoFactory<com.aoindustries.aoserv.client.dto.HttpdJBossVersion> {

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
    private UnixPath templateDir;

    public HttpdJBossVersion(HttpdJBossVersionService<?,?> service, int version, int tomcatVersion, UnixPath templateDir) {
        super(service, version);
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
    protected int compareToImpl(HttpdJBossVersion other) throws RemoteException {
        return key==other.key ? 0 : getTechnologyVersion().compareToImpl(other.getTechnologyVersion());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=0, name=COLUMN_VERSION, index=IndexType.PRIMARY_KEY, description="jboss version designator")
    public TechnologyVersion getTechnologyVersion() throws RemoteException {
        return getService().getConnector().getTechnologyVersions().get(key);
    }

    static final String COLUMN_TOMCAT_VERSION = "tomcat_version";
    @SchemaColumn(order=1, name=COLUMN_TOMCAT_VERSION, index=IndexType.INDEXED, description="version of tomcat associated with this jboss version")
    public HttpdTomcatVersion getHttpdTomcatVersion() throws RemoteException {
        return getService().getConnector().getHttpdTomcatVersions().get(tomcatVersion);
    }

    @SchemaColumn(order=2, name="template_dir", description="directory containing the install template")
    public UnixPath getTemplateDirectory() {
        return templateDir;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.HttpdJBossVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdJBossVersion(key, tomcatVersion, getDto(templateDir));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyVersion());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdTomcatVersion());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJBossSites());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IndexedSet<HttpdJBossSite> getHttpdJBossSites() throws RemoteException {
        return getService().getConnector().getTicketCategories().filterIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}
