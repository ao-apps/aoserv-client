package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
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
    ) throws SQLException, IOException {
        this.expression=expression;
        this.keyColumn=keyColumn;
        this.keyIndex=keyColumn.getIndex();
        this.valueColumn=valueColumn;
        this.type=valueColumn.getSchemaType(conn);
        this.table=keyColumn.getSchemaTable(conn).getAOServTable(conn);
        this.valueIndex=valueColumn.getIndex();
    }

    public String getColumnName() {
        return valueColumn.column_name;
    }

    public Object getValue(AOServConnector conn, AOServObject obj) throws IOException, SQLException {
        Object keyValue=expression.getValue(conn, obj);
        if(keyValue!=null) {
            AOServObject row=table.getUniqueRow(keyIndex, keyValue);
            if(row!=null) return row.getColumn(valueIndex);
        }
        return null;
    }

    public SchemaType getType() {
        return type;
    }

    @Override
    public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) throws IOException, SQLException {
        expression.getReferencedTables(conn, tables);
        SchemaTable table=keyColumn.getSchemaTable(conn);
        if(!tables.contains(table)) tables.add(table);
    }
}