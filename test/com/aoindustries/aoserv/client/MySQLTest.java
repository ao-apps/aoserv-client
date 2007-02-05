package com.aoindustries.aoserv.client;
/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

import com.aoindustries.util.StandardErrorHandler;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
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
 * TODO: Test UJIS (japanese) character set (SAKURA)
 * TODO: Test BDB tables (DXS)
 *
 * @author  AO Industries, Inc.
 */
public class MySQLTest extends TestCase {
    
    private AOServConnector conn;
    private Package pack;
    private Username username;
    private MySQLUser mysqlUser;
    private List<MySQLServerUser> mysqlServerUsers=new ArrayList<MySQLServerUser>();
    private Map<MySQLServerUser,String> mysqlServerUserPasswords=new HashMap<MySQLServerUser,String>();
    private List<MySQLDatabase> mysqlDatabases=new ArrayList<MySQLDatabase>();
    private Map<MySQLDatabase,MySQLBackup> mysqlBackups=new HashMap<MySQLDatabase,MySQLBackup>();
    private Map<MySQLDatabase,Integer> dumpSizes=new HashMap<MySQLDatabase,Integer>();

    public MySQLTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        for(MySQLBackup mb : mysqlBackups.values()) mb.remove();
        for(MySQLDatabase md : mysqlDatabases) md.remove();
        for(MySQLServerUser msu : mysqlServerUsers) msu.remove();
        if(mysqlUser!=null) mysqlUser.remove();
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
    public void testMySQL() throws SQLException {
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
        backupMySQLDatabases();
        getMySQLBackups();
    }

    /**
     * Test adding a new mysql user to each of the mysql servers.
     */
    private void addMySQLServerUsers() {
        System.out.println("Testing adding MySQLUser to each MySQLServer");
        System.out.print("    Resolving TEST Package: ");
        pack=conn.packages.get("TEST");
        assertNotNull("Unable to find Package: TEST", pack);
        System.out.println("Done");

        System.out.print("    Generating random username: ");
        Random random=conn.getRandom();
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
            if(conn.usernames.isUsernameAvailable(temp)) randomUsername=temp;
        }
        System.out.println(randomUsername);
        
        System.out.print("    Adding Username: ");
        pack.addUsername(randomUsername);
        username=conn.usernames.get(randomUsername);
        assertNotNull("Username", username);
        System.out.println("Done");
        
        System.out.print("    Adding MySQLUser: ");
        username.addMySQLUser();
        mysqlUser=username.getMySQLUser();
        assertNotNull("MySQLUser", mysqlUser);
        System.out.println("Done");
        
