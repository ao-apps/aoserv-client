package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Meta-data for every field of every <code>AOServObject</code> is available as
 * a <code>SchemaColumn</code>.   This allows <code>AOServObject</code>s to be
 * treated in a uniform manner, while still accessing all of their attributes.
 *
 * @see  SchemaTable
 * @see  AOServObject
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaColumn extends GlobalObjectIntegerKey<SchemaColumn> {

    static final int COLUMN_PKEY=0;

    String table_name;
    String column_name;
    private int index;
    private String type;
    private boolean
        is_nullable,
        is_unique,
        is_public
    ;
    private String description;
    private String since_version;
    private String last_version;

    public SchemaColumn() {
    }

    public SchemaColumn(
        int pkey,
        String table_name,
        String column_name,
        int index,
        String type,
        boolean is_nullable,
        boolean is_unqiue,
        boolean is_public,
        String description,
        String since_version,
        String last_version
    ) {
        this.pkey=pkey;
        this.table_name=table_name;
        this.column_name=column_name;
        this.index=index;
        this.type=type;
        this.is_nullable=is_nullable;
        this.is_unique=is_unique;
        this.is_public=is_public;
        this.description=description;
        this.since_version=since_version;
        this.last_version=last_version;
    }

    public String getColumnName() {
        return column_name;
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, SchemaColumn.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case 1: return table_name;
                case 2: return column_name;
                case 3: return Integer.valueOf(index);
                case 4: return type;
                case 5: return is_nullable?Boolean.TRUE:Boolean.FALSE;
                case 6: return is_unique?Boolean.TRUE:Boolean.FALSE;
                case 7: return is_public?Boolean.TRUE:Boolean.FALSE;
                case 8: return description;
                case 9: return since_version;
                case 10: return last_version;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getDescription() {
        return description;
    }

    public String getSinceVersion() {
        return since_version;
    }
    
    public String getLastVersion() {
        return last_version;
    }

    public int getIndex() {
        return index;
    }

    public List<SchemaForeignKey> getReferencedBy(AOServConnector connector) {
        return connector.schemaForeignKeys.getSchemaForeignKeysReferencing(this);
    }

    public List<SchemaForeignKey> getReferences(AOServConnector connector) {
        return connector.schemaForeignKeys.getSchemaForeignKeysReferencedBy(this);
    }

    public SchemaTable getSchemaTable(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaColumn.class, "getSchemaTable(AOServConnector)", null);
        try {
            SchemaTable obj=connector.schemaTables.get(table_name);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find SchemaTable: "+table_name));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getSchemaTableName() {
        return table_name;
    }

    public SchemaType getSchemaType(AOServConnector connector) {
        Profiler.startProfile(Profiler.FAST, SchemaColumn.class, "getSchemaType(AOServConnector)", null);
        try {
            SchemaType obj=connector.schemaTypes.get(type);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find SchemaType: "+type));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getSchemaTypeName() {
        return type;
    }

    protected int getTableIDImpl() {
        return SchemaTable.SCHEMA_COLUMNS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, SchemaColumn.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getInt(1);
            table_name=result.getString(2);
            column_name=result.getString(3);
            index=result.getInt(4);
            type=result.getString(5);
            is_nullable=result.getBoolean(6);
            is_unique=result.getBoolean(7);
            is_public=result.getBoolean(8);
            description=result.getString(9);
            since_version=result.getString(10);
            last_version=result.getString(11);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean isNullable() {
        return is_nullable;
    }

    public boolean isPublic() {
        return is_public;
    }

    public boolean isUnique() {
        return is_unique;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaColumn.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            table_name=in.readUTF();
            column_name=in.readUTF();
            index=in.readCompressedInt();
            type=in.readUTF();
            is_nullable=in.readBoolean();
            is_unique=in.readBoolean();
            is_public=in.readBoolean();
            description=in.readUTF();
            since_version=in.readUTF();
            last_version=readNullUTF(in);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        return table_name+'.'+column_name;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, SchemaColumn.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(table_name);
            out.writeUTF(column_name);
            out.writeCompressedInt(index);
            out.writeUTF(type);
            out.writeBoolean(is_nullable);
            out.writeBoolean(is_unique);
            out.writeBoolean(is_public);
            out.writeUTF(description);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) writeNullUTF(out, last_version);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}