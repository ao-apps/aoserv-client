/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Meta-data for every field of every <code>AOServObject</code> is available as
 * a <code>SchemaColumn</code>.   This allows <code>AOServObject</code>s to be
 * treated in a uniform manner, while still accessing all of their attributes.
 *
 * @see  SchemaTable
 * @see  AOServObject
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
        boolean is_unique,
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

    Object getColumnImpl(int i) {
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

    public List<SchemaForeignKey> getReferencedBy(AOServConnector connector) throws IOException, SQLException {
        return connector.getSchemaForeignKeys().getSchemaForeignKeysReferencing(this);
    }

    public List<SchemaForeignKey> getReferences(AOServConnector connector) throws IOException, SQLException {
        return connector.getSchemaForeignKeys().getSchemaForeignKeysReferencedBy(this);
    }

    public SchemaTable getSchemaTable(AOServConnector connector) throws SQLException, IOException {
        SchemaTable obj=connector.getSchemaTables().get(table_name);
        if(obj==null) throw new SQLException("Unable to find SchemaTable: "+table_name);
        return obj;
    }

    public String getSchemaTableName() {
        return table_name;
    }

    public SchemaType getSchemaType(AOServConnector connector) throws SQLException, IOException {
        SchemaType obj=connector.getSchemaTypes().get(type);
        if(obj==null) throw new SQLException("Unable to find SchemaType: "+type);
        return obj;
    }

    public String getSchemaTypeName() {
        return type;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_COLUMNS;
    }

    public void init(ResultSet result) throws SQLException {
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
        pkey=in.readCompressedInt();
        table_name=in.readUTF().intern();
        column_name=in.readUTF().intern();
        index=in.readCompressedInt();
        type=in.readUTF().intern();
        is_nullable=in.readBoolean();
        is_unique=in.readBoolean();
        is_public=in.readBoolean();
        description=in.readUTF();
        since_version=in.readUTF().intern();
        last_version=InternUtils.intern(in.readNullUTF());
    }

    @Override
    String toStringImpl() {
        return table_name+'.'+column_name;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(table_name);
        out.writeUTF(column_name);
        out.writeCompressedInt(index);
        out.writeUTF(type);
        out.writeBoolean(is_nullable);
        out.writeBoolean(is_unique);
        out.writeBoolean(is_public);
        out.writeUTF(description);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_101)>=0) out.writeUTF(since_version);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) out.writeNullUTF(last_version);
    }
}