package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersion extends AOServObjectIntegerKey<OperatingSystemVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.OperatingSystemVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        //VERSION_1_4="1.4",
        //VERSION_7_2="7.2",
        //VERSION_9_2="9.2",
        VERSION_5="5",
        VERSION_2006_0="2006.0",
        VERSION_ES_4="ES 4"
    ;
    
    /**
     * @deprecated  Mandrake 10.1 no longer used.
     */
    @Deprecated
    public static final String VERSION_10_1="10.1";

    /**
     * @deprecated  What is this used for?
     */
    @Deprecated
    public static final String DEFAULT_OPERATING_SYSTEM_VERSION=VERSION_2006_0;

    public static final int
        CENTOS_5DOM0_X86_64 = 63,
        CENTOS_5DOM0_I686 = 64,
        CENTOS_5_I686_AND_X86_64 = 67,
        //GENTOO_1_4_I686=5,
        //MANDRAKE_9_2_I586=12,
        //REDHAT_7_2_I686=27
        MANDRIVA_2006_0_I586=45,
        REDHAT_ES_4_X86_64=47
    ;

    /**
     * @deprecated  Mandrake 10.1 no longer used.
     */
    @Deprecated
    public static final int MANDRAKE_10_1_I586=14;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String operatingSystem;
    final private String versionNumber;
    final private String versionName;
    final private String architecture;
    final private String display;
    final private boolean isAoservDaemonSupported;
    final private short sortOrder;

    public OperatingSystemVersion(
        OperatingSystemVersionService<?,?> service,
        int pkey,
        String operatingSystem,
        String versionNumber,
        String versionName,
        String architecture,
        String display,
        boolean isAoservDaemonSupported,
        short sortOrder
    ) {
        super(service, pkey);
        this.operatingSystem = operatingSystem.intern();
        this.versionNumber = versionNumber;
        this.versionName = versionName;
        this.architecture = architecture.intern();
        this.display = display;
        this.isAoservDaemonSupported = isAoservDaemonSupported;
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(OperatingSystemVersion other) {
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
        return getService().getConnector().getOperatingSystems().get(operatingSystem);
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
        Architecture ar=getService().getConnector().getArchitectures().get(architecture);
        if(ar==null) throw new RemoteException("Unable to find Architecture: "+architecture);
        return ar;
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.OperatingSystemVersion getBean() {
        return new com.aoindustries.aoserv.client.beans.OperatingSystemVersion(key, operatingSystem, versionNumber, versionName, architecture, display, isAoservDaemonSupported, sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getOperatingSystem(),
            getArchitecture()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getServers(),
            getTechnologyVersions()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<Server> getServers() throws RemoteException {
        return getService().getConnector().getServers().getIndexed(Server.COLUMN_OPERATING_SYSTEM_VERSION, this);
    }

    public Set<TechnologyVersion> getTechnologyVersions() throws RemoteException {
        return getService().getConnector().getTechnologyVersions().getIndexed(TechnologyVersion.COLUMN_OPERATING_SYSTEM_VERSION, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
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