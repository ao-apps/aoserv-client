package com.aoindustries.aoserv.client;
/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.security.LoginException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests various aspects of the AOServConnector class.
 *
 * @author  AO Industries, Inc.
 */
public class AOServConnectorTest extends TestCase {

    static final String REGULAR_USER_USERNAME = "danshome";
    static final String REGULAR_USER_PASSWORD = "4TiveKluve4";

    /**
     * Gets the list of connectors to be used during testing.  This represents the three different
     * filter modes.  Regular user (testuser), unrestritected master (aoweb_app), and a single server
     * (test_svr).
     */
    static List<AOServConnector> getTestConnectors() throws IOException, RemoteException, LoginException, ValidationException {
        List<AOServConnector> conns = new ArrayList<AOServConnector>();
        // conns.add(AOServClientConfiguration.getConnector(UserId.valueOf("aoweb_app"), "changeme", true));
        conns.add(AOServClientConfiguration.getConnector(UserId.valueOf("orion"), "T3st1234"));
        conns.add(AOServClientConfiguration.getConnector(UserId.valueOf(REGULAR_USER_USERNAME), REGULAR_USER_PASSWORD));
        conns.add(AOServClientConfiguration.getConnector(UserId.valueOf("suspendo_svr"), "68OrfulSkuza"));
        return conns;
    }

    private List<AOServConnector> conns;

    public AOServConnectorTest(String testName) {
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
        TestSuite suite = new TestSuite(AOServConnectorTest.class);
        
        return suite;
    }

    /**
     * Test of clearCaches method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testClearCaches() throws Exception {
        System.out.println("Testing clearCaches");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            for(int c=0;c<1000;c++) conn.clearCaches();
        }
    }*/

    /**
     * Test of executeCommand method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testExecuteCommand() throws Exception {
        System.out.println("Testing executeCommand");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            assertEquals(username+"\n", conn.executeCommand(new String[] {"whoami"}));
        }
    }*/

    /**
     * Test of getConnectorID method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODOpublic void testGetConnectorID() throws Exception {
        System.out.println("Testing getConnectorID");
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getThisBusinessAdministrator());
            UUID connectorID=conn.getConnectorId();
            for(AOServConnector conn2 : conns) {
                UUID connectorID2 = conn2.getConnectorId();
                if(conn==conn2) {
                    // Must have same connector ID
                    assertEquals(connectorID, connectorID2);
                } else {
                    // Must have different connector ID
                    assertTrue(!connectorID.equals(connectorID2));
                }
            }
        }
    }*/

    /**
     * Test of getHostname method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testGetHostname() throws Exception {
        System.out.println("Testing getHostname");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            assertEquals("192.168.1.129", conn.getHostname());
        }
    }*/

    /**
     * Test of getPort method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testGetPort() throws Exception {
        System.out.println("Testing getPort");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            assertEquals(4582, conn.getPort());
        }
    }*/

    /**
     * Test of getProtocol method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testGetProtocol() throws Exception {
        System.out.println("Testing getProtocol");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            assertEquals(NetProtocol.TCP, conn.getProtocol());
        }
    }*/

    /**
     * Test of getRandom method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testGetRandom() throws Exception {
        System.out.println("Testing getRandom");
        Random random=AOServConnector.getRandom();
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
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
    }*/

    /**
     * Test of isSecure method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testIsSecure() throws Exception {
        System.out.println("Testing isSecure");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
            System.out.println("    "+username);
            assertTrue(conn.isSecure());
        }
    }*/

    /**
     * Test of ping method, of class com.aoindustries.aoserv.client.AOServConnector.
     */
    /* TODO
    public void testPing() throws Exception {
        System.out.print("Testing ping: ");
        for(AOServConnector conn : conns) {
            String username = conn.getThisBusinessAdministrator().pkey;
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
    }*/

    /**
     * Test the ability to get the set of tables from the connector.  Also makes sure each table is a unique instance.
     */
    public void testGetTables() throws Exception {
        System.out.println("Testing getTables");
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getThisBusinessAdministrator());
            int numTables = ServiceName.values.size();
            for(int c=0;c<numTables;c++) {
                AOServService table=conn.getServices().get(ServiceName.values.get(c));
                if(c>0) {
                    // Make sure not a duplicate
                    for(int d=0;d<c;d++) assertNotSame(table, conn.getServices().get(ServiceName.values.get(d)));
                }
            }
        }
    }
}
