/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>Architecture</code> is a simple wrapper for the type
 * of computer architecture used in a server.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class Architecture extends GlobalObjectStringKey<Architecture> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    public static final String
        ALPHA="alpha",
        ARM="arm",
        I386="i386",
        I486="i486",
        I586="i586",
        I686="i686",
        I686_AND_X86_64="i686,x86_64",
        M68K="m68k",
        MIPS="mips",
        PPC="ppc",
        SPARC="sparc",
        X86_64="x86_64"
    ;

    public static final String DEFAULT_ARCHITECTURE=I686;

    private int bits;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return Integer.valueOf(bits);
            default: throw new IllegalArgumentException("Invalid index: "+i);
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

    public void init(ResultSet result) throws SQLException {
        pkey=result.getString(1);
        bits=result.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        bits=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) out.writeCompressedInt(bits);
    }
}