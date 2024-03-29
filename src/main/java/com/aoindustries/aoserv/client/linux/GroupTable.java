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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationResult;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Group
 *
 * @author  AO Industries, Inc.
 */
public final class GroupTable extends CachedTableGroupNameKey<Group> {

  GroupTable(AoservConnector connector) {
    super(connector, Group.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Group.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public void addLinuxGroup(Group.Name name, Package packageObject, String type) throws IOException, SQLException {
    connector.requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.LINUX_GROUPS,
        name,
        packageObject.getName(),
        type
    );
  }

  @Override
  public Group get(Group.Name name) throws IOException, SQLException {
    return getUniqueRow(Group.COLUMN_NAME, name);
  }

  public List<Group> getLinuxGroups(Package pack) throws IOException, SQLException {
    return getIndexedRows(Group.COLUMN_PACKAGE, pack.getName());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.LINUX_GROUPS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_LINUX_GROUP)) {
      if (Aosh.checkParamCount(Command.ADD_LINUX_GROUP, args, 3, err)) {
        connector.getSimpleClient().addLinuxGroup(
            Aosh.parseGroupName(args[1], "group"),
            Aosh.parseAccountingCode(args[2], "package"),
            args[3]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.CHECK_LINUX_GROUP_NAME)) {
      if (Aosh.checkParamCount(Command.CHECK_LINUX_GROUP_NAME, args, 1, err)) {
        ValidationResult validationResult = Group.Name.validate(args[1]);
        out.println(validationResult.isValid());
        out.flush();
        if (!validationResult.isValid()) {
          err.print("aosh: " + Command.CHECK_LINUX_GROUP_NAME + ": ");
          err.println(validationResult.toString());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.IS_LINUX_GROUP_NAME_AVAILABLE)) {
      if (Aosh.checkParamCount(Command.IS_LINUX_GROUP_NAME_AVAILABLE, args, 1, err)) {
        try {
          out.println(
              connector.getSimpleClient().isLinuxGroupNameAvailable(
                  Aosh.parseGroupName(args[1], "groupname")
              )
          );
          out.flush();
        } catch (IllegalArgumentException iae) {
          err.print("aosh: " + Command.IS_LINUX_GROUP_NAME_AVAILABLE + ": ");
          err.println(iae.getMessage());
          err.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_LINUX_GROUP)) {
      if (Aosh.checkParamCount(Command.REMOVE_LINUX_GROUP, args, 1, err)) {
        connector.getSimpleClient().removeLinuxGroup(
            Aosh.parseGroupName(args[1], "group")
        );
      }
      return true;
    }
    return false;
  }

  public boolean isLinuxGroupNameAvailable(Group.Name groupname) throws SQLException, IOException {
    return connector.requestBooleanQuery(true, AoservProtocol.CommandId.IS_LINUX_GROUP_NAME_AVAILABLE, groupname);
  }
}
