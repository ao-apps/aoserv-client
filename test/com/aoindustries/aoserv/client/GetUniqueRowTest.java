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
 * Tests the accuracy of the system on all columns flagged as unique.
 *
 * @author  AO Industries, Inc.
 */
public class GetUniqueRowTest extends TestCase {
    
    private AOServConnector conn;

    public GetUniqueRowTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        conn=null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GetUniqueRowTest.class);
        
        return suite;
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testGetUniqueRows() {
        System.out.println("Testing all unique rows:");
        Map<Object,AOServObject> uniqueMap=new HashMap<Object,AOServObject>();
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
            AOServTable table=conn.getTable(c);
            System.out.print("    "+table.getTableName()+": ");
            List<AOServObject> rows=new ArrayList<AOServObject>();
            rows.addAll(table.getRows());
            System.out.println(rows.size()+" rows");
            System.out.println("        Shuffling rows");
            Collections.shuffle(rows);
            List<SchemaColumn> columns=table.getTableSchema().getSchemaColumns(conn);
            for(SchemaColumn column : columns) {
                uniqueMap.clear();
                if(column.isUnique()) {
                    int index=column.getIndex();
                    for(AOServObject row : rows) {
                        Object uniqueValue=row.getColumn(index);
                        // Check that is actually unique in overall list of data
                        if(uniqueMap.containsKey(uniqueValue)) fail("Column is flagged as unique but has a duplicate value.  Table="+table.getTableName()+", Column="+column.getColumnName()+", Value="+uniqueValue);
                        uniqueMap.put(uniqueValue, row);
                        // Check that the object returned from the get unique row call matches the row that provides the unique value
                        AOServObject fromUniqueCall=table.getUniqueRow(index, uniqueValue);
                        assertEquals(row, fromUniqueCall);
                    }
                }
            }
        }
    }
}
