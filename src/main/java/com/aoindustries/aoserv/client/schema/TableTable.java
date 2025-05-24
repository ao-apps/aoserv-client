/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.schema;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.sort.JavaSort;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.sql.Parser;
import com.aoindustries.aoserv.client.sql.SqlExpression;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @see  Table
 *
 * @author  AO Industries, Inc.
 */
public final class TableTable extends GlobalTableIntegerKey<Table> {

  TableTable(AoservConnector connector) {
    super(connector, Table.class);
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return null;
  }

  /**
   * Supports Integer (table_id), String(name), and SchemaTable.TableId (table_id) keys.
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public Table get(Object pkey) throws IOException, SQLException {
    if (pkey == null) {
      return null;
    } else if (pkey instanceof Integer) {
      return get(((Number) pkey).intValue());
    } else if (pkey instanceof String) {
      return get((String) pkey);
    } else if (pkey instanceof Table.TableId) {
      return get((Table.TableId) pkey);
    } else {
      throw new IllegalArgumentException("Must be an Integer, a String, or a SchemaTable.TableId");
    }
  }

  /** Avoid repeated array copies. */
  private static final int numTables = Table.TableId.values().length;

  @Override
  public List<Table> getRows() throws IOException, SQLException {
    List<Table> rows = super.getRows();
    int size = rows.size();
    if (size != numTables) {
      throw new SQLException("Unexpected number of rows: expected " + numTables + ", got " + size);
    }
    return rows;
  }

