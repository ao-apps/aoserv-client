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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Counts the number of rows.
 *
 * @author  AO Industries, Inc.
 */
public final class SqlCount implements SqlExpression {

  /**
   * The case-insensitive function name.
   */
  public static final String COUNT = "count";

  /**
   * The case-insensitive representation as a function.
   */
  public static final String COUNT_FUNCTION = COUNT + "(*)";

  private final Type intType;

  public SqlCount(AoservConnector conn) throws SQLException, IOException {
    this.intType = Objects.requireNonNull(conn.getSchema().getType().get(Type.INT));
  }

  @Override
  public String toString() {
    return COUNT_FUNCTION;
  }

  @Override
  public String getColumnName() {
    return COUNT;
  }

  @Override
  public Object evaluateAggregate(AoservConnector conn, List<AoservObject<?, ?>> rows) {
    return rows.size();
  }

  @Override
  public boolean isAggregate() {
    return true;
  }

  @Override
  public Type getType() {
    return intType;
  }
}
