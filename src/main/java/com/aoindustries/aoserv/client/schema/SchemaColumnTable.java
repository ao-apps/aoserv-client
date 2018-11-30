/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  SchemaColumn
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaColumnTable extends GlobalTableIntegerKey<SchemaColumn> {

	/** Avoid repeated copies. */
	private static final int numTables = SchemaTable.TableID.values().length;
	/**
	 * The columns for tables are cached for faster lookups.
	 */
	private static final List<List<SchemaColumn>> tableColumns=new ArrayList<>(numTables);
	static {
		for(int c=0;c<numTables;c++) tableColumns.add(null);
	}

	/**
	 * The nameToColumns are cached for faster lookups.
	 */
	private static final List<Map<String,SchemaColumn>> nameToColumns=new ArrayList<>(numTables);
	static {
		for(int c=0;c<numTables;c++) nameToColumns.add(null);
	}

	public SchemaColumnTable(AOServConnector connector) {
		super(connector, SchemaColumn.class);
	}

	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return null;
	}

	@Override
	public SchemaColumn get(int pkey) throws IOException, SQLException {
		return getUniqueRow(SchemaColumn.COLUMN_ID, pkey);
	}

	SchemaColumn getSchemaColumn(SchemaTable table, String columnName) throws IOException, SQLException {
		int tableID=table.getId();
		synchronized(nameToColumns) {
			Map<String,SchemaColumn> map=nameToColumns.get(tableID);
			if(map==null || map.isEmpty()) {
				List<SchemaColumn> cols=getSchemaColumns(table);
				int len=cols.size();
				if(map==null) nameToColumns.set(tableID, map=new HashMap<>(len*4/3+1));
				for(int c=0;c<len;c++) {
					SchemaColumn col=cols.get(c);
					map.put(col.getName(), col);
				}
			}
			return map.get(columnName);
		}
	}

	SchemaColumn getSchemaColumn(SchemaTable table, int columnIndex) throws IOException, SQLException {
		return getSchemaColumns(table).get(columnIndex);
	}

	List<SchemaColumn> getSchemaColumns(SchemaTable table) throws IOException, SQLException {
		int tableID=table.getId();
		synchronized(tableColumns) {
			List<SchemaColumn> cols=tableColumns.get(tableID);
			if(cols!=null) return cols;

			String name=table.getName();
			List<SchemaColumn> cached=getRows();
			List<SchemaColumn> matches=new ArrayList<>();
			int size=cached.size();
			for(int c=0;c<size;c++) {
				SchemaColumn col=cached.get(c);
				if(col.getTable_name().equals(name)) matches.add(col);
			}
			matches=Collections.unmodifiableList(matches);
			tableColumns.set(tableID, matches);
			return matches;
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SCHEMA_COLUMNS;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			for(int c=0;c<numTables;c++) {
				synchronized(tableColumns) {
					tableColumns.set(c, null);
				}
				synchronized(nameToColumns) {
					Map<String,SchemaColumn> map=nameToColumns.get(c);
					if(map!=null) map.clear();
				}
			}
		}
	}
}
