/*
 * Copyright 2002-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Gets the value for one column.
 *
 * @author  AO Industries, Inc.
 */
final public class SQLColumnValue extends SQLExpression {

	final private SchemaColumn column;
	final private SchemaType columnType;

	public SQLColumnValue(AOServConnector conn, SchemaColumn column) throws SQLException, IOException {
		if(column==null) throw new NullPointerException("column is null");
		this.column=column;
		this.columnType=column.getSchemaType(conn);
	}

	@Override
	public String getColumnName() {
		return column.column_name;
	}

	@Override
	public Object getValue(AOServConnector conn, AOServObject obj) {
		return obj.getColumn(column.getIndex());
	}

	@Override
	public SchemaType getType() {
		return columnType;
	}

	@Override
	public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) throws SQLException, IOException {
		SchemaTable table=column.getSchemaTable(conn);
		if(!tables.contains(table)) tables.add(table);
	}
}
