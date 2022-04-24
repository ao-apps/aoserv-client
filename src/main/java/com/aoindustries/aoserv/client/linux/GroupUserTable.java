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

import com.aoapps.collections.AoCollections;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.util.Tuple2;
import com.aoapps.lang.NullArgumentException;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  GroupUser
 *
 * @author  AO Industries, Inc.
 */
public final class GroupUserTable extends CachedTableIntegerKey<GroupUser> {

  private boolean hashBuilt = false;
  private final Map<Tuple2<Group.Name, User.Name>, List<GroupUser>> hash = new HashMap<>();

  /**
   * The group name of the primary group is hashed on first use for fast
   * lookups.
   */
  private boolean primaryHashBuilt = false;
  private final Map<User.Name, Group.Name> primaryHash = new HashMap<>();

  GroupUserTable(AOServConnector connector) {
    super(connector, GroupUser.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(GroupUser.COLUMN_GROUP_name, ASCENDING),
      new OrderBy(GroupUser.COLUMN_USER_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addLinuxGroupAccount(
      Group groupObject,
      User userObject
  ) throws IOException, SQLException {
    int pkey = connector.requestIntQueryIL(
        true,
        AoservProtocol.CommandID.ADD,
        Table.TableID.LINUX_GROUP_ACCOUNTS,
        groupObject.getName(),
        userObject.getUsername()
    );
    return pkey;
  }

  @Override
  public GroupUser get(int id) throws IOException, SQLException {
    return getUniqueRow(GroupUser.COLUMN_ID, id);
  }

  public List<GroupUser> getLinuxGroupAccounts(
      Group.Name group,
      User.Name user
  ) throws IOException, SQLException {
    synchronized (hash) {
      if (!hashBuilt) {
        hash.clear();
        for (GroupUser lga : getRows()) {
          Tuple2<Group.Name, User.Name> key = new Tuple2<>(lga.getGroup_name(), lga.getUser_username());
          List<GroupUser> list = hash.get(key);
          if (list == null) {
            hash.put(key, list = new ArrayList<>());
          }
          list.add(lga);
        }
        // Make entries unmodifiable
        for (Map.Entry<Tuple2<Group.Name, User.Name>, List<GroupUser>> entry : hash.entrySet()) {
          entry.setValue(
              AoCollections.optimalUnmodifiableList(entry.getValue())
          );
        }
        hashBuilt = true;
      }
      List<GroupUser> lgas = hash.get(new Tuple2<>(group, user));
      if (lgas == null) {
        return Collections.emptyList();
      }
      return lgas;
    }
  }

  List<Group> getLinuxGroups(User linuxAccount) throws IOException, SQLException {
    User.Name username = linuxAccount.getUsername_id();
    List<GroupUser> rows = getRows();
    int len = rows.size();
    List<Group> matches = new ArrayList<>(GroupUser.MAX_GROUPS);
    for (int c = 0; c < len; c++) {
      GroupUser lga = rows.get(c);
      if (lga.getUser_username().equals(username)) {
        Group lg = lga.getGroup();
        // Avoid duplicates that are now possible due to operating_system_version
        if (!matches.contains(lg)) {
          matches.add(lg);
        }
      }
    }
    return matches;
  }

  Group getPrimaryGroup(User account) throws IOException, SQLException {
    NullArgumentException.checkNotNull(account, "account");
    synchronized (primaryHash) {
      // Rebuild the hash if needed
      if (!primaryHashBuilt) {
        List<GroupUser> cache = getRows();
        primaryHash.clear();
        int len = cache.size();
        for (int c = 0; c < len; c++) {
          GroupUser lga = cache.get(c);
          if (lga.isPrimary()) {
            User.Name username = lga.getUser_username();
            Group.Name groupName = lga.getGroup_name();
            Group.Name existing = primaryHash.put(username, groupName);
            if (
                existing != null
                    && !existing.equals(groupName)
            ) {
              throw new SQLException(
                  "Conflicting primary groups for "
                      + username
                      + ": "
                      + existing
                      + " and "
                      + groupName
              );
            }
          }
        }
        primaryHashBuilt = true;
      }
      Group.Name groupName = primaryHash.get(account.getUsername_id());
      if (groupName == null) {
        return null;
      }
      // May be filtered
      return connector.getLinux().getGroup().get(groupName);
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.LINUX_GROUP_ACCOUNTS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_LINUX_GROUP_ACCOUNT)) {
      if (AOSH.checkParamCount(Command.ADD_LINUX_GROUP_ACCOUNT, args, 2, err)) {
        out.println(
            connector.getSimpleAOClient().addLinuxGroupAccount(
                AOSH.parseGroupName(args[1], "group"),
                AOSH.parseLinuxUserName(args[2], "username")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_LINUX_GROUP_ACCOUNT)) {
      if (AOSH.checkParamCount(Command.REMOVE_LINUX_GROUP_ACCOUNT, args, 2, err)) {
        connector.getSimpleAOClient().removeLinuxGroupAccount(
            AOSH.parseGroupName(args[1], "group"),
            AOSH.parseLinuxUserName(args[2], "username")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_PRIMARY_LINUX_GROUP_ACCOUNT)) {
      if (AOSH.checkParamCount(Command.SET_PRIMARY_LINUX_GROUP_ACCOUNT, args, 2, err)) {
        connector.getSimpleAOClient().setPrimaryLinuxGroupAccount(
            AOSH.parseGroupName(args[1], "group"),
            AOSH.parseLinuxUserName(args[2], "username")
        );
      }
      return true;
    }
    return false;
  }

  @Override
  public void clearCache() {
    super.clearCache();
    synchronized (hash) {
      hashBuilt = false;
    }
    synchronized (primaryHash) {
      primaryHashBuilt = false;
    }
  }
}
