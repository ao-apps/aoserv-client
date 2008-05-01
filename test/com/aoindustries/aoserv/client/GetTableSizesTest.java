package com.aoindustries.aoserv.client;
/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
 * Gets the sizes of each table.
 *
 * @author  AO Industries, Inc.
 */
public class GetTableSizesTest extends TestCase {
    
    private List<AOServConnector> conns;

    public GetTableSizesTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        conns = AOServConnectorTest.getTestConnectors();
    }

    @Override
    protected void tearDown() throws Exception {
        conns = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GetTableSizesTest.class);
        
        return suite;
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testTableSizes() {
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
                    AOServTable table=conn.getTable(c);
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
                    // Skip master_history, master_server_profile, and master_processes because they frequently change sizes
                    if(
                        d!=SchemaTable.TableID.MASTER_HISTORY.ordinal()
                        && d!=SchemaTable.TableID.MASTER_SERVER_PROFILE.ordinal()
                        && d!=SchemaTable.TableID.MASTER_PROCESSES.ordinal()
                    ) {
                        AOServTable table=conn.getTable(d);
                        String tableName=table.getTableName();
                        assertEquals("Mismatched counts from different passes on table "+tableName+": ", counts[0][d], counts[c][d]);
                    }
                }
            }
        }
    }
}
