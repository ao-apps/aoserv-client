package com.aoindustries.aoserv.client;
/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests all of the functions related to MySQL.
 *
 * TODO: Test UJIS (japanese) character set (SAKURA)
 *
 * @author  AO Industries, Inc.
 */
public class MySQLTest extends TestCase {

    private static final Logger logger = Logger.getLogger(MySQLTest.class.getName());

    private AOServConnector conn;
    private Business bu;
    private Username username;
    private List<MySQLUser> mysqlUsers=new ArrayList<MySQLUser>();
    private Map<MySQLUser,String> mysqlUserPasswords=new HashMap<MySQLUser,String>();
    private List<MySQLDatabase> mysqlDatabases=new ArrayList<MySQLDatabase>();
    private Map<MySQLDatabase,Integer> dumpSizes=new HashMap<MySQLDatabase,Integer>();

    public MySQLTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(AOServConnectorTest.REGULAR_USER_USERNAME, AOServConnectorTest.REGULAR_USER_PASSWORD, logger);
    }

    @Override
    protected void tearDown() throws Exception {
        for(MySQLDatabase md : mysqlDatabases) md.remove();
        for(MySQLUser mu : mysqlUsers) mu.remove();
        if(username!=null) username.remove();
        conn=null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MySQLTest.class);
        return suite;
    }

    /**
     * Runs the full MySQL test.
     */
    public void testMySQL() throws Exception {
        addMySQLUsers();
        setMySQLUserPasswords();
        addMySQLDatabases();
        doCantConnectTest();
        addMySQLDBUser();
        createTestTable();
        selectCount();
        disableMySQLUsers();
        doCantConnectTest();
        enableMySQLUsers();
        selectCount();
        dumpMySQLDatabases();
    }

    /**
     * Test adding a new mysql user to each of the mysql servers.
     */
    private void addMySQLUsers() throws Exception {
        System.out.println("Testing adding MySQLUser to each MySQLServer");
        System.out.print("    Resolving TEST Business: ");
        bu=conn.getBusinesses().get("TEST");
        assertNotNull("Unable to find Business: TEST", bu);
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
            if(conn.getUsernames().isUsernameAvailable(temp, Locale.getDefault())) randomUsername=temp;
        }
        System.out.println(randomUsername);
        
        System.out.print("    Adding Username: ");
        bu.addUsername(randomUsername);
        username=conn.getUsernames().get(randomUsername);
        assertNotNull("Username", username);
        System.out.println("Done");
        
        System.out.println("    Adding MySQLUsers:");
        for(MySQLServer mysqlServer : conn.getMysqlServers()) {
            System.out.print("        "+mysqlServer+": ");
            int pkey=username.addMySQLUser(mysqlServer, MySQLUser.ANY_HOST);
            MySQLUser mu=conn.getMysqlUsers().get(pkey);
            assertNotNull("MySQLUser", mu);
            mysqlUsers.add(mu);
            mysqlServer.getAOServer().waitForMySQLUserRebuild();
            System.out.println("Done");
        }
    }

    /**
     * Tests the password setting and related functions
     */
    private void setMySQLUserPasswords() throws Exception {
        for(MySQLUser mu : mysqlUsers) {
            System.out.print("Testing MySQLUser.arePasswordsSet for NONE: ");
            assertEquals(mu.arePasswordsSet(), PasswordProtected.NONE);
            System.out.println("Done");

            System.out.print("Testing MySQLUser.setPassword for "+mu+": ");
            String password=LinuxAccountTable.generatePassword();
            mu.setPassword(password);
            System.out.println("Done");
            mysqlUserPasswords.put(mu, password);

            System.out.print("Testing MySQLUser.arePasswordsSet for ALL: ");
            assertEquals(mu.arePasswordsSet(), PasswordProtected.ALL);
            System.out.println(" Done");
        }
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
                if(mysqlServer.isMySQLDatabaseNameAvailable(temp)) randomName=temp;
            }
            System.out.println(randomName);

            System.out.print("    Adding MySQLDatabase to "+mysqlServer+": ");
            int pkey=mysqlServer.addMySQLDatabase(randomName, bu);
            MySQLDatabase mysqlDatabase=conn.getMysqlDatabases().get(pkey);
            assertNotNull("MySQLDatabase", mysqlDatabase);
            mysqlServer.getAOServer().waitForMySQLDatabaseRebuild();
            System.out.println("Done");
            mysqlDatabases.add(mysqlDatabase);
        }
    }

    /**
     * Gets the test MySQLUser for the provided MySQLServer.
     */
    private MySQLUser getMySQLUser(MySQLServer ms) throws Exception {
        MySQLUser foundMU=null;
        for(MySQLUser mu : mysqlUsers) {
            if(mu.getMySQLServer().equals(ms)) {
                foundMU=mu;
                break;
            }
        }
        assertNotNull(foundMU);
        return foundMU;
    }

    /**
     * Gets a new connection to the provided MySQLDatabase.
     */
    private Connection getConnection(MySQLDatabase md, MySQLUser mu) throws Exception {
        try {
            assertEquals(md.getMySQLServer(), mu.getMySQLServer());
            Class.forName(md.getJdbcDriver()).newInstance();
            return DriverManager.getConnection(md.getJdbcUrl(true), mu.getUsername().getUsername(), mysqlUserPasswords.get(mu));
        } catch(ClassNotFoundException err) {
            fail(err.toString());
            return null;
        } catch(InstantiationException err) {
            fail(err.toString());
            return null;
        } catch(IllegalAccessException err) {
            fail(err.toString());
            return null;
        }
    }

    /**
     * Gets a new connection to the provided MySQLDatabase.
     */
    private Connection getConnection(MySQLDatabase md) throws Exception {
        return getConnection(md, getMySQLUser(md.getMySQLServer()));
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
                Connection myConn=getConnection(md);
                try {
                    //conn.createStatement().executeUpdate("create table test (test integer not null)");
                    connected=true;
                } finally {
                    myConn.close();
                }
            } catch(SQLException err) {
                String message=err.getMessage();
                if(
                    message!=null
                    && (
                        message.startsWith("Access denied for user")
                        || message.indexOf("is not allowed to connect to")!=-1
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
            MySQLUser mu = getMySQLUser(md.getMySQLServer());
            md.addMySQLDBUser(mu, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
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
            Connection connection=getConnection(md);
            try {
                Statement stmt=connection.createStatement();
                stmt.executeUpdate("create table test (test integer not null)");
                Random random=AOServConnector.getRandom();
                for(int c=0;c<1000;c++) {
                    stmt.executeUpdate("insert into test values("+random.nextInt()+")");
                }
            } finally {
                connection.close();
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
            Connection connection=getConnection(md);
            try {
                Statement stmt=connection.createStatement();
                ResultSet results=stmt.executeQuery("select count(*) from test");
                if(!results.next()) fail("no row returned");
                int count=results.getInt(1);
                assertEquals(1000, count);
            } finally {
                connection.close();
            }
        }
        System.out.println(" Done");
    }

    /**
     * Test disable users.
     */
    private void disableMySQLUsers() throws Exception {
        System.out.print("Disabling MySQLUsers: ");
        DisableLog dl=conn.getDisableLogs().get(bu.addDisableLog("Test disabling"));
        for(MySQLUser mu : mysqlUsers) {
            System.out.print('.');
            mu.disable(dl);
            mu.getMySQLServer().getAOServer().waitForMySQLUserRebuild();
        }
        System.out.println(" Done");
    }

    /**
     * Test enable users.
     */
    private void enableMySQLUsers() throws Exception {
        System.out.print("Enabling MySQLUsers: ");
        for(MySQLUser mu : mysqlUsers) {
            System.out.print('.');
            mu.enable();
            mu.getMySQLServer().getAOServer().waitForMySQLUserRebuild();
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
