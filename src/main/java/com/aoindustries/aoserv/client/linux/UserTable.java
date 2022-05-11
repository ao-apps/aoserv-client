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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

  public void addLinuxAccount(
      final com.aoindustries.aoserv.client.account.User usernameObject,
      final Group.Name primaryGroup,
      final User.Gecos name,
      final User.Gecos officeLocation,
      final User.Gecos officePhone,
      final User.Gecos homePhone,
      final String type,
      final PosixPath shell
  ) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.LINUX_ACCOUNTS.ordinal());
            out.writeUTF(usernameObject.getUsername().toString());
            out.writeUTF(primaryGroup.toString());
            out.writeNullUTF(Objects.toString(name, null));
            out.writeNullUTF(Objects.toString(officeLocation, null));
            out.writeNullUTF(Objects.toString(officePhone, null));
            out.writeNullUTF(Objects.toString(homePhone, null));
            out.writeUTF(type);
            out.writeUTF(shell.toString());
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            connector.tablesUpdated(invalidateList);
          }
        }
    );
  }

  @Override
  public User get(User.Name username) throws IOException, SQLException {
    return getUniqueRow(User.COLUMN_USERNAME, username);
  }

  public List<User> getMailAccounts() throws IOException, SQLException {
    List<User> cached = getRows();
    int len = cached.size();
    List<User> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      User linuxAccount = cached.get(c);
      if (linuxAccount.getType().isEmail()) {
        matches.add(linuxAccount);
      }
    }
    return matches;
  }

  public List<User> getMailAccounts(Account business) throws IOException, SQLException {
    Account.Name accounting = business.getName();
    List<User> cached = getRows();
    int len = cached.size();
    List<User> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      User linuxAccount = cached.get(c);
      if (
          linuxAccount.getType().isEmail()
              && linuxAccount.getUsername().getPackage().getAccount_name().equals(accounting)
      ) {
        matches.add(linuxAccount);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.LINUX_ACCOUNTS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_LINUX_ACCOUNT)) {
      if (Aosh.checkParamCount(Command.ADD_LINUX_ACCOUNT, args, 8, err)) {
        connector.getSimpleClient().addLinuxAccount(
            Aosh.parseLinuxUserName(args[1], "username"),
            Aosh.parseGroupName(args[2], "primary_group"),
            args[3].isEmpty() ? null : Aosh.parseGecos(args[3], "full_name"),
            args[4].isEmpty() ? null : Aosh.parseGecos(args[4], "office_location"),
            args[5].isEmpty() ? null : Aosh.parseGecos(args[5], "office_phone"),
            args[6].isEmpty() ? null : Aosh.parseGecos(args[6], "home_phone"),
            args[7],
            Aosh.parseUnixPath(args[8], "shell")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ARE_LINUX_ACCOUNT_PASSWORDS_SET)) {
      if (Aosh.checkParamCount(Command.ARE_LINUX_ACCOUNT_PASSWORDS_SET, args, 1, err)) {
        int result = connector.getSimpleClient().areLinuxAccountPasswordsSet(
            Aosh.parseLinuxUserName(args[1], "username")
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
    } else if (command.equalsIgnoreCase(Command.CHECK_LINUX_ACCOUNT_NAME)) {
      if (Aosh.checkParamCount(Command.CHECK_LINUX_ACCOUNT_NAME, args, 1, err)) {
        ValidationResult validationResult = User.Gecos.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_LINUX_ACCOUNT_NAME + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_LINUX_ACCOUNT_PASSWORD)) {
      if (Aosh.checkParamCount(Command.CHECK_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
        List<PasswordChecker.Result> results = connector.getSimpleClient().checkLinuxAccountPassword(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2]
        );
        if (PasswordChecker.hasResults(results)) {
          PasswordChecker.printResults(results, out);
          out.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_LINUX_ACCOUNT)) {
      if (Aosh.checkParamCount(Command.DISABLE_LINUX_ACCOUNT, args, 2, err)) {
        out.println(
            connector.getSimpleClient().disableLinuxAccount(
                Aosh.parseLinuxUserName(args[1], "username"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_LINUX_ACCOUNT)) {
      if (Aosh.checkParamCount(Command.ENABLE_LINUX_ACCOUNT, args, 1, err)) {
        connector.getSimpleClient().enableLinuxAccount(
            Aosh.parseLinuxUserName(args[1], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GENERATE_PASSWORD)) {
      if (Aosh.checkParamCount(Command.GENERATE_PASSWORD, args, 0, err)) {
        out.println(connector.getSimpleClient().generatePassword());
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_LINUX_ACCOUNT)) {
      if (Aosh.checkParamCount(Command.REMOVE_LINUX_ACCOUNT, args, 1, err)) {
        connector.getSimpleClient().removeLinuxAccount(
            Aosh.parseLinuxUserName(args[1], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_HOME_PHONE)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_HOME_PHONE, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountHomePhone(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2].isEmpty() ? null : Aosh.parseGecos(args[2], "phone_number")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_NAME)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_NAME, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountName(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2].isEmpty() ? null : Aosh.parseGecos(args[2], "full_name")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_OFFICE_LOCATION)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_OFFICE_LOCATION, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountOfficeLocation(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2].isEmpty() ? null : Aosh.parseGecos(args[2], "location")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_OFFICE_PHONE)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_OFFICE_PHONE, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountOfficePhone(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2].isEmpty() ? null : Aosh.parseGecos(args[2], "phone_number")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_PASSWORD)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_PASSWORD, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountPassword(
            Aosh.parseLinuxUserName(args[1], "username"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_LINUX_ACCOUNT_SHELL)) {
      if (Aosh.checkParamCount(Command.SET_LINUX_ACCOUNT_SHELL, args, 2, err)) {
        connector.getSimpleClient().setLinuxAccountShell(
            Aosh.parseLinuxUserName(args[1], "username"),
            Aosh.parseUnixPath(args[2], "shell")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.WAIT_FOR_LINUX_ACCOUNT_REBUILD)) {
      if (Aosh.checkParamCount(Command.WAIT_FOR_LINUX_ACCOUNT_REBUILD, args, 1, err)) {
        connector.getSimpleClient().waitForLinuxAccountRebuild(args[1]);
      }
      return true;
    }
    return false;
  }

  void waitForRebuild(Server aoServer) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.WAIT_FOR_REBUILD,
        Table.TableId.LINUX_ACCOUNTS,
        aoServer.getPkey()
    );
  }
}
