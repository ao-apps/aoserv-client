/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Type
 *
 * @author  AO Industries, Inc.
 */
public final class TypeTable extends GlobalTableIntegerKey<Type> {

	TypeTable(AOServConnector connector) {
		super(connector, Type.class);
	}

	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return null;
	}

	/*
	@Override
	protected int getMaxConnectionsPerThread() {
		return 2;
	}*/

	/**
	 * Supports both Integer (num) and String (type) keys.
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Type get(Object pkey) throws IOException, SQLException {
		if(pkey == null) return null;
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof String) return get((String)pkey);
		else throw new IllegalArgumentException("Must be an Integer or a String");
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	@Override
	public Type get(int num) throws IOException, SQLException {
		return getRows().get(num);
	}

	/**
	 * @see  #get(java.lang.Object)
	 */
	public Type get(String type) throws IOException, SQLException {
		return getUniqueRow(Type.COLUMN_NAME, type);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SCHEMA_TYPES;
	}
}
