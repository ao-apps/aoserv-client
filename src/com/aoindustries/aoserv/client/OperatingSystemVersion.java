package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersion extends AOServObjectIntegerKey<OperatingSystemVersion> {

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
    final private String operating_system;
    final private String version_number;
    final private String version_name;
    final private String architecture;
    final private String display;
    final private boolean is_aoserv_daemon_supported;
    final private short sort_order;

    public OperatingSystemVersion(
        OperatingSystemVersionService<?,?> service,
        int pkey,
        String operating_system,
        String version_number,
        String version_name,
        String architecture,
        String display,
        boolean is_aoserv_daemon_supported,
        short sort_order
    ) {
        super(service, pkey);
        this.operating_system = operating_system.intern();
        this.version_number = version_number;
        this.version_name = version_name;
        this.architecture = architecture.intern();
        this.display = display;
        this.is_aoserv_daemon_supported = is_aoserv_daemon_supported;
        this.sort_order = sort_order;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(OperatingSystemVersion other) {
        return compare(sort_order, other.sort_order);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="a generated, unique ID")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="operating_system", description="the name of the OS")
    public OperatingSystem getOperatingSystem() throws RemoteException {
        return getService().getConnector().getOperatingSystems().get(operating_system);
    }

    @SchemaColumn(order=2, name="version_number", description="the number of OS version")
    public String getVersionNumber() {
        return version_number;
    }

    @SchemaColumn(order=3, name="version_name", description="the name of this OS release")
    public String getVersionName() {
        return version_name;
    }

    @SchemaColumn(order=4, name="architecture", description="the name of the architecture")
    public Architecture getArchitecture(AOServConnector connector) throws RemoteException {
        Architecture ar=getService().getConnector().getArchitectures().get(architecture);
        if(ar==null) throw new RemoteException("Unable to find Architecture: "+architecture);
        return ar;
    }

    @SchemaColumn(order=5, name="display", unique=true, description="the full display name for this version")
    public String getDisplay() {
        return display;
    }

    @SchemaColumn(order=6, name="is_aoserv_daemon_supported", description="can AOServ Daemon be ran on this OS")
    public boolean isAOServDaemonSupported() {
        return is_aoserv_daemon_supported;
    }

    @SchemaColumn(order=7, name="sort_order", unique=true, description="the default sort order")
    public short getSortOrder() {
        return sort_order;
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