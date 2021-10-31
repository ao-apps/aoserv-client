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
import com.aoindustries.aoserv.client.schema.Table;
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
@SuppressWarnings("UseOfSystemOutOrSystemErr")
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
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			int numTables = Table.TableID.values().length;
			int[][] counts=new int[PASSES][numTables];
			for(int d=0;d<PASSES;d++) {
				// Excluded for testing speed
				if(
					d==Table.TableID.DISTRO_FILES.ordinal()
					|| d==Table.TableID.TRANSACTIONS.ordinal()
					|| d==Table.TableID.WhoisHistory.ordinal()
				) continue;
				System.out.print("        Pass"+(d<9?"  ":" ")+(d+1)+" of "+PASSES+": ");
				for(int c=0;c<numTables;c++) {
					System.out.print('.');
					AOServTable<?, ?> table=conn.getTable(c);
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
						d==Table.TableID.DISTRO_FILES.ordinal()
						|| d==Table.TableID.TRANSACTIONS.ordinal()
						|| d==Table.TableID.WhoisHistory.ordinal() // TODO: Just exclude output/error columns?
					) continue;
					// Skip master_processes because they frequently change sizes
					if(
						d!=Table.TableID.MASTER_PROCESSES.ordinal()
					) {
						AOServTable<?, ?> table=conn.getTable(d);
						String tableName=table.getTableName();
						assertEquals("Mismatched counts from different passes on table "+tableName+": ", counts[0][d], counts[c][d]);
					}
				}
			}
		}
	}
}
