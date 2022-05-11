/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class UserServerTable extends CachedTableIntegerKey<UserServer> {

  UserServerTable(AoservConnector connector) {
    super(connector, UserServer.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(UserServer.COLUMN_USERNAME_name, ASCENDING),
      new OrderBy(UserServer.COLUMN_POSTGRES_SERVER_name + '.' + Server.COLUMN_NAME_name, ASCENDING),
      new OrderBy(UserServer.COLUMN_POSTGRES_SERVER_name + '.' + Server.COLUMN_AO_SERVER_name + '.' + com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addPostgresServerUser(User.Name username, Server postgresServer) throws IOException, SQLException {
    if (User.isSpecial(username)) {
      throw new SQLException("Refusing to add special PostgreSQL user: " + username + " on " + postgresServer);
    }
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.POSTGRES_SERVER_USERS,
        username,
        postgresServer.getBind_id()
    );
  }

  @Override
  public UserServer get(int pkey) throws IOException, SQLException {
    return getUniqueRow(UserServer.COLUMN_PKEY, pkey);
  }

  UserServer getPostgresServerUser(User.Name username, Server postgresServer) throws IOException, SQLException {
    return getPostgresServerUser(username, postgresServer.getBind_id());
  }

  UserServer getPostgresServerUser(User.Name username, int postgresServer) throws IOException, SQLException {
    List<UserServer> table = getRows();
    int size = table.size();
    for (int c = 0; c < size; c++) {
      UserServer psu = table.get(c);
      if (
          psu.getPostgresUser_username().equals(username)
              && psu.getPostgresServer_bind_id() == postgresServer
      ) {
        return psu;
      }
    }
    return null;
  }

  List<UserServer> getPostgresServerUsers(User pu) throws IOException, SQLException {
    return getIndexedRows(UserServer.COLUMN_USERNAME, pu.getUsername_username_id());
  }

  List<UserServer> getPostgresServerUsers(Server postgresServer) throws IOException, SQLException {
    return getIndexedRows(UserServer.COLUMN_POSTGRES_SERVER, postgresServer.getBind_id());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.POSTGRES_SERVER_USERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_POSTGRES_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.ADD_POSTGRES_SERVER_USER, args, 3, err)) {
        out.println(
            connector.getSimpleClient().addPostgresServerUser(
                Aosh.parsePostgresUserName(args[1], "username"),
                Aosh.parsePostgresServerName(args[2], "postgres_server"),
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_POSTGRES_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.DISABLE_POSTGRES_SERVER_USER, args, 4, err)) {
        out.println(
            connector.getSimpleClient().disablePostgresServerUser(
                Aosh.parsePostgresUserName(args[1], "username"),
                Aosh.parsePostgresServerName(args[2], "postgres_server"),
                args[3],
                args[4]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_POSTGRES_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.ENABLE_POSTGRES_SERVER_USER, args, 3, err)) {
        connector.getSimpleClient().enablePostgresServerUser(
            Aosh.parsePostgresUserName(args[1], "username"),
            Aosh.parsePostgresServerName(args[2], "postgres_server"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_POSTGRES_SERVER_USER_PASSWORD_SET)) {
      if (Aosh.checkParamCount(Command.IS_POSTGRES_SERVER_USER_PASSWORD_SET, args, 2, err)) {
        out.println(
            connector.getSimpleClient().isPostgresServerUserPasswordSet(
                Aosh.parsePostgresUserName(args[1], "username"),
                Aosh.parsePostgresServerName(args[2], "postgres_server"),
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_POSTGRES_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.REMOVE_POSTGRES_SERVER_USER, args, 3, err)) {
        connector.getSimpleClient().removePostgresServerUser(
            Aosh.parsePostgresUserName(args[1], "username"),
            Aosh.parsePostgresServerName(args[2], "postgres_server"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_POSTGRES_SERVER_USER_PASSWORD)) {
      if (Aosh.checkParamCount(Command.SET_POSTGRES_SERVER_USER_PASSWORD, args, 4, err)) {
        connector.getSimpleClient().setPostgresServerUserPassword(
            Aosh.parsePostgresUserName(args[1], "username"),
            Aosh.parsePostgresServerName(args[2], "postgres_server"),
            args[3],
            args[4]
        );
      }
      return true;
    }
    return false;
  }
}