        System.out.println("    Adding MySQLServerUsers:");
        for(MySQLServer mysqlServer : conn.mysqlServers) {
            System.out.print("        "+mysqlServer+": ");
            int pkey=mysqlUser.addMySQLServerUser(mysqlServer, MySQLServerUser.ANY_HOST);
            MySQLServerUser msu=conn.mysqlServerUsers.get(pkey);
            assertNotNull("MySQLServerUser", msu);
            mysqlServerUsers.add(msu);
            mysqlServer.getAOServer().waitForMySQLUserRebuild();
            System.out.println("Done");
        }
    }
    
    /**
     * Tests the password setting and related functions
     */
    private void setMySQLServerUserPasswords() {
        System.out.print("Testing MySQLUser.arePasswordsSet for NONE: ");
        assertEquals(mysqlUser.arePasswordsSet(), PasswordProtected.NONE);
        System.out.println("Done");

        for(int c=0;c<mysqlServerUsers.size();c++) {
            MySQLServerUser msu = mysqlServerUsers.get(c);
            System.out.print("Testing MySQLServerUser.arePasswordsSet for NONE: ");
            assertEquals(msu.arePasswordsSet(), PasswordProtected.NONE);
            System.out.println("Done");

            System.out.print("Testing MySQLServerUser.setPassword for "+msu+": ");
            String password=conn.linuxAccounts.generatePassword();
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
    private void addMySQLDatabases() {
        System.out.println("Testing adding MySQLDatabase to each MySQLServer");

        Random random=conn.getRandom();
        for(MySQLServer mysqlServer : conn.mysqlServers) {
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
            MySQLDatabase mysqlDatabase=conn.mysqlDatabases.get(pkey);
            assertNotNull("MySQLDatabase", mysqlDatabase);
            mysqlServer.getAOServer().waitForMySQLDatabaseRebuild();
            System.out.println("Done");
            mysqlDatabases.add(mysqlDatabase);
        }
    }

    /**
     * Gets the test MySQLServerUser for the provided MySQLServer.
     */
    private MySQLServerUser getMySQLServerUser(MySQLServer ms) {
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
    private Connection getConnection(MySQLDatabase md, MySQLServerUser msu) throws SQLException {
        try {
            assertEquals(md.getMySQLServer(), msu.getMySQLServer());
            Class.forName(md.getJdbcDriver()).newInstance();
            return DriverManager.getConnection(md.getJdbcUrl(true), msu.getMySQLUser().getUsername().getUsername(), mysqlServerUserPasswords.get(msu));
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
    private Connection getConnection(MySQLDatabase md) throws SQLException {
        return getConnection(md, getMySQLServerUser(md.getMySQLServer()));
    }

    /**
     * Test that cannot connect until MySQLDBUser added
     */
    private void doCantConnectTest() {
        System.out.print("Testing not allowed to connect to MySQLDatabase until MySQLDBUser added: ");
        for(MySQLDatabase md : mysqlDatabases) {
            System.out.print('.');
            boolean connected=false;
            try {
                Connection conn=getConnection(md);
                try {
                    //conn.createStatement().executeUpdate("create table test (test integer not null)");
                    connected=true;
                } finally {
                    conn.close();
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
                //    err.printStackTrace();
                //}
                fail("Should not be able to connect to database until MySQLDBUser added: "+md);
            }
        }
        System.out.println(" Done");
    }

    /**
     * Adds the MySQLDBUser.
     */
    private void addMySQLDBUser() {
        System.out.print("Testing addMySQLDBUser: ");
        for(MySQLDatabase md : mysqlDatabases) {
            System.out.print('.');
            MySQLServerUser msu = getMySQLServerUser(md.getMySQLServer());
            md.addMySQLServerUser(msu, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            md.getMySQLServer().getAOServer().waitForMySQLDBUserRebuild();
        }
        System.out.println(" Done");
    }

    /**
     * Test creating a test table.
     */
    private void createTestTable() throws SQLException {
        System.out.print("Creating test tables: ");
        for(MySQLDatabase md : mysqlDatabases) {
            System.out.print('.');
            Connection connection=getConnection(md);
            try {
                Statement stmt=connection.createStatement();
                stmt.executeUpdate("create table test (test integer not null)");
                Random random=conn.getRandom();
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
    private void selectCount() throws SQLException {
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
    private void disableMySQLServerUsers() throws SQLException {
        System.out.print("Disabling MySQLServerUsers: ");
        DisableLog dl=conn.disableLogs.get(pack.getBusiness().addDisableLog("Test disabling"));
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
    private void enableMySQLServerUsers() throws SQLException {
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
    private void dumpMySQLDatabases() throws SQLException {
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

    /**
     * Test backup.
     */
    private void backupMySQLDatabases() throws SQLException {
        /** TODO Disabled until new backup system completed
        System.out.print("Backing-up MySQLDatabases:");
        for(MySQLDatabase md : mysqlDatabases) {
            int pkey=md.backup();
            System.out.print(" "+pkey);
            mysqlBackups.put(md, conn.mysqlBackups.get(pkey));
        }
        System.out.println(" Done");
         */
    }

    /**
     * Gets backup data.
     */
    private void getMySQLBackups() throws SQLException {
        /** TODO Disabled until new backup system completed
        System.out.print("Testing MySQL backup retrieval: ");
        for(MySQLDatabase md : mysqlDatabases) {
            System.out.print('.');
            int expectedSize=dumpSizes.get(md);
            MySQLBackup backup=mysqlBackups.get(md);
            assertNotNull(backup);
            ByteArrayOutputStream bout=new ByteArrayOutputStream();
            PrintWriter writer=new PrintWriter(bout);
            backup.dump(writer);
            writer.flush();
            int length=bout.toByteArray().length;
            assertEquals(expectedSize, length);
        }
        System.out.println(" Done");
         */
    }
}
