package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    int
        key_column,
        foreign_column
    ;
    boolean is_bridge;
    private int tied_bridge;
    private String since_version;
    private String last_version;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case 1: return Integer.valueOf(key_column);
                case 2: return Integer.valueOf(foreign_column);
                case 3: return is_bridge?Boolean.TRUE:Boolean.FALSE;
                case 4: return tied_bridge==-1?null:Integer.valueOf(tied_bridge);
                case 5: return since_version;
                case 6: return last_version;
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

    protected int getTableIDImpl() {
        return SchemaTable.SCHEMA_FOREIGN_KEYS;
    }

    public SchemaForeignKey getTiedBridge(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaForeignKey.class, "getTiesBridge(AOServConnector)", null);
        try {
            if(tied_bridge==-1) return null;
            SchemaForeignKey obj=connector.schemaForeignKeys.get(tied_bridge);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find SchemaForeignKey: "+tied_bridge));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
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
            is_bridge = result.getBoolean(4);
            tied_bridge = result.getInt(5);
            if(result.wasNull()) tied_bridge=-1;
            since_version=result.getString(6);
            last_version=result.getString(7);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isBridge() {
        return is_bridge;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaForeignKey.class, "read(CompressedDataInputStream)", null);
        try {
            pkey = in.readCompressedInt();
            key_column = in.readCompressedInt();
            foreign_column = in.readCompressedInt();
            is_bridge = in.readBoolean();
            tied_bridge = in.readCompressedInt();
            since_version=in.readUTF();
            last_version=readNullUTF(in);
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
            out.writeBoolean(is_bridge);
            out.writeCompressedInt(tied_bridge);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) writeNullUTF(out, last_version);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}