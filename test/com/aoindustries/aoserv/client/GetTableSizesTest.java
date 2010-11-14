/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
        return new TestSuite(GetTableSizesTest.class);
    }

    /**
     * Test the size() method of each AOServTable.
     */
    public void testTableSizes() throws Exception {
        final int PASSES=10;
        System.out.println("Testing getTable(tableID).size()");
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getThisBusinessAdministrator());
            int numTables = ServiceName.values.size();
            int[][] counts=new int[PASSES][numTables];
            for(int d=0;d<PASSES;d++) {
                System.out.print("        Pass"+(d<9?"  ":" ")+(d+1)+" of "+PASSES+": ");
                if(d==0) System.out.println();
                for(int c=0;c<numTables;c++) {
                    AOServService<?,?> service = conn.getServices().get(ServiceName.values.get(c));
                    // Excluded for testing speed
                    /* TODOif(
                        (table instanceof DistroFileTable...c==SchemaTable.TableID.DISTRO_FILES.ordinal()
                        || c==SchemaTable.TableID.TRANSACTIONS.ordinal()
                        || c==SchemaTable.TableID.WHOIS_HISTORY.ordinal()
                    ) continue;*/
                    int size=service.getSize();
                    if(d==0) System.out.println(service+": "+size);
                    else System.out.print('.');
                    //if(c==SchemaTable.TableID.AO_SERVER_RESOURCES.ordinal()) System.out.println("\nao_server_resources.size="+size);
                    if(size<0) fail("Table size < 0 for table "+service.getTable().getTableName()+": "+size);
                    counts[d][c]=size;
                }
                System.out.println(" Done");
            }
            // Make sure counts match
            for(int c=1;c<PASSES;c++) {
                for(int d=0;d<numTables;d++) {
                    // Excluded for testing speed
                    /* TODO: if(
                        d==SchemaTable.TableID.DISTRO_FILES.ordinal()
                        || d==SchemaTable.TableID.TRANSACTIONS.ordinal()
                        || d==SchemaTable.TableID.WHOIS_HISTORY.ordinal()
                    ) continue;
                    // Skip master_history, master_server_profile, and master_processes because they frequently change sizes
                    if(
                        d!=SchemaTable.TableID.MASTER_HISTORY.ordinal()
                        && d!=SchemaTable.TableID.MASTER_SERVER_PROFILE.ordinal()
                        && d!=SchemaTable.TableID.MASTER_PROCESSES.ordinal()
                    ) {*/
                        AOServService<?,?> table=conn.getServices().get(ServiceName.values.get(d));
                        /*if(c==1) {
                            for(AOServObject<?,?> row : table.getSortedSet()) {
                                System.err.println(row);
                            }
                        }*/
                        String tableName=table.getTable().getTableName();
                        assertEquals("Mismatched counts from different passes on table "+tableName+": ", counts[0][d], counts[c][d]);
                    // TODO: }
                }
            }
        }
    }
}
