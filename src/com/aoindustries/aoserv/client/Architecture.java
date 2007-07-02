package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>Architecture</code> is a simple wrapper for the type
 * of computer architecture used in a server.
 *
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Architecture extends GlobalObjectStringKey<Architecture> {

    static final int COLUMN_NAME=0;

    public static final String
        ALPHA="alpha",
        ARM="arm",
        I386="i386",
        I486="i486",
        I586="i586",
        I686="i686",
        M68K="m68k",
        MIPS="mips",
        PPC="ppc",
        SPARC="sparc",
        X86_64="x86_64"
    ;

    public static final String DEFAULT_ARCHITECTURE=I686;

    private int bits;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, Architecture.class, "getColValueImpl()", null);
        try {
            switch(i) {
                case COLUMN_NAME: return pkey;
                case 1: return Integer.valueOf(bits);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getName() {
        return pkey;
    }

    public int getBits() {
        return bits;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ARCHITECTURES;
    }

    void initImpl(ResultSet results) throws SQLException {
        pkey=results.getString(1);
        bits=results.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, Architecture.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF().intern();
            bits=in.readCompressedInt();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, Architecture.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) out.writeCompressedInt(bits);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}