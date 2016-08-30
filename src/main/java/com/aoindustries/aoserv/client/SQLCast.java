/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2013, 2016  AO Industries, Inc.
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
 * Casts one result type to another.
 *
 * @author  AO Industries, Inc.
 */
final public class SQLCast extends SQLExpression {

	private SQLExpression expression;
	private SchemaType castToType;

	public SQLCast(SQLExpression expression, SchemaType castToType) {
		this.expression=expression;
		this.castToType=castToType;
	}

	@Override
	public String getColumnName() {
		return castToType.getType();
	}

	@Override
	public Object getValue(AOServConnector conn, AOServObject obj) throws IOException, SQLException {
		return expression.getType().cast(conn, expression.getValue(conn, obj), castToType);
	}

	@Override
	public SchemaType getType() {
		return castToType;
	}

	@Override
	public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) throws IOException, SQLException {
		expression.getReferencedTables(conn, tables);
	}
}
