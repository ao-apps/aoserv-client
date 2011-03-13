/*
 * Copyright 2006-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the aoserv-client object indexing algorithms for accuracy.
 *
 * @author  AO Industries, Inc.
 */
public class GetIndexedRowTest extends TestCase {
    
    private List<AOServConnector> conns;

    public GetIndexedRowTest(String testName) {
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
        return new TestSuite(GetIndexedRowTest.class);
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testGetIndexedRows() throws Exception {
        System.out.println("Testing all indexed rows:");
        System.out.println("+ means supported");
        System.out.println("- means unsupported");
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getConnectAs());
            for(ServiceName serviceName : ServiceName.values) {
                AOServService<?,?> service=conn.getServices().get(serviceName);
                System.out.print("        "+serviceName.name()+": ");
                IndexedSet<? extends AOServObject<?>> set = service.getSet();
                if(set.isEmpty()) System.out.println("Empty table, cannot test");
                else {
                    List<? extends MethodColumn> columns = service.getTable().getColumns();
                    Map<Object,Set<AOServObject<?>>> expectedSets=new HashMap<Object,Set<AOServObject<?>>>();
                    int numColumns = columns.size();
                    for(int col=0; col<numColumns; col++) {
                        MethodColumn column = columns.get(col);
                        boolean supported = column.getIndexType()==IndexType.INDEXED;
                        if(supported) {
                            String columnName=column.getColumnName();
                            try {
                                // Build our set of the expected objects by iterating through the entire list
                                expectedSets.clear();
                                for(AOServObject<?> row : set) {
                                    Object value=row.getColumn(col);
                                    // null values are not indexed
                                    if(value!=null) {
                                        Set<AOServObject<?>> expectedSet = expectedSets.get(value);
                                        if(expectedSet==null) expectedSets.put(value, expectedSet=new HashSet<AOServObject<?>>());
                                        expectedSet.add(row);
                                    }
                                }
                                // Compare to the lists using the index routines
                                for(Object value : expectedSets.keySet()) {
                                    Set<AOServObject<?>> expectedSet=expectedSets.get(value);
                                    Set<? extends AOServObject<?>> indexedSet=service.filterIndexed(columnName, value);
                                    assertEquals(serviceName.name()+"."+columnName+"="+value+": Mismatch in list size: ", expectedSet.size(), indexedSet.size());
                                    if(!expectedSet.containsAll(indexedSet)) fail(serviceName.name()+"."+columnName+"="+value+": expectedSet does not contain all the rows of indexedSet");
                                    if(!indexedSet.containsAll(expectedSet)) fail(serviceName.name()+"."+columnName+"="+value+": indexedSet does not contain all the rows of expectedSet");
                                }
                            } catch(RuntimeException err) {
                                System.out.println("RuntimeException serviceName="+serviceName+", columnName="+columnName);
                                throw err;
                            }
                        }
                        System.out.print(supported?'+':'-');
                    }
                    System.out.println();
                }
            }
        }
    }
}
