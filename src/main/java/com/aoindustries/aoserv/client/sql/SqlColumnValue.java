/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Gets the value for one column.
 *
 * @author  AO Industries, Inc.
 */
public final class SqlColumnValue implements SqlExpression {

  private final Column column;
  private final Type columnType;

  public SqlColumnValue(AoservConnector conn, Column column) throws SQLException, IOException {
    if (column == null) {
      throw new NullPointerException("column is null");
    }
    this.column = column;
    this.columnType = column.getType(conn);
  }

  @Override
  public String toString() {
    return Parser.quote(column.getName());
  }

  @Override
  public String getColumnName() {
    return column.getName();
  }

  @Override
  public Object evaluate(AoservConnector conn, AoservObject<?, ?> obj) {
    return obj.getColumn(column.getIndex());
  }

  @Override
  public Type getType() {
    return columnType;
  }

  @Override
  public void getReferencedTables(AoservConnector conn, List<Table> tables) throws SQLException, IOException {
    Table table = column.getTable(conn);
    if (!tables.contains(table)) {
      tables.add(table);
    }
  }
}
