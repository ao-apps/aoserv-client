/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.SimpleAoservClient;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
public final class UserTable extends CachedTableUserNameKey<User> {

  UserTable(AoservConnector connector) {
    super(connector, User.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(User.COLUMN_USERNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public void addMysqlUser(User.Name username) throws IOException, SQLException {
    if (User.isSpecial(username)) {
      throw new SQLException("Refusing to add special MySQL user: " + username);
    }
    connector.requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.MYSQL_USERS,
        username
    );
  }

  @Override
  public User get(User.Name username) throws IOException, SQLException {
    return getUniqueRow(User.COLUMN_USERNAME, username);
  }

  public List<User> getMysqlUsers(Package pack) throws IOException, SQLException {
    Account.Name name = pack.getName();
    List<User> cached = getRows();
    int size = cached.size();
    List<User> matches = new ArrayList<>(size);
    for (int c = 0; c < size; c++) {
      User msu = cached.get(c);
      if (msu.getUsername().getPackage_name().equals(name)) {
        matches.add(msu);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_USERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_MYSQL_USER)) {
      if (Aosh.checkParamCount(Command.ADD_MYSQL_USER, args, 1, err)) {
        connector.getSimpleClient().addMysqlUser(
            Aosh.parseMysqlUserName(args[1], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ARE_MYSQL_USER_PASSWORDS_SET)) {
      if (Aosh.checkParamCount(Command.ARE_MYSQL_USER_PASSWORDS_SET, args, 1, err)) {
        int result = connector.getSimpleClient().areMysqlUserPasswordsSet(
            Aosh.parseMysqlUserName(args[1], "username")
        );
        if (result == PasswordProtected.NONE) {
          out.println("none");
        } else if (result == PasswordProtected.SOME) {
          out.println("some");
        } else if (result == PasswordProtected.ALL) {
          out.println("all");
        } else {
          throw new RuntimeException("Unexpected value for result: " + result);
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_MYSQL_PASSWORD)) {
      if (Aosh.checkParamCount(Command.CHECK_MYSQL_PASSWORD, args, 2, err)) {
        List<PasswordChecker.Result> results = SimpleAoservClient.checkMysqlPassword(
            Aosh.parseMysqlUserName(args[1], "username"),
            args[2]
        );
        if (PasswordChecker.hasResults(results)) {
          PasswordChecker.printResults(results, out);
          out.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_MYSQL_USERNAME)) {
      if (Aosh.checkParamCount(Command.CHECK_MYSQL_USERNAME, args, 1, err)) {
        ValidationResult validationResult = User.Name.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_MYSQL_USERNAME + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_MYSQL_USER)) {
      if (Aosh.checkParamCount(Command.DISABLE_MYSQL_USER, args, 2, err)) {
        out.println(
            connector.getSimpleClient().disableMysqlUser(
                Aosh.parseMysqlUserName(args[1], "username"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_MYSQL_USER)) {
      if (Aosh.checkParamCount(Command.ENABLE_MYSQL_USER, args, 1, err)) {
        connector.getSimpleClient().enableMysqlUser(
            Aosh.parseMysqlUserName(args[1], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_MYSQL_USER)) {
      if (Aosh.checkParamCount(Command.REMOVE_MYSQL_USER, args, 1, err)) {
        connector.getSimpleClient().removeMysqlUser(
            Aosh.parseMysqlUserName(args[1], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_MYSQL_USER_PASSWORD)) {
      if (Aosh.checkParamCount(Command.SET_MYSQL_USER_PASSWORD, args, 2, err)) {
        connector.getSimpleClient().setMysqlUserPassword(
            Aosh.parseMysqlUserName(args[1], "username"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.WAIT_FOR_MYSQL_USER_REBUILD)) {
      if (Aosh.checkParamCount(Command.WAIT_FOR_MYSQL_USER_REBUILD, args, 1, err)) {
        connector.getSimpleClient().waitForMysqlUserRebuild(args[1]);
      }
      return true;
    }
    return false;
  }

  public void waitForRebuild(com.aoindustries.aoserv.client.linux.Server aoServer) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.WAIT_FOR_REBUILD,
        Table.TableId.MYSQL_USERS,
        aoServer.getPkey()
    );
  }
}
