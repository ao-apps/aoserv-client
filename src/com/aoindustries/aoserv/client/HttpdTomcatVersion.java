/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

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
final public class HttpdTomcatVersion extends AOServObjectIntegerKey implements Comparable<HttpdTomcatVersion>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdTomcatVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
    private static final long serialVersionUID = -6212104411644523461L;

    private UnixPath installDir;
    final private boolean requiresModJk;

    public HttpdTomcatVersion(AOServConnector connector, int version, UnixPath installDir, boolean requiresModJk) {
        super(connector, version);
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
    public int compareTo(HttpdTomcatVersion other) {
        try {
            return key==other.key ? 0 : getVersion().compareTo(other.getVersion());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_VERSION = getMethodColumn(HttpdTomcatVersion.class, "version");
    @DependencySingleton
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a reference to the tomcat details in the technology_versions table")
    public TechnologyVersion getVersion() throws RemoteException {
        return getConnector().getTechnologyVersions().get(key);
    }

    @SchemaColumn(order=1, description="the directory the basic install files are located in")
    public UnixPath getInstallDir() {
        return installDir;
    }

    @SchemaColumn(order=2, description="indicates that this version of Tomcat requires the use of mod_jk")
    public boolean getRequiresModJK() {
        return requiresModJk;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public HttpdTomcatVersion(AOServConnector connector, com.aoindustries.aoserv.client.dto.HttpdTomcatVersion dto) throws ValidationException {
        this(
            connector,
            dto.getVersion(),
            getUnixPath(dto.getInstallDir()),
            dto.isRequiresModJk()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdTomcatVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdTomcatVersion(key, getDto(installDir), requiresModJk);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<HttpdJBossVersion> getHttpdJBossVersions() throws RemoteException {
        return getConnector().getHttpdJBossVersions().filterIndexed(HttpdJBossVersion.COLUMN_TOMCAT_VERSION, this);
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
        String version = getVersion().getVersion();
        return version==VERSION_3_1; // OK - interned
    }

    public boolean isTomcat3_2_4() throws RemoteException {
        String version = getVersion().getVersion();
        return version==VERSION_3_2_4; // OK - interned
    }

    public boolean isTomcat4_1_X() throws RemoteException {
        String version = getVersion().getVersion();
        return version.startsWith(VERSION_4_1_PREFIX);
    }

    public boolean isTomcat5_5_X() throws RemoteException {
        String version = getVersion().getVersion();
        return version.startsWith(VERSION_5_5_PREFIX);
    }

    public boolean isTomcat6_0_X() throws RemoteException {
        String version = getVersion().getVersion();
        return version.startsWith(VERSION_6_0_PREFIX);
    }
    // </editor-fold>
}