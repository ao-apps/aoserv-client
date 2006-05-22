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
 * Tests the aoserv-client object indexing algorithms for accuracy.
 *
 * @author  AO Industries, Inc.
 */
public class GetIndexedRowTest extends TestCase {
    
    private AOServConnector conn;

    public GetIndexedRowTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        conn=AOServConnector.getConnector(new StandardErrorHandler());
    }

    protected void tearDown() throws Exception {
        conn=null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(GetIndexedRowTest.class);
        
        return suite;
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testGetIndexedRows() {
        System.out.println("Testing all indexed rows:");
        System.out.println("+ means supported");
        System.out.println("- means unsupported");
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
            AOServTable table=conn.getTable(c);
            String tableName=table.getTableName();
            System.out.print("    "+tableName+": ");
            List<AOServObject> rows=table.getRows();
            if(rows.isEmpty()) System.out.println("Empty table, cannot test");
            else {
                List<SchemaColumn> columns=table.getTableSchema().getSchemaColumns(conn);
                Map<Object,List<AOServObject>> expectedLists=new HashMap<Object,List<AOServObject>>();
                for(SchemaColumn column : columns) {
                    boolean supported=true;
                    String columnName=column.getColumnName();
                    try {
                        int colIndex=column.getIndex();
                        // Build our list of the expected objects by iterating through the entire list
                        expectedLists.clear();
                        for(AOServObject row : rows) {
                            Object value=row.getColumn(colIndex);
                            // null values are not indexed
                            if(value!=null) {
                                List<AOServObject> list=expectedLists.get(value);
                                if(list==null) expectedLists.put(value, list=new ArrayList<AOServObject>());
                                list.add(row);
                            }
                        }
                        // Compare to the lists using the index routines
                        for(Object value : expectedLists.keySet()) {
                            List<AOServObject> expectedList=expectedLists.get(value);
                            List<AOServObject> indexedRows=table.getIndexedRows(colIndex, value);
                            assertEquals(tableName+"."+columnName+"="+value+": Mismatch in list size: ", expectedList.size(), indexedRows.size());
                            if(!expectedList.containsAll(indexedRows)) fail(tableName+"."+columnName+"="+value+": expectedList does not contain all the rows of indexedRows");
                            if(!indexedRows.containsAll(expectedList)) fail(tableName+"."+columnName+"="+value+": indexedRows does not contain all the rows of expectedList");
                        }
                    } catch(UnsupportedOperationException err) {
                        supported=false;
                    } catch(RuntimeException err) {
                        System.out.println("RuntimeException tableName="+tableName+", columnName="+columnName);
                        throw err;
                    }
                    System.out.print(supported?'+':'-');
                }
                System.out.println();
            }
        }
    }
}
