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
 * Gets the value for one column.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SQLColumnValue extends SQLExpression {

    final private SchemaColumn column;
    final private SchemaType columnType;

    public SQLColumnValue(AOServConnector conn, SchemaColumn column) {
        Profiler.startProfile(Profiler.FAST, SQLColumnValue.class, "<init>(SchemaColumn)", null);
        try {
            if(column==null) throw new NullPointerException("column is null");
            this.column=column;
            this.columnType=column.getSchemaType(conn);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getColumnName() {
        return column.column_name;
    }

    public Object getValue(AOServConnector conn, AOServObject obj) {
        return obj.getColumn(column.getIndex());
    }

    public SchemaType getType() {
        return columnType;
    }

    public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) {
        SchemaTable table=column.getSchemaTable(conn);
        if(!tables.contains(table)) tables.add(table);
    }
}