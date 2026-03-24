/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025, 2026  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.sql.Parser;
import com.aoindustries.aoserv.client.sql.SqlSelect;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

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
   * @see  TableTable#get(java.lang.Object)
   */
  @Override
  public Table get(int tableId) throws IOException, SQLException {
    return getRows().get(tableId);
  }

  /**
   * @see  TableTable#get(java.lang.Object)
   */
  public Table get(String name) throws IOException, SQLException {
    return getUniqueRow(Table.COLUMN_NAME, name);
  }

  /**
   * @see  TableTable#get(java.lang.Object)
   */
  public Table get(Table.TableId tableId) throws IOException, SQLException {
    return get(tableId.ordinal());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SCHEMA_TABLES;
  }

  @Override
  public boolean handleCommand(String[] rawArgs, String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws SQLException, IOException {
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
      // Parse
      SqlSelect sqlSelect;
      try {
        // Use rawArgs instead of args when available
        sqlSelect = Parser.parseSqlSelect(connector, (rawArgs != null ? rawArgs : args));
      } catch (IllegalArgumentException e) {
        err.println("aosh: " + Command.SELECT + ": " + e.getMessage());
        err.flush();
        return true;
      }
      // Execute
      sqlSelect.execute(connector, out, isInteractive);
      out.flush();
      return true;
    } else if (command.equalsIgnoreCase(Command.SHOW)) {
      int argCount = args.length;
      if (argCount >= 2) {
        if ("tables".equalsIgnoreCase(args[1])) {
          String[] selectCommand = {"select", "name,", "description", "from", "schema_tables"};
          handleCommand(selectCommand, selectCommand, in, out, err, isInteractive);
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
}
