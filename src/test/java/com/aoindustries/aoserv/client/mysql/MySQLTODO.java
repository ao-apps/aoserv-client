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
package com.aoindustries.aoserv.client.mysql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServConnectorTODO;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.password.PasswordGenerator;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests all of the functions related to MySQL.
 *
 * TODO: This test does not run without a master setup.
 *
 * TODO: Test UJIS (japanese) character set (SAKURA)
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class MySQLTODO extends TestCase {

	private AOServConnector conn;
	private Package pack;
	private com.aoindustries.aoserv.client.account.User username;
	private User mysqlUser;
	private final List<UserServer> mysqlServerUsers=new ArrayList<>();
	private final Map<UserServer,String> mysqlServerUserPasswords=new HashMap<>();
	private final List<Database> mysqlDatabases=new ArrayList<>();
	private final Map<Database,Integer> dumpSizes=new HashMap<>();

	public MySQLTODO(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		conn = AOServConnector.getConnector(AOServConnectorTODO.REGULAR_USER_USERNAME, AOServConnectorTODO.REGULAR_USER_PASSWORD);
	}

	@Override
	protected void tearDown() throws Exception {
		for(Database md : mysqlDatabases) {
			md.remove();
		}
		for(UserServer msu : mysqlServerUsers) {
			msu.remove();
		}
		if(mysqlUser!=null) mysqlUser.remove();
		if(username!=null) username.remove();
		conn=null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(MySQLTODO.class);
		return suite;
	}

	/**
	 * Runs the full MySQL test.
	 */
	public void testMySQL() throws Exception {
		addMySQLServerUsers();
		setMySQLServerUserPasswords();
		addMySQLDatabases();
		doCantConnectTest();
		addMySQLDBUser();
		createTestTable();
		selectCount();
		disableMySQLServerUsers();
		doCantConnectTest();
		enableMySQLServerUsers();
		selectCount();
		dumpMySQLDatabases();
	}

	/**
	 * Test adding a new mysql user to each of the mysql servers.
	 */
	private void addMySQLServerUsers() throws Exception {
		System.out.println("Testing adding MySQLUser to each MySQLServer");
		System.out.print("    Resolving TEST Package: ");
		pack=conn.getBilling().getPackage().get(Account.Name.valueOf("TEST"));
		assertNotNull("Unable to find Package: TEST", pack);
		System.out.println("Done");

		System.out.print("    Generating random username: ");
		Random random=AOServConnector.getFastRandom();
		User.Name randomUsername=null;
		while(randomUsername==null) {
			User.Name temp = User.Name.valueOf(
				"test_"
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
			);
			if(conn.getAccount().getUser().isUsernameAvailable(temp)) randomUsername=temp;
		}
		System.out.println(randomUsername);

		System.out.print("    Adding Username: ");
		pack.addUsername(randomUsername);
		username=conn.getAccount().getUser().get(randomUsername);
		assertNotNull("Username", username);
		System.out.println("Done");

		System.out.print("    Adding MySQLUser: ");
		username.addMySQLUser();
		mysqlUser=username.getMySQLUser();
		assertNotNull("MySQLUser", mysqlUser);
		System.out.println("Done");

		System.out.println("    Adding MySQLServerUsers:");
		for(Server mysqlServer : conn.getMysql().getServer()) {
			System.out.print("        "+mysqlServer+": ");
			int pkey=mysqlUser.addMySQLServerUser(mysqlServer, UserServer.ANY_HOST);
			UserServer msu=conn.getMysql().getUserServer().get(pkey);
			assertNotNull("MySQLServerUser", msu);
			mysqlServerUsers.add(msu);
			mysqlServer.getLinuxServer().waitForMySQLUserRebuild();
			System.out.println("Done");
		}
	}

	/**
	 * Tests the password setting and related functions
	 */
	private void setMySQLServerUserPasswords() throws Exception {
		System.out.print("Testing MySQLUser.arePasswordsSet for NONE: ");
		assertEquals(mysqlUser.arePasswordsSet(), PasswordProtected.NONE);
		System.out.println("Done");

		for(int c=0;c<mysqlServerUsers.size();c++) {
			UserServer msu = mysqlServerUsers.get(c);
			System.out.print("Testing MySQLServerUser.arePasswordsSet for NONE: ");
			assertEquals(msu.arePasswordsSet(), PasswordProtected.NONE);
			System.out.println("Done");

			System.out.print("Testing MySQLServerUser.setPassword for "+msu+": ");
			String password=PasswordGenerator.generatePassword();
			msu.setPassword(password);
			System.out.println("Done");
			mysqlServerUserPasswords.put(msu, password);

			System.out.print("Testing MySQLServerUser.arePasswordsSet for ALL: ");
			assertEquals(msu.arePasswordsSet(), PasswordProtected.ALL);
			System.out.println(" Done");

			if(c==0 && mysqlServerUsers.size()>1) {
				System.out.print("Testing MySQLUser.arePasswordsSet for SOME: ");
				assertEquals(mysqlUser.arePasswordsSet(), PasswordProtected.SOME);
				System.out.println("Done");
			}
		}
		System.out.print("Testing MySQLUser.arePasswordsSet for ALL: ");
		assertEquals(mysqlUser.arePasswordsSet(), PasswordProtected.ALL);
		System.out.println("Done");
	}

	/**
	 * Test adding a new mysql databases to each of the mysql servers.
	 */
	private void addMySQLDatabases() throws Exception {
		System.out.println("Testing adding MySQLDatabase to each MySQLServer");

		Random random=AOServConnector.getFastRandom();
		for(Server mysqlServer : conn.getMysql().getServer()) {
			System.out.print("    Generating random database name on "+mysqlServer+": ");
			Database.Name randomName=null;
			while(randomName==null) {
				Database.Name temp=Database.Name.valueOf(
					"test_"
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
				);
				boolean found=false;
				if(mysqlServer.isMySQLDatabaseNameAvailable(temp)) randomName=temp;
			}
			System.out.println(randomName);

			System.out.print("    Adding MySQLDatabase to "+mysqlServer+": ");
			int pkey=mysqlServer.addMySQLDatabase(randomName, pack);
			Database mysqlDatabase=conn.getMysql().getDatabase().get(pkey);
			assertNotNull("MySQLDatabase", mysqlDatabase);
			mysqlServer.getLinuxServer().waitForMySQLDatabaseRebuild();
			System.out.println("Done");
			mysqlDatabases.add(mysqlDatabase);
		}
	}

	/**
	 * Gets the test MySQLServerUser for the provided MySQLServer.
	 */
	private UserServer getMySQLServerUser(Server ms) throws Exception {
		UserServer foundMSU=null;
		for(UserServer msu : mysqlServerUsers) {
			if(msu.getMySQLServer().equals(ms)) {
				foundMSU=msu;
				break;
			}
		}
		assertNotNull(foundMSU);
		return foundMSU;
	}

	/**
	 * Gets a new connection to the provided MySQLDatabase.
	 */
	private Connection getConnection(Database md, UserServer msu) throws Exception {
		try {
			assertEquals(md.getMySQLServer(), msu.getMySQLServer());
			Class.forName(md.getJdbcDriver()).getConstructor().newInstance();
			return DriverManager.getConnection(
				md.getJdbcUrl(true),
				msu.getMySQLUser().getUsername().getUsername().toString(),
				mysqlServerUserPasswords.get(msu)
			);
		} catch(ReflectiveOperationException err) {
			fail(err.toString());
			return null;
		}
	}

	/**
	 * Gets a new connection to the provided MySQLDatabase.
	 */
	private Connection getConnection(Database md) throws Exception {
		return getConnection(md, getMySQLServerUser(md.getMySQLServer()));
	}

	/**
	 * Test that cannot connect until MySQLDBUser added
	 */
	@SuppressWarnings("try")
	private void doCantConnectTest() throws Exception {
		System.out.print("Testing not allowed to connect to MySQLDatabase until MySQLDBUser added: ");
		for(Database md : mysqlDatabases) {
			System.out.print('.');
			boolean connected=false;
			try {
				try (Connection myConn = getConnection(md)) {
					//conn.createStatement().executeUpdate("create table test (test integer not null)");
					connected=true;
				}
			} catch(SQLException err) {
				String message=err.getMessage();
				if(
					message!=null
					&& (
						message.startsWith("Access denied for user")
						|| message.contains("is not allowed to connect to")
					)
				) connected=false;
				else fail("Unexpected SQLException: "+err);
			}
			if(connected) {
				//System.out.println("Should not be able to connect to database until MySQLDBUser added: "+md+" - sleeping for 30 seconds");
				//try {
				//    Thread.sleep(30000);
				//} catch(InterruptedException err) {
				//    logger...
				//}
				fail("Should not be able to connect to database until MySQLDBUser added: "+md);
			}
		}
		System.out.println(" Done");
	}

	/**
	 * Adds the MySQLDBUser.
	 */
	private void addMySQLDBUser() throws Exception {
		System.out.print("Testing addMySQLDBUser: ");
		for(Database md : mysqlDatabases) {
			System.out.print('.');
			UserServer msu = getMySQLServerUser(md.getMySQLServer());
			conn.getMysql().getDatabaseUser().addMySQLDBUser(md, msu, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
			md.getMySQLServer().getLinuxServer().waitForMySQLDBUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test creating a test table.
	 */
	private void createTestTable() throws Exception {
		System.out.print("Creating test tables: ");
		for(Database md : mysqlDatabases) {
			System.out.print('.');
			try (Connection connection = getConnection(md)) {
				Statement stmt=connection.createStatement();
				stmt.executeUpdate("create table test (test integer not null)");
				Random random=AOServConnector.getFastRandom();
				for(int c=0;c<1000;c++) {
					stmt.executeUpdate("insert into test values("+random.nextInt()+")");
				}
			}
		}
		System.out.println(" Done");
	}

	/**
	 * Test select count.
	 */
	private void selectCount() throws Exception {
		System.out.print("Testing select count(*) from test: ");
		for(Database md : mysqlDatabases) {
			System.out.print('.');
			try (Connection connection = getConnection(md)) {
				Statement stmt=connection.createStatement();
				ResultSet results=stmt.executeQuery("select count(*) from test");
				if(!results.next()) fail("no row returned");
				int count=results.getInt(1);
				assertEquals(1000, count);
			}
		}
		System.out.println(" Done");
	}

	/**
	 * Test disable users.
	 */
	private void disableMySQLServerUsers() throws Exception {
		System.out.print("Disabling MySQLServerUsers: ");
		DisableLog dl=conn.getAccount().getDisableLog().get(pack.getAccount().addDisableLog("Test disabling"));
		for(UserServer msu : mysqlServerUsers) {
			System.out.print('.');
			msu.disable(dl);
			msu.getMySQLServer().getLinuxServer().waitForMySQLUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test enable users.
	 */
	private void enableMySQLServerUsers() throws Exception {
		System.out.print("Enabling MySQLServerUsers: ");
		for(UserServer msu : mysqlServerUsers) {
			System.out.print('.');
			msu.enable();
			msu.getMySQLServer().getLinuxServer().waitForMySQLUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test dump.
	 */
	private void dumpMySQLDatabases() throws Exception {
		System.out.print("Dumping MySQLDatabases:");
		for(Database md : mysqlDatabases) {
			ByteArrayOutputStream bout=new ByteArrayOutputStream();
			PrintWriter writer=new PrintWriter(bout);
			md.dump(writer);
			writer.flush();
			int length=bout.toByteArray().length;
			if(length<1000) fail("dump too small: "+length);
			System.out.print(" "+length);
			dumpSizes.put(md, length);
		}
		System.out.println(" Done");
	}
}
