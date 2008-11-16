package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        if(column==null) throw new NullPointerException("column is null");
        this.column=column;
        this.columnType=column.getSchemaType(conn);
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

    @Override
    public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) {
        SchemaTable table=column.getSchemaTable(conn);
        if(!tables.contains(table)) tables.add(table);
    }
}