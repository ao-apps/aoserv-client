package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * One type of operating system.
 *
 * @see Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystem extends GlobalObjectStringKey<OperatingSystem> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    public static final String
        CENTOS="centos",
        DEBIAN="debian",
        GENTOO="gentoo",
        MANDRAKE="mandrake",
        MANDRIVA="mandriva",
        REDHAT="redhat",
        WINDOWS="windows"
    ;
    
    public static final String DEFAULT_OPERATING_SYSTEM=MANDRAKE;

    private String display;
    private boolean is_unix;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, OperatingSystem.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_NAME: return pkey;
                case 1: return display;
                case 2: return is_unix?Boolean.TRUE:Boolean.FALSE;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getName() {
        return pkey;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isUnix() {
        return is_unix;
    }

    public OperatingSystemVersion getOperatingSystemVersion(AOServConnector conn, String version, Architecture architecture) {
        Profiler.startProfile(Profiler.FAST, OperatingSystem.class, "getOperatingSystemVersion(AOServConnector,String,Architecture)", null);
        try {
            return conn.operatingSystemVersions.getOperatingSystemVersion(this, version, architecture);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.OPERATING_SYSTEMS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, OperatingSystem.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getString(1);
            display=result.getString(2);
            is_unix=result.getBoolean(3);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, OperatingSystem.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF().intern();
            display=in.readUTF();
            is_unix=in.readBoolean();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, OperatingSystem.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(display);
            out.writeBoolean(is_unix);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}