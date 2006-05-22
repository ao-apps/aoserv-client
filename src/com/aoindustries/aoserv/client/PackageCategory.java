package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>PackageCategory</code> represents one type of service
 *
 * @see  PackageDefinition
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageCategory extends GlobalObjectStringKey<PackageCategory> {

    static final int COLUMN_NAME=0;

    public static final String
        AOSERV="aoserv",
        BACKUP="backup",
        COLOCATION="colocation",
        DEDICATED="dedicated",
        MANAGED="managed",
        SYSADMIN="sysadmin",
        VIRTUAL="virtual"
    ;

    private String display;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, PackageCategory.class, "getColValueImpl()", null);
        try {
            switch(i) {
                case COLUMN_NAME: return pkey;
                case 1: return display;
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

    protected int getTableIDImpl() {
        return SchemaTable.PACKAGE_CATEGORIES;
    }

    void initImpl(ResultSet results) throws SQLException {
        Profiler.startProfile(Profiler.FAST, PackageCategory.class, "initImpl(ResultSet)", null);
        try {
            pkey=results.getString(1);
            display=results.getString(2);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, PackageCategory.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF();
            display=in.readUTF();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, PackageCategory.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(display);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}