package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Gets the value for one column by following its reference to another table.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SQLColumnJoin extends SQLExpression {

    final private SQLExpression expression;
    final private SchemaColumn keyColumn;
    final private int keyIndex;
    final private SchemaColumn valueColumn;
    final private SchemaType type;
    final private AOServTable table;
    final private int valueIndex;

    public SQLColumnJoin(
        AOServConnector conn,
        SQLExpression expression,
        SchemaColumn keyColumn,
        SchemaColumn valueColumn
    ) {
        Profiler.startProfile(Profiler.FAST, SQLColumnJoin.class, "<init>(AOServConnector,SQLExpression,SchemaColumn,SchemaColumn)", null);
        try {
            this.expression=expression;
            this.keyColumn=keyColumn;
            this.keyIndex=keyColumn.getIndex();
            this.valueColumn=valueColumn;
            this.type=valueColumn.getSchemaType(conn);
            this.table=keyColumn.getSchemaTable(conn).getAOServTable(conn);
            this.valueIndex=valueColumn.getIndex();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getColumnName() {
        return valueColumn.column_name;
    }

    public Object getValue(AOServConnector conn, AOServObject obj) {
        Profiler.startProfile(Profiler.FAST, SQLColumnJoin.class, "getValue(AOServConnector,AOServObject)", null);
        try {
            Object keyValue=expression.getValue(conn, obj);
            if(keyValue!=null) {
                AOServObject row=table.getUniqueRow(keyIndex, keyValue);
                if(row!=null) return row.getColumn(valueIndex);
            }
            return null;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaType getType() {
        return type;
    }

    public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) {
        Profiler.startProfile(Profiler.FAST, SQLColumnJoin.class, "getReferencedTables(AOServConnector,List<SchemaTable>)", null);
        try {
            expression.getReferencedTables(conn, tables);
            SchemaTable table=keyColumn.getSchemaTable(conn);
            if(!tables.contains(table)) tables.add(table);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}