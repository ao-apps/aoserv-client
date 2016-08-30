/*
 * Copyright 2006-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
	@SuppressWarnings({"unchecked"})
	public void testGetUniqueRows() throws Exception {
		System.out.println("Testing all unique rows:");
		for(AOServConnector conn : conns) {
			String username = conn.getThisBusinessAdministrator().pkey;
			System.out.println("    "+username);
			Map<Object,AOServObject> uniqueMap=new HashMap<>();
			int numTables = SchemaTable.TableID.values().length;
			for(int c=0;c<numTables;c++) {
				// Excluded for testing speed
				if(
					c==SchemaTable.TableID.DISTRO_FILES.ordinal()
				) continue;
				AOServTable table=conn.getTable(c);
				System.out.print("        "+table.getTableName()+": ");
				List<AOServObject> rows=new ArrayList<>();
				rows.addAll(table.getRows());
				System.out.println(rows.size()+" rows");
				System.out.println("            Shuffling rows");
				Collections.shuffle(rows);
				List<SchemaColumn> columns=table.getTableSchema().getSchemaColumns(conn);
				for(SchemaColumn column : columns) {
					uniqueMap.clear();
					if(column.isUnique()) {
						int index=column.getIndex();
						for(AOServObject row : rows) {
							Object uniqueValue=row.getColumn(index);
							// Multiple rows may have null values even when the column is otherwise unique
							if(uniqueValue!=null) {
								// Check that is actually unique in overall list of data
								if(uniqueMap.containsKey(uniqueValue)) fail("Column is flagged as unique but has a duplicate value.  Table="+table.getTableName()+", Column="+column.getColumnName()+", Value="+uniqueValue);
								uniqueMap.put(uniqueValue, row);
								// Check that the object returned from the get unique row call matches the row that provides the unique value
								AOServObject fromUniqueCall=table.getUniqueRow(index, uniqueValue);
								assertEquals("Table="+table.getTableName()+", Column="+column.getColumnName(), row, fromUniqueCall);
							} else {
								// Make sure is nullable
								if(!column.isNullable()) fail("Column returned null value but is not flagged as nullable.  Table="+table.getTableName()+", Column="+column.getColumnName()+", Value="+uniqueValue);
							}
						}
					}
				}
			}
		}
	}
}