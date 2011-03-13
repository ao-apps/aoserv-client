/*
 * Copyright 2006-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.Column;
import com.aoindustries.table.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests all of the table and column names.
 *
 * @author  AO Industries, Inc.
 */
public class TableNameTest extends TestCase {
    
    private List<AOServConnector> conns;

    public TableNameTest(String testName) {
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
        return new TestSuite(TableNameTest.class);
    }

    /**
     * Tests the table interfaces.
     */
    public void testTableNames() throws Exception {
        System.out.println("Testing table and column names for uniqueness");
        Set<String> tableNames = new HashSet<String>();
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getThisBusinessAdministrator());
            tableNames.clear();
            for(ServiceName schemaTableName : ServiceName.values) {
                AOServService<?,?> aoTable=conn.getServices().get(schemaTableName);
                Table<MethodColumn,?> table = aoTable.getTable();
                String tableName = table.getTableName();
                System.out.println("        "+tableName);
                if(!tableNames.add(tableName)) fail("Table name found twice in tables: "+tableNames);
                for(Column column : table.getColumns()) {
                    System.out.println("            "+column);
                }
            }
        }
    }
}
