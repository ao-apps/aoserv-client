/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersion extends AOServObjectIntegerKey implements Comparable<OperatingSystemVersion>, DtoFactory<com.aoindustries.aoserv.client.dto.OperatingSystemVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        VERSION_5="5",
        VERSION_ES_4="ES 4"
    ;
    
    public static final int
        CENTOS_5DOM0_X86_64 = 63,
        CENTOS_5DOM0_I686 = 64,
        CENTOS_5_I686_AND_X86_64 = 67,
        REDHAT_ES_4_X86_64 = 47
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String operatingSystem;
    private String versionNumber;
    private String versionName;
    private String architecture;
    private String display;
    final private boolean isAoservDaemonSupported;
    final private short sortOrder;

    public OperatingSystemVersion(
        AOServConnector connector,
        int pkey,
        String operatingSystem,
        String versionNumber,
        String versionName,
        String architecture,
        String display,
        boolean isAoservDaemonSupported,
        short sortOrder
    ) {
        super(connector, pkey);
        this.operatingSystem = operatingSystem;
        this.versionNumber = versionNumber;
        this.versionName = versionName;
        this.architecture = architecture;
        this.display = display;
        this.isAoservDaemonSupported = isAoservDaemonSupported;
        this.sortOrder = sortOrder;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        operatingSystem = intern(operatingSystem);
        versionNumber = intern(versionNumber);
        versionName = intern(versionName);
        architecture = intern(architecture);
        display = intern(display);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(OperatingSystemVersion other) {
        return AOServObjectUtils.compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique ID")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_OPERATING_SYSTEM = "operating_system";
    @SchemaColumn(order=1, name=COLUMN_OPERATING_SYSTEM, index=IndexType.INDEXED, description="the name of the OS")
    public OperatingSystem getOperatingSystem() throws RemoteException {
        return getConnector().getOperatingSystems().get(operatingSystem);
    }

    @SchemaColumn(order=2, name="version_number", description="the number of OS version")
    public String getVersionNumber() {
        return versionNumber;
    }

    @SchemaColumn(order=3, name="version_name", description="the name of this OS release")
    public String getVersionName() {
        return versionName;
    }

    static final String COLUMN_ARCHITECTURE = "architecture";
    @SchemaColumn(order=4, name=COLUMN_ARCHITECTURE, index=IndexType.INDEXED, description="the name of the architecture")
    public Architecture getArchitecture() throws RemoteException {
        return getConnector().getArchitectures().get(architecture);
    }

    @SchemaColumn(order=5, name="display", index=IndexType.UNIQUE, description="the full display name for this version")
    public String getDisplay() {
        return display;
    }

    @SchemaColumn(order=6, name="is_aoserv_daemon_supported", description="can AOServ Daemon be ran on this OS")
    public boolean isAOServDaemonSupported() {
        return isAoservDaemonSupported;
    }

    @SchemaColumn(order=7, name="sort_order", index=IndexType.UNIQUE, description="the default sort order")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.OperatingSystemVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.OperatingSystemVersion(key, operatingSystem, versionNumber, versionName, architecture, display, isAoservDaemonSupported, sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getOperatingSystem());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getArchitecture());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyVersions());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Server> getServers() throws RemoteException {
        return getConnector().getServers().filterIndexed(Server.COLUMN_OPERATING_SYSTEM_VERSION, this);
    }

    public IndexedSet<TechnologyVersion> getTechnologyVersions() throws RemoteException {
        return getConnector().getTechnologyVersions().filterIndexed(TechnologyVersion.COLUMN_OPERATING_SYSTEM_VERSION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /**
     * Gets the directory that stores websites for this operating system or <code>null</code>
     * if this OS doesn't support web sites.
     */
    /* TODO
    public String getHttpdSitesDirectory() {
        return getHttpdSitesDirectory(pkey);
    }
    */
    /**
     * Gets the directory that stores websites for this operating system or <code>null</code>
     * if this OS doesn't support web sites.
     */
    /* TODO
    public static String getHttpdSitesDirectory(int osv) {
        switch(osv) {
            case MANDRAKE_10_1_I586 :
            case MANDRIVA_2006_0_I586 :
            case REDHAT_ES_4_X86_64 :
            case CENTOS_5_I686_AND_X86_64 :
                return "/www";
            case CENTOS_5DOM0_I686 :
            case CENTOS_5DOM0_X86_64 :
                return null;
            default :
                throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
        }
    }*/

    /**
     * Gets the directory that contains the shared tomcat directories or <code>null</code>
     * if this OS doesn't support shared tomcats.
     */
    /* TODO
    public String getHttpdSharedTomcatsDirectory() {
        return getHttpdSharedTomcatsDirectory(pkey);
    }
    */
    /**
     * Gets the directory that contains the shared tomcat directories or <code>null</code>
     * if this OS doesn't support shared tomcats.
     */
    /* TODO
    public static String getHttpdSharedTomcatsDirectory(int osv) {
        switch(osv) {
            case MANDRAKE_10_1_I586 :
            case MANDRIVA_2006_0_I586 :
            case REDHAT_ES_4_X86_64 :
            case CENTOS_5_I686_AND_X86_64 :
                return "/wwwgroup";
            case CENTOS_5DOM0_I686 :
            case CENTOS_5DOM0_X86_64 :
                return null;
            default :
                throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
        }
    }
     */
    // </editor-fold>
}