/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the accuracy of the system on all columns flagged as unique.
 *
 * TODO: This test does not run without a master setup.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class GetUniqueRowTODO extends TestCase {

	private List<AOServConnector> conns;

	public GetUniqueRowTODO(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		conns = AOServConnectorTODO.getTestConnectors();
	}

	@Override
	protected void tearDown() throws Exception {
		conns = null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(GetUniqueRowTODO.class);

		return suite;
	}

	/**
	 * Test the size() method of each AOServTable.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testGetUniqueRows() throws Exception {
		System.out.println("Testing all unique rows:");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			Map<Object, AOServObject> uniqueMap=new HashMap<>();
			int numTables = Table.TableID.values().length;
			for(int c=0;c<numTables;c++) {
				// Excluded for testing speed
				if(
					c==Table.TableID.DISTRO_FILES.ordinal()
				) continue;
				AOServTable table=conn.getTable(c);
				System.out.print("        "+table.getTableName()+": ");
				List<AOServObject> rows=new ArrayList<>();
				rows.addAll(table.getRows());
				System.out.println(rows.size()+" rows");
				System.out.println("            Shuffling rows");
				Collections.shuffle(rows);
				List<Column> columns=table.getTableSchema().getSchemaColumns(conn);
				for(Column column : columns) {
					uniqueMap.clear();
					if(column.isUnique()) {
						int index=column.getIndex();
						for(AOServObject row : rows) {
							Object uniqueValue=row.getColumn(index);
							// Multiple rows may have null values even when the column is otherwise unique
							if(uniqueValue!=null) {
								// Check that is actually unique in overall list of data
								if(uniqueMap.containsKey(uniqueValue)) fail("Column is flagged as unique but has a duplicate value.  Table="+table.getTableName()+", Column="+column.getName()+", Value="+uniqueValue);
								uniqueMap.put(uniqueValue, row);
								// Check that the object returned from the get unique row call matches the row that provides the unique value
								AOServObject fromUniqueCall=table.getUniqueRow(index, uniqueValue);
								assertEquals("Table="+table.getTableName()+", Column="+column.getName(), row, fromUniqueCall);
							} else {
								// Make sure is nullable
								if(!column.isNullable()) fail("Column returned null value but is not flagged as nullable.  Table="+table.getTableName()+", Column="+column.getName()+", Value="+uniqueValue);
							}
						}
					}
				}
			}
		}
	}
}
