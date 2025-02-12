/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
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
      new OrderBy(UserServer.COLUMN_MYSQL_SERVER_name + '.' + Server.COLUMN_AO_SERVER_name + '.' + com.aoindustries.aoserv.client.linux.Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(UserServer.COLUMN_MYSQL_SERVER_name + '.' + Server.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addMysqlServerUser(final User.Name username, final Server mysqlServer, final String host) throws IOException, SQLException {
    if (User.isSpecial(username)) {
      throw new SQLException("Refusing to add special MySQL user: " + username + " on " + mysqlServer);
    }
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.MYSQL_SERVER_USERS.ordinal());
            out.writeUTF(username.toString());
            out.writeCompressedInt(mysqlServer.getPkey());
            out.writeBoolean(host != null);
            if (host != null) {
              out.writeUTF(host);
            }
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public Integer afterRelease() {
            connector.tablesUpdated(invalidateList);
            return pkey;
          }
        }
    );
  }

  @Override
  public UserServer get(int pkey) throws IOException, SQLException {
    return getUniqueRow(UserServer.COLUMN_PKEY, pkey);
  }

  UserServer getMysqlServerUser(User.Name username, Server ms) throws IOException, SQLException {
    int msPkey = ms.getPkey();

    List<UserServer> table = getRows();
    int size = table.size();
    for (int c = 0; c < size; c++) {
      UserServer msu = table.get(c);
      if (msu.getMysqlServer_id() == msPkey && msu.getMysqlUser_username().equals(username)) {
        return msu;
      }
    }
    return null;
  }

  List<UserServer> getMysqlServerUsers(User mu) throws IOException, SQLException {
    return getIndexedRows(UserServer.COLUMN_USERNAME, mu.getUsername_id());
  }

  List<UserServer> getMysqlServerUsers(Server ms) throws IOException, SQLException {
    return getIndexedRows(UserServer.COLUMN_MYSQL_SERVER, ms.getBind_id());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_SERVER_USERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_MYSQL_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.ADD_MYSQL_SERVER_USER, args, 4, err)) {
        out.println(
            connector.getSimpleClient().addMysqlServerUser(
                Aosh.parseMysqlUserName(args[1], "username"),
                Aosh.parseMysqlServerName(args[2], "mysql_server"),
                args[3],
                args[4]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_MYSQL_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.DISABLE_MYSQL_SERVER_USER, args, 4, err)) {
        out.println(
            connector.getSimpleClient().disableMysqlServerUser(
                Aosh.parseMysqlUserName(args[1], "username"),
                Aosh.parseMysqlServerName(args[2], "mysql_server"),
                args[3],
                args[4]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_MYSQL_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.ENABLE_MYSQL_SERVER_USER, args, 3, err)) {
        connector.getSimpleClient().enableMysqlServerUser(
            Aosh.parseMysqlUserName(args[1], "username"),
            Aosh.parseMysqlServerName(args[2], "mysql_server"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_MYSQL_SERVER_USER_PASSWORD_SET)) {
      if (Aosh.checkParamCount(Command.IS_MYSQL_SERVER_USER_PASSWORD_SET, args, 3, err)) {
        out.println(
            connector.getSimpleClient().isMysqlServerUserPasswordSet(
                Aosh.parseMysqlUserName(args[1], "username"),
                Aosh.parseMysqlServerName(args[2], "mysql_server"),
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_MYSQL_SERVER_USER)) {
      if (Aosh.checkParamCount(Command.REMOVE_MYSQL_SERVER_USER, args, 3, err)) {
        connector.getSimpleClient().removeMysqlServerUser(
            Aosh.parseMysqlUserName(args[1], "username"),
            Aosh.parseMysqlServerName(args[2], "mysql_server"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_MYSQL_SERVER_USER_PASSWORD)) {
      if (Aosh.checkParamCount(Command.SET_MYSQL_SERVER_USER_PASSWORD, args, 4, err)) {
        connector.getSimpleClient().setMysqlServerUserPassword(
            Aosh.parseMysqlUserName(args[1], "username"),
            Aosh.parseMysqlServerName(args[2], "mysql_server"),
            args[3],
            args[4]
        );
      }
      return true;
    }
    return false;
  }
}
