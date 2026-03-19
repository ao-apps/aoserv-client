/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2026  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * An expression in used in select statements and internal sorting.
 *
 * @author  AO Industries, Inc.
 */
public interface SqlExpression {

  String getColumnName();

  /**
   * Evaluates the expression on the given connector and object.
   */
  default Object evaluate(AoservConnector conn, AoservObject<?, ?> obj) throws IOException, SQLException {
    throw new SQLException("Is an aggregate function: " + toString());
  }

  /**
   * Evaluates the aggregate expression on the given connector and rows.
   */
  default Object evaluateAggregate(AoservConnector conn, List<AoservObject<?, ?>> rows) throws IOException, SQLException {
    throw new SQLException("Not an aggregate function: " + toString());
  }

  /**
   * Is this an aggregate function?  Controls whether evaluation is performed via
   * {@link SqlExpression#evaluate(com.aoindustries.aoserv.client.AoservConnector, com.aoindustries.aoserv.client.AoservObject)}
   * or {@link SqlExpression#evaluateAggregate(com.aoindustries.aoserv.client.AoservConnector, java.util.List)}.
   * Please be sure to implement the corresponding evaluation function.
   */
  boolean isAggregate();

  /**
   * The return type of this expression.
   */
  Type getType();

  /**
   * Gets all of the tables referenced by this expression.
   */
  default void getReferencedTables(AoservConnector conn, List<Table> tables) throws IOException, SQLException {
    // Do not add any by default
  }
}
