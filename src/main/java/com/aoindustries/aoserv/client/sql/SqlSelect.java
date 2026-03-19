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

import com.aoapps.hodgepodge.sort.JavaSort;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.io.function.IOFunctionE;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Represents a parsed SELECT statement.
 *
 * @author  AO Industries, Inc.
 */
public final class SqlSelect {

  /**
   * The case-insensitive SELECT keyword.
   */
  public static final String SELECT = "SELECT";

  static {
    assert SELECT.equalsIgnoreCase(Command.SELECT);
  }

  /**
   * The case-insensitive FROM keyword.
   */
  public static final String FROM = "FROM";

  /**
   * The case-insensitive ORDER keyword.
   */
  public static final String ORDER = "ORDER";

  /**
   * The case-insensitive BY keyword.
   */
  public static final String BY = "BY";

  private final boolean isAggregate;
  private final List<SqlExpression> expressions;
  private final Table fromTable;
  private final List<SqlOrderByExpression> orderBy;

  public SqlSelect(List<SqlExpression> expressions, Table fromTable, List<SqlOrderByExpression> orderBy)
      throws IllegalArgumentException {
    int numExpressions = expressions.size();
    if (numExpressions < 1) {
      throw new IllegalArgumentException("One or more expressions required.");
    }
    // May not combined aggregate and non-aggregate functions
    isAggregate = expressions.get(0).isAggregate();
    for (int i = 1; i < numExpressions; i++) {
      if (isAggregate != expressions.get(i).isAggregate()) {
        throw new IllegalArgumentException("May not combine aggregate functions with non-aggregate functions (GROUP BY not implemented).");
      }
    }
    // May not use ORDER BY with aggregate functions
    if (isAggregate && !orderBy.isEmpty()) {
      throw new IllegalArgumentException("May not use " + ORDER + " " + BY + " with aggregate functions.");
    }
    this.expressions = expressions;
    this.fromTable = fromTable;
    this.orderBy = orderBy;
  }

