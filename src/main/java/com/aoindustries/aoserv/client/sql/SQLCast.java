/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.sql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Casts one result type to another.
 *
 * @author  AO Industries, Inc.
 */
public final class SQLCast implements SQLExpression {

	private final SQLExpression expression;
	private final Type castToType;

	public SQLCast(SQLExpression expression, Type castToType) {
		this.expression=expression;
		this.castToType=castToType;
	}

	@Override
	public String toString() {
		return expression.toString() + "::" + Parser.quote(castToType.getName());
	}

	@Override
	public String getColumnName() {
		return castToType.getName();
	}

	@Override
	public Object evaluate(AOServConnector conn, AOServObject<?, ?> obj) throws IOException, SQLException {
		return expression.getType().cast(conn, expression.evaluate(conn, obj), castToType);
	}

	@Override
	public Type getType() {
		return castToType;
	}

	@Override
	public void getReferencedTables(AOServConnector conn, List<Table> tables) throws IOException, SQLException {
		expression.getReferencedTables(conn, tables);
	}
}
