/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.postgresql;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class ServerTable extends CachedTableIntegerKey<Server> {

  ServerTable(AoservConnector connector) {
    super(connector, Server.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Server.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Server.COLUMN_AO_SERVER_name + '.' + com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addPostgresServer(
      Server.Name name,
      com.aoindustries.aoserv.client.linux.Server aoServer,
      Version version,
      int maxConnections,
      int sortMem,
      int sharedBuffers,
      boolean fsync
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.POSTGRES_SERVERS,
        name,
        aoServer.getPkey(),
        version.getPkey(),
        maxConnections,
        sortMem,
        sharedBuffers,
        fsync
    );
  }

  @Override
  public Server get(int bind) throws IOException, SQLException {
    return getUniqueRow(Server.COLUMN_BIND, bind);
  }

  public Server getPostgresServer(Bind nb) throws IOException, SQLException {
    return getUniqueRow(Server.COLUMN_BIND, nb.getId());
  }

  public List<Server> getPostgresServers(com.aoindustries.aoserv.client.linux.Server ao) throws IOException, SQLException {
    return getIndexedRows(Server.COLUMN_AO_SERVER, ao.getPkey());
  }

  public Server getPostgresServer(Server.Name name, com.aoindustries.aoserv.client.linux.Server ao) throws IOException, SQLException {
    // Use the index first
    List<Server> table = getPostgresServers(ao);
    int size = table.size();
    for (int c = 0; c < size; c++) {
      Server ps = table.get(c);
      if (ps.getName().equals(name)) {
        return ps;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.POSTGRES_SERVERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.CHECK_POSTGRES_SERVER_NAME)) {
      if (Aosh.checkParamCount(Command.CHECK_POSTGRES_SERVER_NAME, args, 1, err)) {
        ValidationResult validationResult = Server.Name.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_POSTGRES_SERVER_NAME + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_POSTGRES_SERVER_NAME_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_POSTGRES_SERVER_NAME_AVAILABLE, args, 2, err)) {
        try {
          out.println(
              connector.getSimpleClient().isPostgresServerNameAvailable(
                  Aosh.parsePostgresServerName(args[1], "server_name"),
                  args[2]
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_POSTGRES_SERVER_NAME_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.RESTART_POSTGRESQL)) {
      if (Aosh.checkParamCount(Command.RESTART_POSTGRESQL, args, 2, err)) {
        connector.getSimpleClient().restartPostgresql(
            Aosh.parsePostgresServerName(args[1], "postgres_server"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.START_POSTGRESQL)) {
      if (Aosh.checkParamCount(Command.START_POSTGRESQL, args, 2, err)) {
        connector.getSimpleClient().startPostgresql(
            Aosh.parsePostgresServerName(args[1], "postgres_server"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.STOP_POSTGRESQL)) {
      if (Aosh.checkParamCount(Command.STOP_POSTGRESQL, args, 2, err)) {
        connector.getSimpleClient().stopPostgresql(
            Aosh.parsePostgresServerName(args[1], "postgres_server"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.WAIT_FOR_POSTGRES_SERVER_REBUILD)) {
      if (Aosh.checkParamCount(Command.WAIT_FOR_POSTGRES_SERVER_REBUILD, args, 1, err)) {
        connector.getSimpleClient().waitForPostgresServerRebuild(args[1]);
      }
      return true;
    }
    return false;
  }

  public boolean isPostgresServerNameAvailable(Server.Name name, com.aoindustries.aoserv.client.linux.Server ao) throws IOException, SQLException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandId.IS_POSTGRES_SERVER_NAME_AVAILABLE, name, ao.getPkey());
  }

  public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.WAIT_FOR_REBUILD,
        Table.TableId.POSTGRES_SERVERS,
        aoServer.getPkey()
    );
  }
}
