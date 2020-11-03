/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
public class GetDefaultOrderBySQLExpressionsTODO extends TestCase {

	private List<AOServConnector> conns;

	public GetDefaultOrderBySQLExpressionsTODO(String testName) {
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
		TestSuite suite = new TestSuite(GetDefaultOrderBySQLExpressionsTODO.class);

		return suite;
	}

	/**
	 * Test the size() method of each AOServTable.
	 */
	public void testTableSizes() throws Exception {
		System.out.println("Testing getTable(tableID).getDefaultOrderBySQLExpressions()");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.print("    "+username+": ");
			int numTables = Table.TableID.values().length;
			for(int c=0;c<numTables;c++) {
				System.out.print('.');
				AOServTable<?,?> table=conn.getTable(c);
				table.getDefaultOrderBySQLExpressions();
			}
			System.out.println(" Done");
		}
	}
}
