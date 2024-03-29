/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.mysql;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.StreamHandler;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
public final class DatabaseTable extends CachedTableIntegerKey<Database> {

  DatabaseTable(AoservConnector connector) {
    super(connector, Database.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Database.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Database.COLUMN_MYSQL_SERVER_name + '.' + Server.COLUMN_AO_SERVER_name + '.' + com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(Database.COLUMN_MYSQL_SERVER_name + '.' + Server.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addMysqlDatabase(
      Database.Name name,
      Server mysqlServer,
      Package packageObj
  ) throws IOException, SQLException {
    if (Database.isSpecial(name)) {
      throw new SQLException("Refusing to add special MySQL database: " + name + " on " + mysqlServer);
    }
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.MYSQL_DATABASES,
        name,
        mysqlServer.getBind_id(),
        packageObj.getName()
    );
  }

  public Database.Name generateMysqlDatabaseName(String templateBase, String templateAdded) throws IOException, SQLException {
    try {
      return Database.Name.valueOf(connector.requestStringQuery(true, AoservProtocol.CommandId.GENERATE_MYSQL_DATABASE_NAME, templateBase, templateAdded));
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public Database get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Database.COLUMN_PKEY, pkey);
  }

  Database getMysqlDatabase(Database.Name name, Server ms) throws IOException, SQLException {
    // Use index first
    for (Database md : getMysqlDatabases(ms)) {
      if (md.getName().equals(name)) {
        return md;
      }
    }
    return null;
  }

  public List<Database> getMysqlDatabases(Package pack) throws IOException, SQLException {
    return getIndexedRows(Database.COLUMN_PACKAGE, pack.getName());
  }

  List<Database> getMysqlDatabases(Server ms) throws IOException, SQLException {
    return getIndexedRows(Database.COLUMN_MYSQL_SERVER, ms.getBind_id());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_DATABASES;
  }

  @Override
  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_MYSQL_DATABASE)) {
      if (Aosh.checkParamCount(Command.ADD_MYSQL_DATABASE, args, 4, err)) {
        out.println(
            connector.getSimpleClient().addMysqlDatabase(
                Aosh.parseMysqlDatabaseName(args[1], "database_name"),
                Aosh.parseMysqlServerName(args[2], "mysql_server"),
                args[3],
                Aosh.parseAccountingCode(args[4], "package")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_MYSQL_DATABASE_NAME)) {
      if (Aosh.checkParamCount(Command.CHECK_MYSQL_DATABASE_NAME, args, 1, err)) {
        ValidationResult validationResult = Database.Name.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_MYSQL_DATABASE_NAME + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DUMP_MYSQL_DATABASE)) {
      if (Aosh.checkParamCount(Command.DUMP_MYSQL_DATABASE, args, 4, err)) {
        try {
          Database.Name dbName = Aosh.parseMysqlDatabaseName(args[1], "database_name");
          Server.Name serverName = Aosh.parseMysqlServerName(args[2], "mysql_server");
          String aoServer = args[3];
          if (Aosh.parseBoolean(args[4], "gzip")) {
            connector.getSimpleClient().dumpMysqlDatabase(
                dbName,
                serverName,
                aoServer,
                true,
                new StreamHandler() {
                  @Override
                  public void onDumpSize(long dumpSize) {
                    // Do nothing
                  }

                  @Override
                  public OutputStream getOut() {
                    return System.out; // By-pass TerminalWriter stuff to avoid possible encoding issues.
                  }
                }
            );
            System.out.flush();
          } else {
            connector.getSimpleClient().dumpMysqlDatabase(
                dbName,
                serverName,
                aoServer,
                out
            );
            out.flush();
          }
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.DUMP_MYSQL_DATABASE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_MYSQL_DATABASE_NAME)) {
      if (Aosh.checkParamCount(Command.GENERATE_MYSQL_DATABASE_NAME, args, 2, err)) {
        out.println(connector.getSimpleClient().generateMysqlDatabaseName(args[1], args[2]));
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_MYSQL_DATABASE_NAME_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_MYSQL_DATABASE_NAME_AVAILABLE, args, 3, err)) {
        try {
          out.println(
              connector.getSimpleClient().isMysqlDatabaseNameAvailable(
                  Aosh.parseMysqlDatabaseName(args[1], "database_name"),
                  Aosh.parseMysqlServerName(args[2], "mysql_server"),
                  args[3]
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_MYSQL_DATABASE_NAME_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_MYSQL_DATABASE)) {
      if (Aosh.checkParamCount(Command.REMOVE_MYSQL_DATABASE, args, 3, err)) {
        connector.getSimpleClient().removeMysqlDatabase(
            Aosh.parseMysqlDatabaseName(args[1], "database_name"),
            Aosh.parseMysqlServerName(args[2], "mysql_server"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.WAIT_FOR_MYSQL_DATABASE_REBUILD)) {
      if (Aosh.checkParamCount(Command.WAIT_FOR_MYSQL_DATABASE_REBUILD, args, 1, err)) {
        connector.getSimpleClient().waitForMysqlDatabaseRebuild(args[1]);
      }
      return true;
    }
    return false;
  }

  boolean isMysqlDatabaseNameAvailable(Database.Name name, Server mysqlServer) throws IOException, SQLException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandId.IS_MYSQL_DATABASE_NAME_AVAILABLE, name, mysqlServer.getPkey());
  }

  public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.WAIT_FOR_REBUILD,
        Table.TableId.MYSQL_DATABASES,
        aoServer.getPkey()
    );
  }
}
