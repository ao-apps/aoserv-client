package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

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

    public static final String
        //VERSION_1_4="1.4",
        //VERSION_7_2="7.2",
        //VERSION_9_2="9.2",
        VERSION_10_1="10.1",
        VERSION_2006_0="2006.0",
        VERSION_ES_4="ES 4"
    ;

    public static final String DEFAULT_OPERATING_SYSTEM_VERSION=VERSION_2006_0;

    public static final int
        //GENTOO_1_4_I686=5,
        //MANDRAKE_9_2_I586=12,
        MANDRAKE_10_1_I586=14,
        //REDHAT_7_2_I686=27
        MANDRIVA_2006_0_I586=45,
        REDHAT_ES_4_X86_64=47
    ;

    private String operating_system;
    String version_number;
    String version_name;
    String architecture;
    private String display;
    private boolean is_aoserv_daemon_supported;
    private short sort_order;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, OperatingSystemVersion.class, "getColValueImpl(int)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public OperatingSystem getOperatingSystem(AOServConnector conn) {
        Profiler.startProfile(Profiler.FAST, OperatingSystemVersion.class, "getOperatingSystem(AOServConnector)", null);
        try {
            return conn.operatingSystems.get(operating_system);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getVersionNumber() {
        return version_number;
    }

    public String getVersionName() {
        return version_name;
    }

    public Architecture getArchitecture(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, OperatingSystemVersion.class, "getArchitecture(AOServConnector)", null);
        try {
            Architecture ar=connector.architectures.get(architecture);
            if(ar==null) throw new WrappedException(new SQLException("Unable to find Architecture: "+architecture));
            return ar;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
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

    protected int getTableIDImpl() {
        return SchemaTable.OPERATING_SYSTEM_VERSIONS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, OperatingSystem.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getInt(1);
            operating_system=result.getString(2);
            version_number=result.getString(3);
            version_name=result.getString(4);
            architecture=result.getString(5);
            display=result.getString(6);
            is_aoserv_daemon_supported=result.getBoolean(7);
            sort_order=result.getShort(8);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, OperatingSystemVersion.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            operating_system=in.readUTF();
            version_number=in.readUTF();
            version_name=in.readUTF();
            architecture=in.readUTF();
            display=in.readUTF();
            is_aoserv_daemon_supported=in.readBoolean();
            sort_order=in.readShort();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, OperatingSystemVersion.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(operating_system);
            out.writeUTF(version_number);
            out.writeUTF(version_name);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) out.writeUTF(architecture);
            out.writeUTF(display);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) out.writeBoolean(is_aoserv_daemon_supported);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_3)>=0) out.writeShort(sort_order);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}