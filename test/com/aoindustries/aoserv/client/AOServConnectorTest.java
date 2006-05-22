package com.aoindustries.aoserv.client;
/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

import junit.framework.*;
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.table.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.security.*;
import java.util.*;

/**
 * Tests various aspects of the AOServConnector class.
 *
 * @author  AO Industries, Inc.
 */
public class AOServConnectorTest extends TestCase {
    
    private AOServConnector conn;

    public AOServConnectorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        conn=null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AOServConnectorTest.class);
        
        return suite;
    }

    /**
     * Test of clearCaches method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testClearCaches() {
        System.out.println("Testing clearCaches");
        for(int c=0;c<1000;c++) conn.clearCaches();
    }

    /**
     * Test of executeCommand method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testExecuteCommand() {
        System.out.println("Testing executeCommand");
        assertEquals("aoweb_app\n", conn.executeCommand(new String[] {"whoami"}));
    }

    /**
     * Test of getConnectorID method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetConnectorID() {
        System.out.println("Testing getConnectorID");
        long connectorID=conn.getConnectorID();
        assertEquals(connectorID, conn.getConnectorID());
    }

    /**
     * Test of getHostname method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetHostname() {
        System.out.println("Testing getHostname");
        assertEquals("192.168.1.129", conn.getHostname());
    }

    /**
     * Test of getPort method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetPort() {
        System.out.println("Testing getPort");
        assertEquals(4582, conn.getPort());
    }

    /**
     * Test of getProtocol method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetProtocol() {
        System.out.println("Testing getProtocol");
        assertEquals(NetProtocol.TCP, conn.getProtocol());
    }

    /**
     * Test of getRandom method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetRandom() {
        System.out.println("Testing getRandom");
        final int NUM_BYTES=1000000;
        final int CHECKPOINTS=100000;
        final int MAX_DEVIATION_PERCENT=10;
        final int MINIMUM=NUM_BYTES/256*(100-MAX_DEVIATION_PERCENT)/100;
        final int MAXIMUM=NUM_BYTES/256*(100+MAX_DEVIATION_PERCENT)/100;
        Random random=conn.getRandom();
        assertNotNull(random);
        int[] counts=new int[256];
        byte[] byteArray=new byte[1];
        for(int c=0;c<NUM_BYTES;) {
            random.nextBytes(byteArray);
            byte randByte=byteArray[0];
            counts[((int)randByte)&255]++;
            c++;
            if((c%CHECKPOINTS)==0) System.out.println("Completed "+c+" of "+NUM_BYTES);
        }
        System.out.print("Analyzing distribution for more than "+MAX_DEVIATION_PERCENT+"% devation: ");
        for(int c=0;c<256;c++) {
            int count=counts[c];
            if(count<MINIMUM || count>MAXIMUM) fail("Random distribution deviation greater than "+MAX_DEVIATION_PERCENT+"% for value "+c+".  Acceptable range "+MINIMUM+" to "+MAXIMUM+", got "+count);
        }
        System.out.println("OK");
    }

    /**
     * Test of isSecure method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testIsSecure() {
        System.out.println("Testing isSecure");
        assertTrue(conn.isSecure());
    }

    /**
     * Test of ping method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testPing() {
        System.out.print("Testing ping: ");
        int totalTime=0;
        for(int c=0;c<50;c++) {
            int latency=conn.ping();
            if(latency>5000) fail("ping latency > 5000ms: "+latency);
            totalTime+=latency;
            System.out.print('.');
        }
        System.out.println(" Average: "+(totalTime/50)+"ms");
    }

    /**
     * Test of getConnection method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    public void testGetConnection() throws IOException {
        System.out.println("Testing getConnection and releaseConnection");
        for(int c=0;c<1000;c++) {
            AOServConnection connection=conn.getConnection(1);
            conn.releaseConnection(connection);
        }
    }
    
    /**
     * Test the ability to get each table from the connector by table ID.  Also makes sure each table is a unique instance.
     */
    public void testGetTable() {
        System.out.println("Testing getTable and getTables");
        AOServTable[] tables=new AOServTable[SchemaTable.NUM_TABLES];
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
            AOServTable table=tables[c]=conn.getTable(c);
            // Make sure index matches table ID
            assertEquals("AOServConnector.tables["+c+"] and AOServTable("+table.getClass().getName()+".getTableID()="+table.getTableID(), table.getTableID(), c);
            if(c>0) {
                // Make sure not a duplicate
                for(int d=0;d<c;d++) assertNotSame(table, tables[d]);
            }
        }
        AOServTable[] allTables=conn.getTables();
        assertEquals(tables.length, allTables.length);
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) assertSame(tables[c], allTables[c]);
    }
}
