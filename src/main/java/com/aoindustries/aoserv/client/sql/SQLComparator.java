/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.sql;

import com.aoapps.lang.exception.WrappedException;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * Compares columns.
 *
 * @author  AO Industries, Inc.
 */
public final class SQLComparator<T> implements Comparator<T> {

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

  @Override
  public int compare(T o1, T o2) {
    try {
      if (o1 instanceof AOServObject) {
        AOServObject<?, ?> ao1 = (AOServObject)o1;
        if (o2 instanceof AOServObject) {
          AOServObject<?, ?> ao2 = (AOServObject)o2;
          return ao1.compareTo(connector, ao2, exprs, sortOrders);
        } else if (o2 instanceof Object[]) {
          return ao1.compareTo(connector, (Object[])o2, exprs, sortOrders);
        } else if (o2 instanceof Comparable) {
          return ao1.compareTo(connector, (Comparable)o2, exprs, sortOrders);
        } else {
          throw new IllegalArgumentException("O2 must be either AOServObject, Object[], or Comparable");
        }
      } else if (o1 instanceof Object[]) {
        @SuppressWarnings({"unchecked"})
        T[] oa1 = (T[])o1;
        if (o2 instanceof AOServObject) {
          AOServObject<?, ?> ao2 = (AOServObject)o2;
          return -ao2.compareTo(connector, oa1, exprs, sortOrders);
        } else if (o2 instanceof Object[]) {
          @SuppressWarnings({"unchecked"})
          T[] oa2 = (T[])o2;
          return compare(oa1, oa2);
        } else if (o2 instanceof Comparable) {
          throw new IllegalArgumentException("Comparing of Object[] and Comparable not supported.");
        } else {
          throw new IllegalArgumentException("O2 must be either AOServObject, Object[], or Comparable");
        }
      } else if (o1 instanceof Comparable) {
        @SuppressWarnings({"unchecked"})
        Comparable<Object> c1 = (Comparable)o1;
        if (o2 instanceof AOServObject) {
          AOServObject<?, ?> ao2 = (AOServObject)o2;
          return -ao2.compareTo(connector, c1, exprs, sortOrders);
        } else if (o2 instanceof Object[]) {
          throw new IllegalArgumentException("Comparing of Comparable and Object[] not supported.");
        } else if (o2 instanceof Comparable) {
          return c1.compareTo(o2);
        } else {
          throw new IllegalArgumentException("O2 must be either AOServObject or Comparable");
        }
      } else {
        throw new IllegalArgumentException("O1 must be either AOServObject or Comparable");
      }
    } catch (IOException | SQLException err) {
      throw new WrappedException(err);
    }
  }

  public int compare(T[] oa1, T[] oa2) {
    int oa1Len = oa1.length;
    int oa2Len = oa2.length;
    if (oa1Len != oa2Len) {
      throw new IllegalArgumentException("Mismatched array lengths when comparing two Object[]s: OA1.length=" + oa1Len + ", OA2.length=" + oa2Len);
    }
    for (int c = 0; c < oa1Len; c++) {
      int diff = compare(oa1[c], oa2[c]);
      if (diff != 0) {
        return diff;
      }
    }
    return 0;
  }
}
