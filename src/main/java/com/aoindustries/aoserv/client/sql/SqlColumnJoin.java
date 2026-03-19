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
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.DbEnum;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Gets the value for one column by following its reference to another table.
 * See {@link SqlColumnJoin#SqlColumnJoin(com.aoindustries.aoserv.client.AoservConnector, com.aoindustries.aoserv.client.sql.SqlExpression, com.aoindustries.aoserv.client.schema.Column, com.aoindustries.aoserv.client.schema.Column)}
 * for allowed type conversions.
 *
 * @author  AO Industries, Inc.
 */
public final class SqlColumnJoin implements SqlExpression {

  private final SqlExpression expression;
  private final Column keyColumn;
  private final int keyIndex;
  private final Column valueColumn;
  private final Type type;
  private final AoservTable<?, ?> table;
  private final int valueIndex;
  private final Function<Object, Object> expressionToKeyTypeMapper;

  /**
   * Supports the following mismatches between expression.type and keyColumn.type, any other mismatch will throw
   * {@link SQLException}:
   *
   * <ol>
   * <li>{@link Type#FKEY} to {@link Type#PKEY}, converted via {@link Function#identity()}.</li>
   * <li>{@link Type#ENUM} to {@link Type#STRING}, converted via {@link DbEnum#toDbValue(java.lang.Enum)}.</li>
   * </ol>
   */
  public SqlColumnJoin(
      AoservConnector conn,
      SqlExpression expression,
      Column keyColumn,
      Column valueColumn
  ) throws SQLException, IOException, IllegalArgumentException {
    this.expression = Objects.requireNonNull(expression);
    if (expression.isAggregate()) {
      throw new IllegalArgumentException("Aggregate functions not supported for dot-joins.");
    }
    this.keyColumn = keyColumn;
    this.keyIndex = keyColumn.getIndex();
    this.valueColumn = valueColumn;
    this.type = valueColumn.getType(conn);
    this.table = keyColumn.getTable(conn).getAoservTable(conn);
    this.valueIndex = valueColumn.getIndex();
    // Support specific types of type conversions across joins
    Type expressionType = expression.getType();
    int expressionTypeId = expressionType.getId();
    Type keyType = keyColumn.getType(conn);
    int keyTypeId = keyType.getId();
    if (
        expressionTypeId == keyTypeId
          // Allow joins from fkey to pkey
          || (expressionTypeId == Type.FKEY && keyTypeId == Type.PKEY)
    ) {
      expressionToKeyTypeMapper = Function.identity();
    } else if (expressionTypeId == Type.ENUM && keyTypeId == Type.STRING) {
      // Allow joins from enum to string
      expressionToKeyTypeMapper = value -> DbEnum.toDbValue((Enum<?>) value);
    } else {
      throw new IllegalArgumentException("Join type mismatch: " + expression + " is " + expressionType
          + " while " + keyColumn + " is " + keyType);
    }
  }

  @Override
  public String toString() {
    return expression.toString() + "." + Parser.quote(valueColumn.getName());
  }

  @Override
  public String getColumnName() {
    return valueColumn.getName();
  }

  @Override
  public Object evaluate(AoservConnector conn, AoservObject<?, ?> obj) throws IOException, SQLException {
    Object keyValue = expression.evaluate(conn, obj);
    if (keyValue != null) {
      AoservObject<?, ?> row = table.getUniqueRow(keyIndex, expressionToKeyTypeMapper.apply(keyValue));
      if (row != null) {
        return row.getColumn(valueIndex);
      }
    }
    return null;
  }

  @Override
  public boolean isAggregate() {
    return false;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public void getReferencedTables(AoservConnector conn, List<Table> tables) throws IOException, SQLException {
    expression.getReferencedTables(conn, tables);
    Table t = keyColumn.getTable(conn);
    if (!tables.contains(t)) {
      tables.add(t);
    }
  }
}
