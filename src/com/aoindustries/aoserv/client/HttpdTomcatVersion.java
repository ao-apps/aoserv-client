/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * An <code>HttpdTomcatVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the Jakarta
 * Tomcat servlet engine.  Multiple versions of the Tomcat servlet
 * engine are supported, but only one version may be configured within
 * each Java virtual machine.
 *
 * @see  HttpdTomcatSite
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersion extends AOServObjectIntegerKey<HttpdTomcatVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.HttpdTomcatVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String TECHNOLOGY_NAME="jakarta-tomcat";

    public static final String
        VERSION_3_1="3.1",
        VERSION_3_2_4="3.2.4",
        VERSION_4_1_PREFIX="4.1.",
        VERSION_5_5_PREFIX="5.5.",
        VERSION_6_0_PREFIX="6.0."
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private UnixPath installDir;
    final private boolean requiresModJk;

    public HttpdTomcatVersion(HttpdTomcatVersionService<?,?> service, int version, UnixPath installDir, boolean requiresModJk) {
        super(service, version);
        this.installDir = installDir;
        this.requiresModJk = requiresModJk;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        installDir = intern(installDir);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(HttpdTomcatVersion other) throws RemoteException {
        return key==other.key ? 0 : getTechnologyVersion().compareTo(other.getTechnologyVersion());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=0, name=COLUMN_VERSION, index=IndexType.PRIMARY_KEY, description="a reference to the tomcat details in the technology_versions table")
    public TechnologyVersion getTechnologyVersion() throws RemoteException {
        return getService().getConnector().getTechnologyVersions().get(key);
    }

    @SchemaColumn(order=1, name="install_dir", description="the directory the basic install files are located in")
    public UnixPath getInstallDirectory() {
        return installDir;
    }

    @SchemaColumn(order=2, name="requires_mod_jk", description="indicates that this version of Tomcat requires the use of mod_jk")
    public boolean requiresModJK() {
        return requiresModJk;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.HttpdTomcatVersion getBean() {
        return new com.aoindustries.aoserv.client.beans.HttpdTomcatVersion(key, installDir.getBean(), requiresModJk);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTechnologyVersion()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getHttpdJBossVersions()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<HttpdJBossVersion> getHttpdJBossVersions() throws RemoteException {
        return getService().getConnector().getHttpdJBossVersions().filterIndexed(HttpdJBossVersion.COLUMN_TOMCAT_VERSION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Convenient Version Checking">
    /**
     * deprecated  Please check all uses of this, because it also returns <code>true</code> for Tomcat 5, which doesn't seem
     *              to match the method name very well.
     *
     * @see  #isTomcat4_1_X()
     * @see  #isTomcat5_5_X()
     * @see  #isTomcat6_0_X()
     */
    //public boolean isTomcat4() throws SQLException, IOException {
    //    String version = getTechnologyVersion(connector).getVersion();
    //    return version.startsWith("4.") || version.startsWith("5.");
    //}

    public boolean isTomcat3_1() throws RemoteException {
        String version = getTechnologyVersion().getVersion();
        return version.equals(VERSION_3_1);
    }

    public boolean isTomcat3_2_4() throws RemoteException {
        String version = getTechnologyVersion().getVersion();
        return version.equals(VERSION_3_2_4);
    }

    public boolean isTomcat4_1_X() throws RemoteException {
        String version = getTechnologyVersion().getVersion();
        return version.startsWith(VERSION_4_1_PREFIX);
    }

    public boolean isTomcat5_5_X() throws RemoteException {
        String version = getTechnologyVersion().getVersion();
        return version.startsWith(VERSION_5_5_PREFIX);
    }

    public boolean isTomcat6_0_X() throws RemoteException {
        String version = getTechnologyVersion().getVersion();
        return version.startsWith(VERSION_6_0_PREFIX);
    }
    // </editor-fold>
}