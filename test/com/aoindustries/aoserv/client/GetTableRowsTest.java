/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.math.Statistics;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Gets the rows of each table, timing the process.
 *
 * @author  AO Industries, Inc.
 */
public class GetTableRowsTest extends TestCase {
    
    private List<AOServConnector> conns;

    public GetTableRowsTest(String testName) {
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
        return new TestSuite(GetTableRowsTest.class);
    }

    /**
     * Test the getSize().size() method of each AOServTable.
     */
    public void testSetSizes() throws Exception {
        final int WARMUP_PASSES = 10;
        final int PASSES=WARMUP_PASSES + 30;
        System.out.println("Testing getTable(tableID).size()");
        for(AOServConnector conn : conns) {
            System.out.println("    "+conn.getThisBusinessAdministrator());
            int numTables = ServiceName.values.size();
            List<Long> times = new ArrayList<Long>(PASSES-WARMUP_PASSES);
            for(int pass=0;pass<PASSES;pass++) {
                if(pass<WARMUP_PASSES) System.out.println("        Warmup "+(pass+1)+" of "+WARMUP_PASSES+": ");
                else System.out.println("        Pass "+(pass+1-WARMUP_PASSES)+" of "+(PASSES-WARMUP_PASSES)+": ");
                int c = ServiceName.ticket_actions.ordinal();
                //for(int c=0;c<numTables;c++) {
                    ServiceName serviceName = ServiceName.values.get(c);
                    AOServService<?,?> service = conn.getServices().get(serviceName);
                    // Excluded for testing speed
                    if(!(service instanceof UnionService)) {
                        long startTime = System.currentTimeMillis();
                        int size=service.getSet().size();
                        long endTime = System.currentTimeMillis();
                        if(pass>WARMUP_PASSES) times.add(endTime - startTime);
                        System.out.println(service+": "+size+" in "+BigDecimal.valueOf(endTime - startTime, 3)+" ms");
                    }
                //}
            }
            System.out.println("    Min...: " + BigDecimal.valueOf(Collections.min(times), 3));
            System.out.println("    Max...: " + BigDecimal.valueOf(Collections.max(times), 3));
            double mean = Statistics.mean(times);
            System.out.println("    Mean..: " + BigDecimal.valueOf((long)mean, 3));
            System.out.println("    StdDev: " + BigDecimal.valueOf((long)Statistics.standardDeviation(mean, times), 3));
            break; // TODO: Remove this: Only test first connector
        }
    }
}
