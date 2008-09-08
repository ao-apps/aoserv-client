package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>SchemaForeignKey</code> represents when a column in one
 * <code>AOServTable</code> references a column in another
 * <code>AOServTable</code>.
 *
 * @see  SchemaColumn
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaForeignKey extends GlobalObjectIntegerKey<SchemaForeignKey> {

    static final int COLUMN_PKEY=0;
    static final String COLUMN_PKEY_name = "pkey";

    int
        key_column,
        foreign_column
    ;
    private String since_version;
    private String last_version;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case 1: return Integer.valueOf(key_column);
                case 2: return Integer.valueOf(foreign_column);
                case 3: return since_version;
                case 4: return last_version;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaColumn getForeignColumn(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "getForeignColumn(AOServConnector)", null);
        try {
            SchemaColumn obj=connector.schemaColumns.get(foreign_column);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find SchemaColumn: "+foreign_column));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaColumn getKeyColumn(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "getKeyColumn(AOServConnector)", null);
        try {
            SchemaColumn obj=connector.schemaColumns.get(key_column);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find SchemaColumn: "+key_column));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_FOREIGN_KEYS;
    }

    public String getSinceVersion() {
        return since_version;
    }

    public String getLastVersion() {
        return last_version;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getInt(1);
            key_column = result.getInt(2);
            foreign_column = result.getInt(3);
            since_version=result.getString(4);
            last_version=result.getString(5);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaForeignKey.class, "read(CompressedDataInputStream)", null);
        try {
            pkey = in.readCompressedInt();
            key_column = in.readCompressedInt();
            foreign_column = in.readCompressedInt();
            since_version=in.readUTF().intern();
            last_version=StringUtility.intern(in.readNullUTF());
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaForeignKey.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(key_column);
            out.writeCompressedInt(foreign_column);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
                out.writeBoolean(false); // is_bridge
                out.writeCompressedInt(-1); // tied_bridge
            }
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) out.writeNullUTF(last_version);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}