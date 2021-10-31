/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008, 2009, 2016, 2017, 2018, 2019, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.payment;

import com.aoindustries.aoserv.client.AOServConnector;
import java.security.SecureRandom;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the credit card class.
 *
 * @author  AO Industries, Inc.
 */
public class CreditCardTest extends TestCase {

	private SecureRandom secureRandom;

	public CreditCardTest(String testName) {
		super(testName);
	}

	@Override
	protected void setUp() throws Exception {
		secureRandom = AOServConnector.getSecureRandom();
	}

	@Override
	protected void tearDown() throws Exception {
		secureRandom = null;
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(CreditCardTest.class);
		return suite;
	}

	/**
	 * @see CreditCard#randomize(java.lang.String)
	 * @see CreditCard#derandomize(java.lang.String)
	 */
	public void testRandomizeDerandomize() {
		StringBuilder sb = new StringBuilder();
		for(int c = 0; c < 100; c++) {
			int len = secureRandom.nextInt(50);
			sb.setLength(0);
			for(int d = 0; d < len; d++) {
				int randVal = secureRandom.nextInt(13);
				char randCh;
				if(randVal<10) randCh = (char)('0'+randVal);
				else if(randVal==10) randCh = ' ';
				else if(randVal==11) randCh = '-';
				else if(randVal==12) randCh = '/';
				else throw new AssertionError("Unexpected value for randVal: "+randVal);
				sb.append(randCh);
			}
			String original = sb.toString();
			//System.out.println(original);
			String randomized = CreditCard.randomize(original);
			//System.out.println(randomized);
			String derandomized = CreditCard.derandomize(randomized);
			//System.out.println(derandomized);
			assertEquals("original and derandomized do not match", original, derandomized);
		}
	}
}
