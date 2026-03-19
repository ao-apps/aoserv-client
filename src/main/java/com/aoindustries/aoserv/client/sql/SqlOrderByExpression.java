/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2026  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservTable;
import java.util.Objects;

/**
 * An expression combined with a direction for use within "ORDER BY".
 *
 * @author  AO Industries, Inc.
 */
public final class SqlOrderByExpression {

  /**
   * The optional case-insensitive representation for ascending.
   */
  public static final String ASC = "ASC";

  /**
   * The optional case-insensitive long-form representation for ascending.
   */
  public static final String ASCENDING = "ASCENDING";

  /**
   * The required case-insensitive representation for descending.
   */
  public static final String DESC = "DESC";

  /**
   * The required case-insensitive long-form representation for descending.
   */
  public static final String DESCENDING = "DESCENDING";

  private final SqlExpression expression;
  private final boolean ascending;

  public SqlOrderByExpression(SqlExpression expression, boolean ascending) throws IllegalArgumentException {
    this.expression = Objects.requireNonNull(expression);
    if (expression.isAggregate()) {
      throw new IllegalArgumentException("Aggregate functions not supported for ORDER BY.");
    }
    this.ascending = ascending;
  }

  @Override
  public String toString() {
    String exprStr = expression.toString();
    return ascending ? exprStr : (exprStr + ' ' + DESC);
  }

  public SqlExpression getExpression() {
    return expression;
  }

  /**
   * Is ascending?
   *
   * @return Either {@link AoservTable#ASCENDING} or {@link AoservTable#DESCENDING}.
   */
  public boolean isAscending() {
    return ascending;
  }
}
