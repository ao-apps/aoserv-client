package com.aoindustries.aoserv.client;
/*
 * Copyright 2006 by AO Industries, Inc.,
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
 * Gest the sizes of each table.
 *
 * @author  AO Industries, Inc.
 */
public class GetTableSizesTest extends TestCase {
    
    private AOServConnector conn;

    public GetTableSizesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        conn=null;
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
        int[][] counts=new int[PASSES][SchemaTable.NUM_TABLES];
        for(int d=0;d<PASSES;d++) {
            System.out.print("Pass "+(d+1)+" of "+PASSES+": ");
            for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
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
            for(int d=0;d<SchemaTable.NUM_TABLES;d++) {
                // Skip master_history and master_server_profile because they frequently change sizes
                if(d!=SchemaTable.MASTER_HISTORY && d!=SchemaTable.MASTER_SERVER_PROFILE) {
                    AOServTable table=conn.getTable(d);
                    String tableName=table.getTableName();
                    assertEquals("Mismatched counts from different passes on table "+tableName+": ", counts[0][d], counts[c][d]);
                }
            }
        }
    }
}
