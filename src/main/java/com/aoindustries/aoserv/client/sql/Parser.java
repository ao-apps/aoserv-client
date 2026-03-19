/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2020, 2021, 2022, 2026  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.ForeignKey;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Static utilities for use in SQL parsing.
 *
 * @author  AO Industries, Inc.
 */
public final class Parser {

  /** Make no instances. */
  private Parser() {
    throw new AssertionError();
  }

  /**
   * Find a match only outside quoted section of the expressions.
   * Quoted by ", with "" being the escape for a " within the quoted section.
   */
  // TODO: Unit tests
  public static int indexOfNotQuoted(String expr, char ch, int fromIndex) {
    boolean quoted = false;
    for (int i = fromIndex, end = expr.length(); i < end; i++) {
      char c = expr.charAt(i);
      if (!quoted && c == ch) {
        return i;
      }
      if (c == '"') {
        // If followed by another quote while quoted, do not unquote
        if (quoted) {
          if (
              i < (end - 1)
                  && expr.charAt(i + 1) == '"'
          ) {
            // Skip
            i++;
          } else {
            // Unquote
            quoted = false;
          }
        } else {
          quoted = true;
        }
      }
    }
    return -1;
  }

  /**
   * Find a match only outside quoted section of the expressions.
   * Quoted by ", with "" being the escape for a " within the quoted section.
   */
  // TODO: Unit tests
  public static int indexOfNotQuoted(String expr, char ch) {
    return indexOfNotQuoted(expr, ch, 0);
  }

  /**
   * Find a match only outside quoted section of the expressions.
   * Quoted by ", with "" being the escape for a " within the quoted section.
   */
  // TODO: Unit tests
  public static int indexOfNotQuoted(String expr, String str, int fromIndex) {
    boolean quoted = false;
    int strLen = str.length();
    for (int i = fromIndex, end = expr.length() - strLen; i < end; i++) {
      char c = expr.charAt(i);
      if (!quoted && expr.regionMatches(i, str, 0, strLen)) {
        return i;
      }
      if (c == '"') {
        // If followed by another quote while quoted, do not unquote
        if (quoted) {
          if (
              i < (end - 1)
                  && expr.charAt(i + 1) == '"'
          ) {
            // Skip
            i++;
          } else {
            // Unquote
            quoted = false;
          }
        } else {
          quoted = true;
        }
      }
    }
    return -1;
  }

  /**
   * Find a match only outside quoted section of the expressions.
   * Quoted by ", with "" being the escape for a " within the quoted section.
   */
  // TODO: Unit tests
  public static int indexOfNotQuoted(String expr, String str) {
    return indexOfNotQuoted(expr, str, 0);
  }

  /**
   * Unquotes a string, removing " characters, except "" being the escape for a " within a quoted section.
   */
  // TODO: Have Aosh only support ' quotes in command line parsing?  This would help with "" quoting for table/columns not being swallowed by bash-style double quotes
  //       This should not hurt since we don't support any variable substitution inside double quotes anyway

  // TODO: Unit tests
  @SuppressWarnings("AssignmentToForLoopParameter")
  public static String unquote(String str) {
    int strLen = str.length();
    StringBuilder unquoted = new StringBuilder(strLen);
    boolean quoted = false;
    for (int i = 0; i < strLen; i++) {
      char c = str.charAt(i);
      if (c == '"') {
        // If followed by another quote while quoted, do not unquote
        if (quoted) {
          if (
              i < (strLen - 1)
                  && str.charAt(i + 1) == '"'
          ) {
            // Is escaped quote
            unquoted.append('"');
            i++;
          } else {
            // Unquote
            quoted = false;
          }
        } else {
          quoted = true;
        }
      } else {
        unquoted.append(c);
      }
    }
    return unquoted.length() == strLen ? str : unquoted.toString();
  }