  /**
   * {@inheritDoc}
   *
   * @see  #get(java.lang.Object)
   */
  @Override
  public Table get(int tableId) throws IOException, SQLException {
    return getRows().get(tableId);
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public Table get(String name) throws IOException, SQLException {
    return getUniqueRow(Table.COLUMN_NAME, name);
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public Table get(Table.TableId tableId) throws IOException, SQLException {
    return get(tableId.ordinal());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SCHEMA_TABLES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.DESC) || command.equalsIgnoreCase(Command.DESCRIBE)) {
      if (Aosh.checkParamCount(Command.DESCRIBE, args, 1, err)) {
        String tableName = Parser.unquote(args[1]);
        Table table = connector.getSchema().getTable().get(tableName);
        if (table != null) {
          table.printDescription(connector, out, isInteractive);
          out.flush();
        } else {
          err.print("aosh: " + Command.DESCRIBE + ": table not found: ");
          err.println(Parser.quote(tableName));
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SELECT)) {
      int argCount = args.length;
      if (argCount >= 4) {
        if (argCount == 4 && "count(*)".equalsIgnoreCase(args[1])) {
          selectCount(args, out, err, isInteractive);
        } else {
          selectRows(args, out, err, isInteractive);
        }
      } else if (argCount < 4) {
        err.println("aosh: " + Command.SELECT + ": not enough parameters");
        err.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SHOW)) {
      int argCount = args.length;
      if (argCount >= 2) {
        if ("tables".equalsIgnoreCase(args[1])) {
          handleCommand(new String[]{"select", "name,", "description", "from", "schema_tables"}, in, out, err, isInteractive);
        } else {
          err.println("aosh: " + Command.SHOW + ": unknown parameter: " + args[1]);
          err.flush();
        }
      } else {
        err.println("aosh: " + Command.SHOW + ": not enough parameters");
        err.flush();
      }
      return true;
    }
    return false;
  }

  private void selectCount(String[] args, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
    // Is a select count(*)
    if ("from".equalsIgnoreCase(args[2])) {
      String tableName = Parser.unquote(args[3]);
      Table table = connector.getSchema().getTable().get(tableName);
      if (table != null) {
        SQLUtility.printTable(
            new String[]{"count"},
            (Iterable<Object[]>) Collections.singleton(new Object[]{table.getAoservTable(connector).size()}),
            out,
            isInteractive,
            new boolean[]{true}
        );
        out.flush();
      } else {
        err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
        err.flush();
      }
    } else {
      err.println("aosh: " + Command.SELECT + ": unknown parameter: " + args[2]);
      err.flush();
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes", "AssignmentToForLoopParameter", "UseSpecificCatch", "TooBroadCatch"})
  private void selectRows(String[] args, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
    int argCount = args.length;
    List<String> expressionArgs = new ArrayList<>();

    // Parse the list of expressions until "from" is found
    int c = 1;
    for (; c < argCount; c++) {
      String arg = args[c];
      if ("from".equalsIgnoreCase(arg)) {
        break;
      }
      expressionArgs.add(arg);
    }
    if (c >= argCount - 1) {
      // The from was not found
      err.println("aosh: " + Command.SELECT + ": parameter not found: from");
      err.flush();
      return;
    }

    // Get the table name
    String tableName = Parser.unquote(args[++c]);
    c++;

    Table schemaTable = connector.getSchema().getTable().get(tableName);
    if (schemaTable != null) {
      List<String> expressions = new ArrayList<>(expressionArgs.size());
      // Substitute any * columnName and ,
      for (String expressionArg : expressionArgs) {
        if ("*".equals(expressionArg)) {
          for (Column column : schemaTable.getSchemaColumns(connector)) {
            expressions.add(Parser.quote(column.getName()));
          }
        } else {
          do {
            String current;
            int commaPos = Parser.indexOfNotQuoted(expressionArg, ',');
            if (commaPos == -1) {
              current = expressionArg;
              expressionArg = "";
            } else {
              current = expressionArg.substring(0, commaPos);
              expressionArg = expressionArg.substring(commaPos + 1);
            }
            if ("*".equals(current)) {
              for (Column column : schemaTable.getSchemaColumns(connector)) {
                expressions.add(Parser.quote(column.getName()));
              }
            } else {
              expressions.add(current);
            }
          } while (!expressionArg.isEmpty());
        }
      }

      AoservTable<?, ?> aoServTable = schemaTable.getAoservTable(connector);

      // Parse any order by clause
      List<SqlExpression> orderExpressions = new ArrayList<>();
      List<Boolean> sortOrders = new ArrayList<>();
      if (c < argCount) {
        String arg = args[c++];
        if ("order".equalsIgnoreCase(arg)) {
          if (c < argCount) {
            if ("by".equalsIgnoreCase(args[c++])) {
              while (c < argCount) {
                String orderBy = args[c++];
                do {
                  String expr;
                  int commaPos = Parser.indexOfNotQuoted(orderBy, ',');
                  if (commaPos == -1) {
                    expr = orderBy;
                    orderBy = "";
                  } else {
                    expr = orderBy.substring(0, commaPos);
                    orderBy = orderBy.substring(commaPos + 1);
                  }
                  if (
                      !orderExpressions.isEmpty()
                          && (
                          "asc".equalsIgnoreCase(expr)
                              || "ascending".equalsIgnoreCase(expr)
                        )
                  ) {
                    sortOrders.set(sortOrders.size() - 1, AoservTable.ASCENDING);
                  } else if (
                      !orderExpressions.isEmpty()
                          && (
                          "desc".equalsIgnoreCase(expr)
                              || "descending".equalsIgnoreCase(expr)
                        )
                  ) {
                    sortOrders.set(sortOrders.size() - 1, AoservTable.DESCENDING);
                  } else { // if (!expr.isEmpty()) {
                    orderExpressions.add(Parser.parseSqlExpression(aoServTable, expr));
                    sortOrders.add(AoservTable.ASCENDING);
                  }
                } while (!orderBy.isEmpty());
              }
              if (orderExpressions.isEmpty()) {
                throw new SQLException("Parse error: no expressions listed after 'order by'");
              }
            } else {
              throw new SQLException("Parse error: 'by' expected");
            }
          } else {
            throw new SQLException("Parse error: 'by' expected");
          }
        } else {
          throw new SQLException("Parse error: 'order' expected, found '" + arg + '\'');
        }
      }

      // Figure out the expressions for each columns
      final int numExpressions = expressions.size();
      final SqlExpression[] valueExpressions = new SqlExpression[numExpressions];
      final Type[] valueTypes = new Type[numExpressions];
      int supportsAnyPrecisionCount = 0;
      boolean[] rightAligns = new boolean[numExpressions];
      for (int d = 0; d < numExpressions; d++) {
        SqlExpression sql = Parser.parseSqlExpression(aoServTable, expressions.get(d));
        Type type = sql.getType();
        valueExpressions[d] = sql;
        valueTypes[d] = type;
        if (type.supportsPrecision()) {
          supportsAnyPrecisionCount++;
        }
        rightAligns[d] = type.alignRight();
      }

      // Get the data
      List<AoservObject> rows = null;
      boolean rowsCopied = false;
      Throwable t0 = null;
      try {
        // Sort if needed
        if (!orderExpressions.isEmpty()) {
          SqlExpression[] exprs = orderExpressions.toArray(new SqlExpression[orderExpressions.size()]);
          boolean[] orders = new boolean[exprs.length];
          for (int d = 0; d < orders.length; d++) {
            orders[d] = sortOrders.get(d);
          }
          rows = (List<AoservObject>) aoServTable.getRowsCopy();
          rowsCopied = true;
          connector.sort(JavaSort.getInstance(), rows, exprs, orders);
        } else {
          rows = (List<AoservObject>) aoServTable.getRows();
        }
        final List<AoservObject> finalRows = rows;
        final int numRows = rows.size();

        // Evaluate the expressions while finding the maximum precisions per column.
        // The precisions allow uniform formatting within a column to depend on the overall contents of the column.
        final int[] precisions = new int[numExpressions];
        Arrays.fill(precisions, -1);
        // Only iterate through all rows here when needing to process precisions
        if (supportsAnyPrecisionCount > 0) {
          // Stop searching if all max precisions have been found
          int precisionsNotMaxedCount = supportsAnyPrecisionCount;
          ROWS:
          for (AoservObject<?, ?> row : rows) {
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

        // Print the results
        String[] cnames = new String[numExpressions];
        for (int d = 0; d < numExpressions; d++) {
          cnames[d] = valueExpressions[d].getColumnName();
        }
        try {
          SQLUtility.printTable(
              cnames,
              (Iterable<String[]>) () -> new Iterator<>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                  return index < numRows;
                }

                @Override
                public String[] next() throws NoSuchElementException {
                  if (index >= numRows) {
                    throw new NoSuchElementException();
                  }
                  try {
                    // Convert the results to strings
                    AoservObject<?, ?> row = finalRows.get(index);
                    String[] strings = new String[numExpressions];
                    for (int col = 0; col < numExpressions; col++) {
                      strings[col] = valueTypes[col].getString(
                          valueExpressions[col].evaluate(connector, row),
                          precisions[col]
                      );
                    }
                    index++;
                    return strings;
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
        if (rowsCopied && rows instanceof AutoCloseable) {
          try {
            ((AutoCloseable) rows).close();
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
      out.flush();
    } else {
      err.println("aosh: " + Command.SELECT + ": table not found: " + Parser.quote(tableName));
      err.flush();
    }
  }
}
