/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2002-2009, 2016  AO Industries, Inc.
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
 * An expression in used in select statements and internal sorting.
 *
 * @author  AO Industries, Inc.
 */
abstract public class SQLExpression {

	abstract public String getColumnName();

	abstract public Object getValue(AOServConnector conn, AOServObject obj) throws IOException, SQLException;

	abstract public SchemaType getType();

	/**
	 * Gets all of the tables referenced by this expression.
	 */
	public void getReferencedTables(AOServConnector conn, List<SchemaTable> tables) throws IOException, SQLException {
	}
}
