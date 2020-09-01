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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.exception.ConfigurationException;
import com.aoindustries.security.Identifier;
import com.aoindustries.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests various aspects of the AOServConnector class.
 *
 * TODO: This test does not run without a master setup.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class AOServConnectorTODO extends TestCase {

	public static final User.Name REGULAR_USER_USERNAME;
	static {
		try {
			REGULAR_USER_USERNAME = User.Name.valueOf("testuser");
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	public static final String REGULAR_USER_PASSWORD="T3st1234";

	/**
	 * Gets the list of connectors to be used during testing.  This represents the three different
	 * filter modes.  Regular user (testuser), unrestricted master (aoweb_app), and a single server
	 * (mandriva20060_svr).
	 */
	static List<AOServConnector> getTestConnectors() throws ConfigurationException {
		try {
			List<AOServConnector> conns = new ArrayList<>();
			conns.add(AOServConnector.getConnector(User.Name.valueOf("aoweb_app"), "changeme"));
			conns.add(AOServConnector.getConnector(REGULAR_USER_USERNAME, REGULAR_USER_PASSWORD));
			conns.add(AOServConnector.getConnector(User.Name.valueOf("mandriva20060_svr"), "Ogrol3Veve5"));
			return conns;
		} catch(ValidationException e) {
			throw new ConfigurationException(e);
		}
	}

	private List<AOServConnector> conns;

	public AOServConnectorTODO(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		conns = getTestConnectors();
	}

	@Override
	protected void tearDown() throws Exception {
		conns = null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(AOServConnectorTODO.class);

		return suite;
	}

	/**
	 * Test of clearCaches method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testClearCaches() throws Exception {
		System.out.println("Testing clearCaches");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			for(int c=0;c<1000;c++) {
				conn.clearCaches();
			}
		}
	}

	/**
	 * Test of executeCommand method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testExecuteCommand() throws Exception {
		System.out.println("Testing executeCommand");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			assertEquals(username+"\n", conn.executeCommand(new String[] {"whoami"}));
		}
	}

	/**
	 * Test of getConnectorId method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetConnectorId() throws Exception {
		System.out.println("Testing getConnectorId");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			Identifier connectorId = conn.getConnectorId();
			for(AOServConnector conn2 : conns) {
				Identifier connectorId2 = conn2.getConnectorId();
				if(conn==conn2) {
					// Must have same connector ID
					assertEquals(connectorId, connectorId2);
				} else {
					// Must have different connector ID
					assertFalse(connectorId.equals(connectorId2));
				}
			}
		}
	}

	/**
	 * Test of getHostname method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetHostname() throws Exception {
		System.out.println("Testing getHostname");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			assertEquals("192.168.1.129", conn.getHostname());
		}
	}

	/**
	 * Test of getPort method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetPort() throws Exception {
		System.out.println("Testing getPort");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			assertEquals(4582, conn.getPort());
		}
	}

	/**
	 * Test of getProtocol method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetProtocol() throws Exception {
		System.out.println("Testing getProtocol");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			assertEquals(TCPConnector.TCP_PROTOCOL, conn.getProtocol());
		}
	}

	private void doTestRandom(Random random) throws Exception {
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			final int NUM_BYTES=1000000;
			final int CHECKPOINTS=100000;
			final int MAX_DEVIATION_PERCENT=10;
			final int MINIMUM=NUM_BYTES/256*(100-MAX_DEVIATION_PERCENT)/100;
			final int MAXIMUM=NUM_BYTES/256*(100+MAX_DEVIATION_PERCENT)/100;
			assertNotNull(random);
			int[] counts=new int[256];
			byte[] byteArray=new byte[1];
			for(int c=0;c<NUM_BYTES;) {
				random.nextBytes(byteArray);
				byte randByte=byteArray[0];
				counts[((int)randByte)&255]++;
				c++;
				if((c%CHECKPOINTS)==0) System.out.println("        Completed "+c+" of "+NUM_BYTES);
			}
			System.out.print("        Analyzing distribution for more than "+MAX_DEVIATION_PERCENT+"% devation: ");
			for(int c=0;c<256;c++) {
				int count=counts[c];
				if(count<MINIMUM || count>MAXIMUM) fail("Random distribution deviation greater than "+MAX_DEVIATION_PERCENT+"% for value "+c+".  Acceptable range "+MINIMUM+" to "+MAXIMUM+", got "+count);
			}
			System.out.println("        OK");
		}
	}

	/**
	 * Test of getSecureRandom method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetSecureRandom() throws Exception {
		System.out.println("Testing getSecureRandom");
		doTestRandom(AOServConnector.getSecureRandom());
	}

	/**
	 * Test of getFastRandom method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetFastRandom() throws Exception {
		System.out.println("Testing getFastRandom");
		doTestRandom(AOServConnector.getFastRandom());
	}

	/**
	 * Test of isSecure method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testIsSecure() throws Exception {
		System.out.println("Testing isSecure");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			assertTrue(conn.isSecure());
		}
	}

	/**
	 * Test of ping method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testPing() throws Exception {
		System.out.print("Testing ping: ");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			int totalTime=0;
			for(int c=0;c<50;c++) {
				int latency=conn.ping();
				if(latency>5000) fail("ping latency > 5000ms: "+latency);
				totalTime+=latency;
				System.out.print('.');
			}
			System.out.println("        Average: "+(totalTime/50)+"ms");
		}
	}

	/**
	 * Test of getConnection method, of class com.aoindustries.aoserv.client.AOServConnector.
	 */
	public void testGetConnection() throws Exception {
		System.out.println("Testing getConnection and releaseConnection");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			for(int c=0;c<1000;c++) {
				AOServConnection connection=conn.getConnection(1);
				conn.releaseConnection(connection);
			}
		}
	}

	/**
	 * Test the ability to get each table from the connector by table ID.  Also makes sure each table is a unique instance.
	 */
	@SuppressWarnings("rawtypes")
	public void testGetTable() throws Exception {
		System.out.println("Testing getTable and getTables");
		for(AOServConnector conn : conns) {
			User.Name username = conn.getCurrentAdministrator().getKey();
			System.out.println("    "+username);
			int numTables = Table.TableID.values().length;
			AOServTable[] tables=new AOServTable[numTables];
			for(int c=0;c<numTables;c++) {
				AOServTable table=tables[c]=conn.getTable(c);
				// Make sure index matches table ID
				// AOServClient version 1.30 had a bug where two tables were swapped
				if(
					AoservProtocol.Version.CURRENT_VERSION==AoservProtocol.Version.VERSION_1_30
					&& (
						c==Table.TableID.AOSERV_PERMISSIONS.ordinal()
						|| c==Table.TableID.AOSERV_PROTOCOLS.ordinal()
					)
				) {
					System.out.println("        Skipping version 1.30 bug where aoserv_protocols and aoserv_permissions were swapped in AOServConnector table array");
				} else {
					assertEquals("AOServConnector.tables["+c+"] and AOServTable("+table.getClass().getName()+").getTableID()="+table.getTableID(), table.getTableID().ordinal(), c);
				}
				if(c>0) {
					// Make sure not a duplicate
					for(int d=0;d<c;d++) {
						assertNotSame(table, tables[d]);
					}
				}
			}
			List<? extends AOServTable<?,?>> allTables = conn.getTables();
			assertEquals(tables.length, allTables.size());
			for(int c=0;c<numTables;c++) {
				assertSame(tables[c], allTables.get(c));
			}
		}
	}
}