  /**
   * Quotes a string if needed.  Currently this only when is empty, contains " or .
   * or a character outside normal ASCII range.
   * Unicode is not considered for use without quoting, since this is only used to quote AOServ table/column names, which are all ASCII.
   */
  // TODO: Unit tests
  public static String quote(String str) {
    int strLen = str.length();
    if (strLen == 0) {
      return "\"\"";
    }
    int quotedLen = strLen + 2;
    boolean needsQuote = false;
    for (int i = 0; i < strLen; i++) {
      char c = str.charAt(i);
      if (c <= ' ' || c > '~' || c == '.') {
        needsQuote = true;
      } else if (c == '"') {
        needsQuote = true;
        quotedLen++;
      }
    }
    if (needsQuote) {
      char[] quoted = new char[quotedLen];
      int quotedPos = 0;
      quoted[quotedPos++] = '"';
      for (int i = 0; i < strLen; i++) {
        char c = str.charAt(i);
        quoted[quotedPos++] = c;
        if (c == '"') {
          quoted[quotedPos++] = '"';
        }
      }
      quoted[quotedPos++] = '"';
      assert quotedPos == quotedLen;
      return new String(quoted);
    } else {
      return str;
    }
  }

  // TODO: Unit tests
  public static SqlExpression parseSqlExpression(AoservConnector connector, Table table, String expr)
      throws SQLException, IOException, IllegalArgumentException {
    // count(*)
    if (expr.equals(SqlCount.COUNT_FUNCTION)) {
      return new SqlCount(connector);
    }
    int joinPos = indexOfNotQuoted(expr, '.');
    if (joinPos == -1) {
      joinPos = expr.length();
    }
    int castPos = indexOfNotQuoted(expr, SqlCast.CAST_SEPARATOR);
    if (castPos == -1) {
      castPos = expr.length();
    }
    int columnNameEnd = Math.min(joinPos, castPos);
    String columnName = unquote(expr.substring(0, columnNameEnd));
    Column lastColumn = table.getSchemaColumn(connector, columnName);
    if (lastColumn == null) {
      throw new IllegalArgumentException("Unable to find column: " + quote(table.getName()) + '.' + quote(columnName));
    }

    SqlExpression sql = new SqlColumnValue(connector, lastColumn);
    expr = expr.substring(columnNameEnd);

    while (!expr.isEmpty()) {
      if (expr.charAt(0) == '.') {
        List<ForeignKey> keys = lastColumn.getReferences(connector);
        if (keys.size() != 1) {
          throw new IllegalArgumentException("Column " + quote(lastColumn.getTable(connector).getName()) + '.'
              + quote(lastColumn.getName()) + " should reference precisely one column, references " + keys.size());
        }

        joinPos = indexOfNotQuoted(expr, '.', 1);
        if (joinPos == -1) {
          joinPos = expr.length();
        }
        castPos = indexOfNotQuoted(expr, SqlCast.CAST_SEPARATOR, 1);
        if (castPos == -1) {
          castPos = expr.length();
        }
        int joinNameEnd = Math.min(joinPos, castPos);
        columnName = unquote(expr.substring(1, joinNameEnd));
        Column keyColumn = keys.get(0).getForeignColumn(connector);
        Table valueTable = keyColumn.getTable(connector);
        Column valueColumn = valueTable.getSchemaColumn(connector, columnName);
        if (valueColumn == null) {
          throw new IllegalArgumentException("Unable to find column: " + quote(valueTable.getName()) + '.' + quote(columnName) + " referenced from " + quote(table.getName()));
        }

        sql = new SqlColumnJoin(connector, sql, keyColumn, valueColumn);
        expr = expr.substring(joinNameEnd);

        lastColumn = valueColumn;
      } else if (expr.charAt(0) == ':' && expr.length() >= 2 && expr.charAt(1) == ':') {
        joinPos = indexOfNotQuoted(expr, '.', 2);
        if (joinPos == -1) {
          joinPos = expr.length();
        }
        castPos = indexOfNotQuoted(expr, SqlCast.CAST_SEPARATOR, 2);
        if (castPos == -1) {
          castPos = expr.length();
        }
        int typeNameEnd = Math.min(joinPos, castPos);
        String typeName = unquote(expr.substring(2, typeNameEnd));
        Type type = connector.getSchema().getType().get(typeName);
        if (type == null) {
          throw new IllegalArgumentException("Unable to find SchemaType: " + quote(typeName));
        }

        sql = new SqlCast(sql, type);
        expr = expr.substring(typeNameEnd);
      } else {
        throw new IllegalArgumentException("Parse error: Unable to parse: " + expr);
      }
    }
    return sql;
  }

