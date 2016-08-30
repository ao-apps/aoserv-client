/*
 * Copyright 2006-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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
import java.util.logging.Logger;
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
public class MySQLTODO extends TestCase {

	private static final Logger logger = Logger.getLogger(MySQLTODO.class.getName());

	private AOServConnector conn;
	private Package pack;
	private Username username;
	private MySQLUser mysqlUser;
	private final List<MySQLServerUser> mysqlServerUsers=new ArrayList<>();
	private final Map<MySQLServerUser,String> mysqlServerUserPasswords=new HashMap<>();
	private final List<MySQLDatabase> mysqlDatabases=new ArrayList<>();
	private final Map<MySQLDatabase,Integer> dumpSizes=new HashMap<>();

	public MySQLTODO(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		conn=AOServConnector.getConnector(AOServConnectorTODO.REGULAR_USER_USERNAME, AOServConnectorTODO.REGULAR_USER_PASSWORD, logger);
	}

	@Override
	protected void tearDown() throws Exception {
		for(MySQLDatabase md : mysqlDatabases) md.remove();
		for(MySQLServerUser msu : mysqlServerUsers) msu.remove();
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
		pack=conn.getPackages().get("TEST");
		assertNotNull("Unable to find Package: TEST", pack);
		System.out.println("Done");

		System.out.print("    Generating random username: ");
		Random random=AOServConnector.getRandom();
		String randomUsername=null;
		while(randomUsername==null) {
			String temp=
				"test_"
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
				+(char)('0'+random.nextInt(10))
			;
			if(conn.getUsernames().isUsernameAvailable(temp)) randomUsername=temp;
		}
		System.out.println(randomUsername);

		System.out.print("    Adding Username: ");
		pack.addUsername(randomUsername);
		username=conn.getUsernames().get(randomUsername);
		assertNotNull("Username", username);
		System.out.println("Done");

		System.out.print("    Adding MySQLUser: ");
		username.addMySQLUser();
		mysqlUser=username.getMySQLUser();
		assertNotNull("MySQLUser", mysqlUser);
		System.out.println("Done");

		System.out.println("    Adding MySQLServerUsers:");
		for(MySQLServer mysqlServer : conn.getMysqlServers()) {
			System.out.print("        "+mysqlServer+": ");
			int pkey=mysqlUser.addMySQLServerUser(mysqlServer, MySQLServerUser.ANY_HOST);
			MySQLServerUser msu=conn.getMysqlServerUsers().get(pkey);
			assertNotNull("MySQLServerUser", msu);
			mysqlServerUsers.add(msu);
			mysqlServer.getAOServer().waitForMySQLUserRebuild();
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
			MySQLServerUser msu = mysqlServerUsers.get(c);
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

		Random random=AOServConnector.getRandom();
		for(MySQLServer mysqlServer : conn.getMysqlServers()) {
			System.out.print("    Generating random database name on "+mysqlServer+": ");
			String randomName=null;
			while(randomName==null) {
				String temp=
					"test_"
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
					+(char)('0'+random.nextInt(10))
				;
				boolean found=false;
				if(mysqlServer.isMySQLDatabaseNameAvailable(temp)) randomName=temp;
			}
			System.out.println(randomName);

			System.out.print("    Adding MySQLDatabase to "+mysqlServer+": ");
			int pkey=mysqlServer.addMySQLDatabase(randomName, pack);
			MySQLDatabase mysqlDatabase=conn.getMysqlDatabases().get(pkey);
			assertNotNull("MySQLDatabase", mysqlDatabase);
			mysqlServer.getAOServer().waitForMySQLDatabaseRebuild();
			System.out.println("Done");
			mysqlDatabases.add(mysqlDatabase);
		}
	}

	/**
	 * Gets the test MySQLServerUser for the provided MySQLServer.
	 */
	private MySQLServerUser getMySQLServerUser(MySQLServer ms) throws Exception {
		MySQLServerUser foundMSU=null;
		for(MySQLServerUser msu : mysqlServerUsers) {
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
	private Connection getConnection(MySQLDatabase md, MySQLServerUser msu) throws Exception {
		try {
			assertEquals(md.getMySQLServer(), msu.getMySQLServer());
			Class.forName(md.getJdbcDriver()).newInstance();
			return DriverManager.getConnection(md.getJdbcUrl(true), msu.getMySQLUser().getUsername().getUsername(), mysqlServerUserPasswords.get(msu));
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException err) {
			fail(err.toString());
			return null;
		}
	}

	/**
	 * Gets a new connection to the provided MySQLDatabase.
	 */
	private Connection getConnection(MySQLDatabase md) throws Exception {
		return getConnection(md, getMySQLServerUser(md.getMySQLServer()));
	}

	/**
	 * Test that cannot connect until MySQLDBUser added
	 */
	private void doCantConnectTest() throws Exception {
		System.out.print("Testing not allowed to connect to MySQLDatabase until MySQLDBUser added: ");
		for(MySQLDatabase md : mysqlDatabases) {
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
		for(MySQLDatabase md : mysqlDatabases) {
			System.out.print('.');
			MySQLServerUser msu = getMySQLServerUser(md.getMySQLServer());
			md.addMySQLServerUser(msu, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
			md.getMySQLServer().getAOServer().waitForMySQLDBUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test creating a test table.
	 */
	private void createTestTable() throws Exception {
		System.out.print("Creating test tables: ");
		for(MySQLDatabase md : mysqlDatabases) {
			System.out.print('.');
			try (Connection connection = getConnection(md)) {
				Statement stmt=connection.createStatement();
				stmt.executeUpdate("create table test (test integer not null)");
				Random random=AOServConnector.getRandom();
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
		for(MySQLDatabase md : mysqlDatabases) {
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
		DisableLog dl=conn.getDisableLogs().get(pack.getBusiness().addDisableLog("Test disabling"));
		for(MySQLServerUser msu : mysqlServerUsers) {
			System.out.print('.');
			msu.disable(dl);
			msu.getMySQLServer().getAOServer().waitForMySQLUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test enable users.
	 */
	private void enableMySQLServerUsers() throws Exception {
		System.out.print("Enabling MySQLServerUsers: ");
		for(MySQLServerUser msu : mysqlServerUsers) {
			System.out.print('.');
			msu.enable();
			msu.getMySQLServer().getAOServer().waitForMySQLUserRebuild();
		}
		System.out.println(" Done");
	}

	/**
	 * Test dump.
	 */
	private void dumpMySQLDatabases() throws Exception {
		System.out.print("Dumping MySQLDatabases:");
		for(MySQLDatabase md : mysqlDatabases) {
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