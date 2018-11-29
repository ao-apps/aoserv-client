/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Gets the value for one column by following its reference to another table.
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
		this.expression = expression;
		this.keyColumn = keyColumn;
		this.keyIndex = keyColumn.getIndex();
		this.valueColumn = valueColumn;
		this.type = valueColumn.getType(conn);
		this.table = keyColumn.getTable(conn).getAOServTable(conn);
		this.valueIndex=valueColumn.getIndex();
	}

	@Override
	public String getColumnName() {
		return valueColumn.getName();
	}

	@Override
	public Object getValue(AOServConnector conn, AOServObject obj) throws IOException, SQLException {
		Object keyValue=expression.getValue(conn, obj);
		if(keyValue!=null) {
			AOServObject row=table.getUniqueRow(keyIndex, keyValue);
			if(row!=null) return row.getColumn(valueIndex);
		}
		return null;
	}

	@Override
	public SchemaType getType() {
		return type;
	}

	@Override
	public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) throws IOException, SQLException {
		expression.getReferencedTables(conn, tables);
		SchemaTable t = keyColumn.getTable(conn);
		if(!tables.contains(t)) tables.add(t);
	}
}