  /**
   * Parses a {@link Command#SELECT} command.
   *
   * @param args  The first argument must be {@link Command#SELECT}, case-insenstive.
   */
  // TODO: Unit tests
  public static SqlSelect parseSqlSelect(AoservConnector connector, String... args)
      throws SQLException, IOException, IllegalArgumentException {
    final int argsLen = args.length;
    final int minArgs =
        1 // SELECT
        + 1 // expression
        + 1 // FROM
        + 1; // table
    if (argsLen < minArgs) {
      throw new IllegalArgumentException("Parse error: not enough parameters");
    }
    if (!args[0].equalsIgnoreCase(Command.SELECT)) {
      throw new IllegalArgumentException("Parse error: first argument must be " + Command.SELECT + ", case-insensitive: " + args[0]);
    }
    // Parse the list of expressions until "FROM" is found
    List<String> expressionArgs = new ArrayList<>();
    int i = 1;
    for (; i < argsLen; i++) {
      String arg = args[i];
      if (SqlSelect.FROM.equalsIgnoreCase(arg)) {
        break;
      }
      expressionArgs.add(arg);
    }
    if (i >= argsLen - 1) {
      // The "FROM" was not found
      throw new IllegalArgumentException("Parse error: parameter not found: " + SqlSelect.FROM);
    }
    i++; // Move i past "FROM"

    // Get the table name
    String tableName = unquote(args[i++]);
    Table table = connector.getSchema().getTable().get(tableName);
    if (table == null) {
      throw new IllegalArgumentException("Parse error: table not found: " + quote(tableName));
    }

    List<SqlExpression> valueExpressions = new ArrayList<>(expressionArgs.size());
    // Substitute any * columnName and ,
    for (String expressionArg : expressionArgs) {
      String remaining = expressionArg;
      do {
        String current;
        int commaPos = indexOfNotQuoted(remaining, ',');
        if (commaPos == -1) {
          current = remaining;
          remaining = "";
        } else {
          current = remaining.substring(0, commaPos);
          remaining = remaining.substring(commaPos + 1);
        }
        if ("*".equals(current)) {
          for (Column column : table.getSchemaColumns(connector)) {
            valueExpressions.add(parseSqlExpression(connector, table, quote(column.getName())));
          }
        } else {
          valueExpressions.add(parseSqlExpression(connector, table, current));
        }
      } while (!remaining.isEmpty());
    }

    // Parse any ORDER BY clause
    List<SqlOrderByExpression> orderBy = new ArrayList<>();
    if (i < argsLen) {
      String arg = args[i++];
      if (!SqlSelect.ORDER.equalsIgnoreCase(arg)) {
        throw new IllegalArgumentException("Parse error: '" + SqlSelect.ORDER + "' expected, found '" + arg + '\'');
      }
      if (i >= argsLen || !SqlSelect.BY.equalsIgnoreCase(args[i++])) {
        throw new IllegalArgumentException("Parse error: '" + SqlSelect.BY + "' expected");
      }
      while (i < argsLen) {
        String remaining = args[i++];
        do {
          String current;
          int commaPos = indexOfNotQuoted(remaining, ',');
          if (commaPos == -1) {
            current = remaining;
            remaining = "";
          } else {
            current = remaining.substring(0, commaPos);
            remaining = remaining.substring(commaPos + 1);
          }
          if (
              !orderBy.isEmpty()
                  && (
                  SqlOrderByExpression.ASC.equalsIgnoreCase(current)
                      || SqlOrderByExpression.ASCENDING.equalsIgnoreCase(current)
                )
          ) {
            int setAt = orderBy.size() - 1;
            orderBy.set(setAt, new SqlOrderByExpression(orderBy.get(setAt).getExpression(), AoservTable.ASCENDING));
          } else if (
              !orderBy.isEmpty()
                  && (
                  SqlOrderByExpression.DESC.equalsIgnoreCase(current)
                      || SqlOrderByExpression.DESCENDING.equalsIgnoreCase(current)
                )
          ) {
            int setAt = orderBy.size() - 1;
            orderBy.set(setAt, new SqlOrderByExpression(orderBy.get(setAt).getExpression(), AoservTable.DESCENDING));
          } else { // if (!expr.isEmpty()) {
            orderBy.add(new SqlOrderByExpression(parseSqlExpression(connector, table, current), AoservTable.ASCENDING));
          }
        } while (!remaining.isEmpty());
      }
      if (orderBy.isEmpty()) {
        throw new IllegalArgumentException("Parse error: no expressions listed after '" + SqlSelect.ORDER + " " + SqlSelect.BY + "'");
      }
    }

    return new SqlSelect(valueExpressions, table, orderBy);
  }
}
