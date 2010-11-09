/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.validate;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.math.LongLong;
import com.aoindustries.sql.SQLUtility;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class InetAddressTest extends TestCase {

    private static final int NUM_TEST = 100000;

    public InetAddressTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(InetAddressTest.class);
    }

    public void testGetAddressAndParse() throws Exception {
        System.out.println("Testing getAddress and parse:");
        // Build test data
        Random random = new SecureRandom();
        List<InetAddress> inetAddresses = new ArrayList<InetAddress>(NUM_TEST);
        long startNanos = System.nanoTime();
        inetAddresses.add(InetAddress.valueOf(LongLong.valueOf(0, 0)));
        inetAddresses.add(InetAddress.valueOf(LongLong.valueOf(0, 1)));
        for(int c=0;c<NUM_TEST;c++) {
            inetAddresses.add(
                InetAddress.valueOf(
                    random.nextBoolean() ? LongLong.valueOf(0, random.nextInt()) // IPv4
                    : LongLong.valueOf(random.nextLong(), random.nextLong())     // IPv6
                )
            );
        }
        long endNanos = System.nanoTime();
        System.out.println("    "+NUM_TEST+" random InetAddresses created in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");

        // Test toString performance
        List<String> toParse = new ArrayList<String>(NUM_TEST);
        startNanos = System.nanoTime();
        for(int c=0;c<NUM_TEST;c++) toParse.add(inetAddresses.get(c).toString());
        endNanos = System.nanoTime();
        System.out.println("    "+NUM_TEST+" toString() in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");

        // Test toString performance
        List<InetAddress> parsed = new ArrayList<InetAddress>(NUM_TEST);
        startNanos = System.nanoTime();
        for(int c=0;c<NUM_TEST;c++) parsed.add(InetAddress.valueOf(toParse.get(c)));
        endNanos = System.nanoTime();
        System.out.println("    "+NUM_TEST+" valueOf(String) in "+SQLUtility.getMilliDecimal((endNanos-startNanos)/1000)+" ms");

        // Test parsing result of toString
        for(int c=0; c<NUM_TEST; c++) assertEquals(parsed.get(c), inetAddresses.get(c));
    }
}
