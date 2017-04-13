/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * Compares columns.
 *
 * @author  AO Industries, Inc.
 */
final public class SQLComparator<T> implements Comparator<T> {

	private final AOServConnector connector;
	private final SQLExpression[] exprs;
	private final boolean[] sortOrders;

	public SQLComparator(
		AOServConnector connector,
		SQLExpression[] exprs,
		boolean[] sortOrders
	) {
		this.connector=connector;
		this.exprs=exprs;
		this.sortOrders=sortOrders;
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public int compare(T O1, T O2) {
		try {
			if(O1 instanceof AOServObject) {
				AOServObject AO1=(AOServObject)O1;
				if(O2 instanceof AOServObject) {
					AOServObject AO2=(AOServObject)O2;
					return AO1.compareTo(connector, AO2, exprs, sortOrders);
				} else if(O2 instanceof Object[]) {
					return AO1.compareTo(connector, (Object[])O2, exprs, sortOrders);
				} else if(O2 instanceof Comparable) {
					return AO1.compareTo(connector, (Comparable)O2, exprs, sortOrders);
				} else throw new IllegalArgumentException("O2 must be either AOServObject, Object[], or Comparable");
			} else if(O1 instanceof Object[]) {
				T[] OA1=(T[])O1;
				if(O2 instanceof AOServObject) {
					AOServObject AO2=(AOServObject)O2;
					return -AO2.compareTo(connector, OA1, exprs, sortOrders);
				} else if(O2 instanceof Object[]) {
					return compare(OA1, (T[])O2);
				} else if(O2 instanceof Comparable) {
					throw new IllegalArgumentException("Comparing of Object[] and Comparable not supported.");
				} else throw new IllegalArgumentException("O2 must be either AOServObject, Object[], or Comparable");
			} else if(O1 instanceof Comparable) {
				Comparable C1=(Comparable)O1;
				if(O2 instanceof AOServObject) {
					AOServObject AO2=(AOServObject)O2;
					return -AO2.compareTo(connector, C1, exprs, sortOrders);
				} else if(O2 instanceof Object[]) {
					throw new IllegalArgumentException("Comparing of Comparable and Object[] not supported.");
				} else if(O2 instanceof Comparable) {
					return C1.compareTo(O2);
				} else throw new IllegalArgumentException("O2 must be either AOServObject or Comparable");
			} else throw new IllegalArgumentException("O1 must be either AOServObject or Comparable");
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	public int compare(T[] OA1, T[] OA2) {
		int OA1Len=OA1.length;
		int OA2Len=OA2.length;
		if(OA1Len!=OA2Len) throw new IllegalArgumentException("Mismatched array lengths when comparing two Object[]s: OA1.length="+OA1Len+", OA2.length="+OA2Len);
		for(int c=0;c<OA1Len;c++) {
			int diff=compare(OA1[c], OA2[c]);
			if(diff!=0) return diff;
		}
		return 0;
	}
}
