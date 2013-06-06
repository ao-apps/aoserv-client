package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One version of a operating system.
 *
 * @see OperatingSystem
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemVersion extends GlobalObjectIntegerKey<OperatingSystemVersion> {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_SORT_ORDER_name = "sort_order";

    public static final String
        //VERSION_1_4="1.4",
        //VERSION_7_2="7.2",
        //VERSION_9_2="9.2",
        VERSION_5="5",
        VERSION_5_DOM0="5.dom0",
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
        CENTOS_5_DOM0_X86_64 = 63,
        CENTOS_5_DOM0_I686 = 64,
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

    private String operating_system;
    String version_number;
    String version_name;
    String architecture;
    private String display;
    private boolean is_aoserv_daemon_supported;
    private short sort_order;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return operating_system;
            case 2: return version_number;
            case 3: return version_name;
            case 4: return architecture;
            case 5: return display;
            case 6: return is_aoserv_daemon_supported?Boolean.TRUE:Boolean.FALSE;
            case 7: return Short.valueOf(sort_order);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public OperatingSystem getOperatingSystem(AOServConnector conn) throws IOException, SQLException {
        return conn.getOperatingSystems().get(operating_system);
    }

    public String getVersionNumber() {
        return version_number;
    }

    public String getVersionName() {
        return version_name;
    }

    public Architecture getArchitecture(AOServConnector connector) throws SQLException, IOException {
        Architecture ar=connector.getArchitectures().get(architecture);
        if(ar==null) throw new SQLException("Unable to find Architecture: "+architecture);
        return ar;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isAOServDaemonSupported() {
        return is_aoserv_daemon_supported;
    }
    
    public short getSortOrder() {
        return sort_order;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.OPERATING_SYSTEM_VERSIONS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        operating_system=result.getString(2);
        version_number=result.getString(3);
        version_name=result.getString(4);
        architecture=result.getString(5);
        display=result.getString(6);
        is_aoserv_daemon_supported=result.getBoolean(7);
        sort_order=result.getShort(8);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        operating_system=in.readUTF().intern();
        version_number=in.readUTF();
        version_name=in.readUTF();
        architecture=in.readUTF().intern();
        display=in.readUTF();
        is_aoserv_daemon_supported=in.readBoolean();
        sort_order=in.readShort();
    }

    @Override
    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(operating_system);
        out.writeUTF(version_number);
        out.writeUTF(version_name);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) out.writeUTF(architecture);
        out.writeUTF(display);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) out.writeBoolean(is_aoserv_daemon_supported);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_3)>=0) out.writeShort(sort_order);
    }
    
    /**
     * Gets the directory that stores websites for this operating system or <code>null</code>
     * if this OS doesn't support web sites.
     */
    public String getHttpdSitesDirectory() {
        return getHttpdSitesDirectory(pkey);
    }

    /**
     * Gets the directory that stores websites for this operating system or <code>null</code>
     * if this OS doesn't support web sites.
     */
    public static String getHttpdSitesDirectory(int osv) {
        switch(osv) {
            case MANDRAKE_10_1_I586 :
            case MANDRIVA_2006_0_I586 :
            case REDHAT_ES_4_X86_64 :
            case CENTOS_5_I686_AND_X86_64 :
                return "/www";
            case CENTOS_5_DOM0_I686 :
            case CENTOS_5_DOM0_X86_64 :
                return null;
            default :
                throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
        }
    }

    /**
     * Gets the directory that contains the shared tomcat directories or <code>null</code>
     * if this OS doesn't support shared tomcats.
     */
    public String getHttpdSharedTomcatsDirectory() {
        return getHttpdSharedTomcatsDirectory(pkey);
    }

    /**
     * Gets the directory that contains the shared tomcat directories or <code>null</code>
     * if this OS doesn't support shared tomcats.
     */
    public static String getHttpdSharedTomcatsDirectory(int osv) {
        switch(osv) {
            case MANDRAKE_10_1_I586 :
            case MANDRIVA_2006_0_I586 :
            case REDHAT_ES_4_X86_64 :
            case CENTOS_5_I686_AND_X86_64 :
                return "/wwwgroup";
            case CENTOS_5_DOM0_I686 :
            case CENTOS_5_DOM0_X86_64 :
                return null;
            default :
                throw new AssertionError("Unexpected OperatingSystemVersion: "+osv);
        }
    }
}