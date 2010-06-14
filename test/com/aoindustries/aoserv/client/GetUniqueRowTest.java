/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the accuracy of the system on all columns flagged as unique.
 *
 * @author  AO Industries, Inc.
 */
public class GetUniqueRowTest extends TestCase {
    
    private List<AOServConnector<?,?>> conns;

    public GetUniqueRowTest(String testName) {
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
        return new TestSuite(GetUniqueRowTest.class);
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testGetUniqueRows() throws Exception {
        System.out.println("Testing all unique rows:");
        for(AOServConnector<?,?> conn : conns) {
            System.out.println("    "+conn.getConnectAs());
            Map<Object,AOServObject> uniqueMap=new HashMap<Object,AOServObject>();
            for(ServiceName serviceName : ServiceName.values) {
                // Excluded for testing speed
                // TODO: if(serviceName==ServiceName.distro_files) continue;
                // Exclude because reads are not repeatable
                // TODO: master_processes
                AOServService<?,?,?,?> service=conn.getServices().get(serviceName);
                System.out.print("        "+serviceName+": ");
                List<AOServObject<?,?>> rows=new ArrayList<AOServObject<?,?>>(service.getSet());
                System.out.println(rows.size()+" rows");
                System.out.println("            Shuffling rows");
                Collections.shuffle(rows);
                List<? extends MethodColumn> columns = service.getTable().getColumns();
                int numColumns = columns.size();
                for(int col = 0; col<numColumns; col++) {
                    MethodColumn column = columns.get(col);
                    uniqueMap.clear();
                    IndexType indexType = column.getIndexType();
                    if(indexType==IndexType.PRIMARY_KEY || indexType==IndexType.UNIQUE) {
                        for(AOServObject row : rows) {
                            Object uniqueValue=row.getColumn(col);
                            // Multiple rows may have null values even when the column is otherwise unique
                            if(uniqueValue!=null) {
                                // Check that is actually unique in overall list of data
                                if(uniqueMap.containsKey(uniqueValue)) fail("Column is flagged as unique but has a duplicate value.  Table="+serviceName+", Column="+column.getColumnName()+", Value="+uniqueValue);
                                uniqueMap.put(uniqueValue, row);
                                // Check that the object returned from the get unique row call matches the row that provides the unique value
                                AOServObject fromUniqueCall=service.filterUnique(column.getColumnName(), uniqueValue);
                                assertEquals("Table="+serviceName+", Column="+column.getColumnName(), row, fromUniqueCall);
                            } else {
                                // Make sure is nullable
                                //if(!column.isNullable()) fail("Column returned null value but is not flagged as nullable.  Table="+table.getTableName()+", Column="+column.getColumnName()+", Value="+uniqueValue);
                            }
                        }
                    }
                }
            }
        }
    }

    public void testGetLinuxAccounts() throws RemoteException {
        System.out.println("Testing AOServer.getLinuxAccounts:");
        for(AOServConnector<?,?> conn : conns) {
            System.out.println("    "+conn.getConnectAs());
            for(AOServer ao : conn.getAoServers().getSet()) {
                System.out.println("        "+ao);
                for(LinuxAccount la : new TreeSet<LinuxAccount>(ao.getLinuxAccounts())) {
                    UserId userId = la.getUsername().getUsername();
                    System.out.println("            "+userId+"->"+ao.getLinuxAccount(userId));
                }
            }
        }
    }
}
