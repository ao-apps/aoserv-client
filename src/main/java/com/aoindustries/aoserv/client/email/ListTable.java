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

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @see  List
 *
 * @author  AO Industries, Inc.
 */
public final class ListTable extends CachedTableIntegerKey<List> {

  ListTable(AoservConnector connector) {
    super(connector, List.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(List.COLUMN_PATH_name, ASCENDING),
      new OrderBy(List.COLUMN_LINUX_SERVER_ACCOUNT_name + '.' + UserServer.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addEmailList(
      PosixPath path,
      UserServer lsa,
      GroupServer lsg
  ) throws IllegalArgumentException, IOException, SQLException {
    Server lsaServer = lsa.getServer();
    Server lsgServer = lsg.getServer();
    if (!lsaServer.equals(lsgServer)) {
      throw new IllegalArgumentException("Mismatched servers: " + lsaServer + " and " + lsgServer);
    }
    if (
        !List.isValidRegularPath(
            path,
            lsaServer.getHost().getOperatingSystemVersion_id()
        )
    ) {
      throw new IllegalArgumentException("Invalid list path: " + path);
    }

    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.EMAIL_LISTS,
        path,
        lsa.getPkey(),
        lsg.getPkey()
    );
  }

  @Override
  public List get(int pkey) throws IOException, SQLException {
    return getUniqueRow(List.COLUMN_PKEY, pkey);
  }

  public java.util.List<List> getEmailLists(Account business) throws IOException, SQLException {
    Account.Name accounting = business.getName();
    java.util.List<List> cached = getRows();
    int len = cached.size();
    java.util.List<List> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      List list = cached.get(c);
      if (
          list
              .getLinuxServerGroup()
              .getLinuxGroup()
              .getPackage()
              .getAccount_name()
              .equals(accounting)
      ) {
        matches.add(list);
      }
    }
    return matches;
  }

  public java.util.List<List> getEmailLists(Package pack) throws IOException, SQLException {
    Account.Name packName = pack.getName();

    java.util.List<List> cached = getRows();
    int size = cached.size();
    java.util.List<List> matches = new ArrayList<>(size);
    for (int c = 0; c < size; c++) {
      List list = cached.get(c);
      if (list.getLinuxServerGroup().getLinuxGroup().getPackage_name().equals(packName)) {
        matches.add(list);
      }
    }
    return matches;
  }

  public java.util.List<List> getEmailLists(UserServer lsa) throws IOException, SQLException {
    return getIndexedRows(List.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
  }

  public List getEmailList(Server ao, PosixPath path) throws IOException, SQLException {
    int aoPkey = ao.getServer_pkey();
    java.util.List<List> cached = getRows();
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      List list = cached.get(c);
      if (list.getLinuxServerGroup().getServer_host_id() == aoPkey && list.getPath().equals(path)) {
        return list;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_LISTS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.ADD_EMAIL_LIST, args, 4, err)) {
        out.println(
            connector.getSimpleClient().addEmailList(
                args[1],
                Aosh.parseUnixPath(args[2], "path"),
                Aosh.parseLinuxUserName(args[3], "username"),
                Aosh.parseGroupName(args[4], "group")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_EMAIL_LIST_PATH)) {
      if (Aosh.checkParamCount(Command.CHECK_EMAIL_LIST_PATH, args, 2, err)) {
        try {
          connector.getSimpleClient().checkEmailListPath(
              args[1],
              Aosh.parseUnixPath(args[2], "path")
          );
          out.print(args[2]);
          out.print(": ");
          out.println("true");
        } catch (IllegalArgumentException ia) {
          out.print(args[2]);
          out.print(": ");
          out.println(ia.getMessage());
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.DISABLE_EMAIL_LIST, args, 3, err)) {
        out.println(
            connector.getSimpleClient().disableEmailList(
                Aosh.parseUnixPath(args[1], "path"),
                args[2],
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.ENABLE_EMAIL_LIST, args, 2, err)) {
        connector.getSimpleClient().enableEmailList(
            Aosh.parseUnixPath(args[1], "path"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GET_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.GET_EMAIL_LIST, args, 2, err)) {
        out.println(
            connector.getSimpleClient().getEmailListAddressList(
                Aosh.parseUnixPath(args[1], "path"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.REMOVE_EMAIL_LIST, args, 2, err)) {
        connector.getSimpleClient().removeEmailList(
            Aosh.parseUnixPath(args[1], "path"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_EMAIL_LIST)) {
      if (Aosh.checkParamCount(Command.SET_EMAIL_LIST, args, 3, err)) {
        connector.getSimpleClient().setEmailListAddressList(
            Aosh.parseUnixPath(args[1], "path"),
            args[2],
            args[3]
        );
      }
      return true;
    }
    return false;
  }
}
