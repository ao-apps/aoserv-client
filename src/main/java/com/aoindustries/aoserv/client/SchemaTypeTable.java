/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.util.sort.ComparisonSortAlgorithm;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  SchemaType
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaTypeTable extends GlobalTableIntegerKey<SchemaType> {

	SchemaTypeTable(AOServConnector connector) {
		super(connector, SchemaType.class);
	}

	@Override
	OrderBy[] getDefaultOrderBy() {
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
	public SchemaType get(Object pkey) throws IOException, SQLException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof String) return get((String)pkey);
		else throw new IllegalArgumentException("Must be an Integer or a String");
	}

	@Override
	public SchemaType get(int num) throws IOException, SQLException {
		return getRows().get(num);
	}

	public SchemaType get(String type) throws IOException, SQLException {
		return getUniqueRow(SchemaType.COLUMN_TYPE, type);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SCHEMA_TYPES;
	}

	public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		T[] list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
		sortAlgorithm.sort(
			list,
			new SQLComparator<T>(
				connector,
				sortExpressions,
				sortOrders
			)
		);
	}

	public <K,T extends AOServObject<K,T>> void sort(
		ComparisonSortAlgorithm<? super T> sortAlgorithm,
		List<T> list,
		SQLExpression[] sortExpressions,
		boolean[] sortOrders
	) {
		sortAlgorithm.sort(
			list,
			new SQLComparator<T>(
				connector,
				sortExpressions,
				sortOrders
			)
		);
	}
}
