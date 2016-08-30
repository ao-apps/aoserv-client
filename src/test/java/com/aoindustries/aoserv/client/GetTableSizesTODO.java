/*
 * Copyright 2006-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Gets the sizes of each table.
 *
 * TODO: This test does not run without a master setup.
 *
 * @author  AO Industries, Inc.
 */
public class GetTableSizesTODO extends TestCase {

	private List<AOServConnector> conns;

	public GetTableSizesTODO(String testName) {
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
		TestSuite suite = new TestSuite(GetTableSizesTODO.class);

		return suite;
	}

	/**
	 * Test the size() method of each AOServTable.
	 */
	public void testTableSizes() throws Exception {
		final int PASSES=10;
		System.out.println("Testing getTable(tableID).size()");
		for(AOServConnector conn : conns) {
			String username = conn.getThisBusinessAdministrator().pkey;
			System.out.println("    "+username);
			int numTables = SchemaTable.TableID.values().length;
			int[][] counts=new int[PASSES][numTables];
			for(int d=0;d<PASSES;d++) {
				// Excluded for testing speed
				if(
					d==SchemaTable.TableID.DISTRO_FILES.ordinal()
					|| d==SchemaTable.TableID.TRANSACTIONS.ordinal()
					|| d==SchemaTable.TableID.WHOIS_HISTORY.ordinal()
				) continue;
				System.out.print("        Pass"+(d<9?"  ":" ")+(d+1)+" of "+PASSES+": ");
				for(int c=0;c<numTables;c++) {
					System.out.print('.');
					AOServTable<?,?> table=conn.getTable(c);
					String tableName=table.getTableName();
					int size=table.size();
					if(size<0) fail("Table size < 0 for table "+tableName+": "+size);
					counts[d][c]=size;
				}
				System.out.println(" Done");
			}
			// Make sure counts match
			for(int c=1;c<PASSES;c++) {
				for(int d=0;d<numTables;d++) {
					// Excluded for testing speed
					if(
						d==SchemaTable.TableID.DISTRO_FILES.ordinal()
						|| d==SchemaTable.TableID.TRANSACTIONS.ordinal()
						|| d==SchemaTable.TableID.WHOIS_HISTORY.ordinal()
					) continue;
					// Skip master_processes because they frequently change sizes
					if(
						d!=SchemaTable.TableID.MASTER_PROCESSES.ordinal()
					) {
						AOServTable<?,?> table=conn.getTable(d);
						String tableName=table.getTableName();
						assertEquals("Mismatched counts from different passes on table "+tableName+": ", counts[0][d], counts[c][d]);
					}
				}
			}
		}
	}
}