  @Override
  public String toString() {
    StringBuilder sql = new StringBuilder();
    sql
        .append(SELECT)
        .append(' ')
        .append(expressions.stream().map(Object::toString).collect(Collectors.joining(", ")))
        .append(' ')
        .append(FROM)
        .append(' ')
        .append(Parser.quote(fromTable.getName()));
    if (!orderBy.isEmpty()) {
      sql
          .append(ORDER)
          .append(' ')
          .append(BY)
          .append(orderBy.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
    return sql.toString();
  }

  public boolean isAggregate() {
    return isAggregate;
  }

  /**
   * {@link SqlExpression#isAggregate()} on every expression will match {@link SqlSelect#isAggregate()}.
   * Mixed aggregate and non-aggregate not currently supported.
   */
  public List<SqlExpression> getExpressions() {
    return Collections.unmodifiableList(expressions);
  }

  public Table getFromTable() {
    return fromTable;
  }

  public List<SqlOrderByExpression> getOrderBy() {
    return Collections.unmodifiableList(orderBy);
  }

  /**
   * Executes the query for display output.
   * TODO: This does not yet implement WHERE, so does not yet have any query planner phase.
   * TODO: This implementation writes to the given out, and has not yet been split into separate execute and display phases.
   */
  @SuppressWarnings({"unchecked", "rawtypes", "UseSpecificCatch", "TooBroadCatch"})
  public void execute(AoservConnector connector, Appendable out, boolean isInteractive) throws IOException, SQLException {
    AoservTable<?, ?> aoServTable = fromTable.getAoservTable(connector);

    // Figure out the expressions for each columns
    final int numExpressions = expressions.size();
    final SqlExpression[] valueExpressions = new SqlExpression[numExpressions];
    final Type[] valueTypes = new Type[numExpressions];
    int supportsAnyPrecisionCount = 0;
    boolean[] rightAligns = new boolean[numExpressions];
    for (int d = 0; d < numExpressions; d++) {
      SqlExpression sql = expressions.get(d);
      Type type = sql.getType();
      valueExpressions[d] = sql;
      valueTypes[d] = type;
      if (type.supportsPrecision()) {
        supportsAnyPrecisionCount++;
      }
      rightAligns[d] = type.alignRight();
    }

    // Get the data
    List<AoservObject> queriedRows = null;
    boolean queriesRowsCopied = false;
    Throwable t0 = null;
    try {
      // The precisions allow uniform formatting within a column to depend on the overall contents of the column.
      final int[] precisions = new int[numExpressions];
      Arrays.fill(precisions, -1);

      final List<? extends Object> displayRows;
      final IOFunctionE<Object, Object[], SQLException> displayRowExecutor;
      if (isAggregate) {
        assert orderBy.isEmpty() : "ORDER BY not supported on aggregate functions";
        Integer optimizedCount = null; // Set when optimized first full-table count is determined
        Object[] aggregates = new Object[numExpressions];
        for (int col = 0; col < numExpressions; col++) {
          SqlExpression expr = valueExpressions[col];
          if (expr instanceof SqlCount) {
            // TODO: Once there is WHERE clause, this optimization can only be performed when no WHERE
            if (optimizedCount == null) {
              optimizedCount = aoServTable.size();
            }
            aggregates[col] = optimizedCount;
          } else {
            throw new SQLException("Unexpected type of aggregate function: " + expr);
          }
        }
        displayRows = Collections.singletonList(aggregates);
        displayRowExecutor = row -> (Object[]) row;
      } else {
        // Sort if needed
        if (!orderBy.isEmpty()) {
          queriedRows = (List<AoservObject>) aoServTable.getRowsCopy();
          queriesRowsCopied = true;
          connector.sort(JavaSort.getInstance(), queriedRows, orderBy.toArray(SqlOrderByExpression[]::new));
        } else {
          queriedRows = (List<AoservObject>) aoServTable.getRows();
        }
        displayRows = queriedRows;

        // Evaluate the expressions while finding the maximum precisions per column.
        // Only iterate through all rows here when needing to process precisions
        if (supportsAnyPrecisionCount > 0) {
          // Stop searching if all max precisions have been found
          int precisionsNotMaxedCount = supportsAnyPrecisionCount;
          ROWS:
          for (AoservObject<?, ?> row : queriedRows) {
            for (int col = 0; col < numExpressions; col++) {
              Type type = valueTypes[col];
              // Skip evaluation when precision not supported
              if (type.supportsPrecision()) {
                int maxPrecision = type.getMaxPrecision();
                int current = precisions[col];
                if (
                    maxPrecision == -1
                        || current == -1
                        || current < maxPrecision
                ) {
                  int precision = type.getPrecision(valueExpressions[col].evaluate(connector, row));
                  if (
                      precision != -1
                          && (current == -1 || precision > current)
                  ) {
                    precisions[col] = precision;
                    if (maxPrecision != -1 && precision >= maxPrecision) {
                      precisionsNotMaxedCount--;
                      // Stop searching when all precision-based columns are maxed
                      if (precisionsNotMaxedCount <= 0) {
                        break ROWS;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        displayRowExecutor = row -> {
          AoservObject<?, ?> obj = (AoservObject<?, ?>) row;
          // Convert the results to strings
          String[] strings = new String[numExpressions];
          for (int col = 0; col < numExpressions; col++) {
            strings[col] = valueTypes[col].getString(
                valueExpressions[col].evaluate(connector, obj),
                precisions[col]
            );
          }
          return strings;
        };
      }

      // Print the results
      String[] cnames = new String[numExpressions];
      for (int d = 0; d < numExpressions; d++) {
        cnames[d] = valueExpressions[d].getColumnName();
      }
      final int numRows = displayRows.size();
      try {
        SQLUtility.printTable(
            cnames,
            (Iterable<Object[]>) () -> new Iterator<>() {
              private int index = 0;

              @Override
              public boolean hasNext() {
                return index < numRows;
              }

              @Override
              public Object[] next() throws NoSuchElementException {
                if (index >= numRows) {
                  throw new NoSuchElementException();
                }
                try {
                  // Convert the results to objects
                  return displayRowExecutor.apply(displayRows.get(index++));
                } catch (IOException | SQLException e) {
                  throw new WrappedException(e);
                }
              }
            },
            out,
            isInteractive,
            rightAligns
        );
      } catch (WrappedException e) {
        Throwable cause = e.getCause();
        if (cause instanceof IOException) {
          throw (IOException) cause;
        }
        if (cause instanceof SQLException) {
          throw (SQLException) cause;
        }
        throw e;
      }
    } catch (Throwable t) {
      t0 = Throwables.addSuppressed(t0, t);
    } finally {
      if (queriesRowsCopied && queriedRows instanceof AutoCloseable) {
        try {
          ((AutoCloseable) queriedRows).close();
        } catch (Throwable t) {
          t0 = Throwables.addSuppressed(t0, t);
        }
      }
    }
    if (t0 != null) {
      if (t0 instanceof IOException) {
        throw (IOException) t0;
      }
      if (t0 instanceof SQLException) {
        throw (SQLException) t0;
      }
      throw Throwables.wrap(t0, WrappedException.class, WrappedException::new);
    }
  }
}
