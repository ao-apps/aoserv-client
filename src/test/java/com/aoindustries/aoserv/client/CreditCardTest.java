package com.aoindustries.aoserv.client;
/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the credit card class.
 *
 * @author  AO Industries, Inc.
 */
public class CreditCardTest extends TestCase {

    private Random random;

    public CreditCardTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        random = AOServConnector.getRandom();
    }

    @Override
    protected void tearDown() throws Exception {
        random = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(CreditCardTest.class);
        return suite;
    }

    /**
     * Runs the full MySQL test.
     */
    public void testRandomizeDerandomize() {
        StringBuilder SB = new StringBuilder();
        for(int c=0;c<100;c++) {
            int len = random.nextInt(50);
            SB.setLength(0);
            for(int d=0;d<len;d++) {
                int randVal = random.nextInt(13);
                char randCh;
                if(randVal<10) randCh = (char)('0'+randVal);
                else if(randVal==10) randCh = ' ';
                else if(randVal==11) randCh = '-';
                else if(randVal==12) randCh = '/';
                else throw new AssertionError("Unexpected value for randVal: "+randVal);
                SB.append(randCh);
            }
            String original = SB.toString();
            //System.out.println(original);
            String randomized = CreditCard.randomize(original);
            System.out.println(randomized);
            String derandomized = CreditCard.derandomize(randomized);
            //System.out.println(derandomized);
            assertEquals("original and derandomized do not match", original, derandomized);
        }
    }
}
