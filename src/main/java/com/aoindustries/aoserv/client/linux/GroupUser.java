/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each <code>LinuxGroup</code> may be accessed by any number
 * of <code>LinuxAccount</code>s.  The accounts are granted access
 * to a group via a <code>LinuxGroupAccount</code>.  One account
 * may access a maximum of 31 different groups.  Also, a
 * <code>LinuxAccount</code> must have one and only one primary
 * <code>LinuxGroupAccount</code>.
 *
 * @see  User
 * @see  Group
 *
 * @author  AO Industries, Inc.
 */
public final class GroupUser extends CachedObjectIntegerKey<GroupUser> implements Removable {

  static final int COLUMN_ID = 0;
  static final String COLUMN_GROUP_name = "group";
  static final String COLUMN_USER_name = "user";

  /**
   * The maximum number of groups allowed for one account.
   *
   * <pre>/usr/include/linux/limits.h:#define NGROUPS_MAX    65536</pre>
   */
  public static final int MAX_GROUPS = 65536;

  private Group.Name group;
  private User.Name user;
  private boolean isPrimary;
  private int operatingSystemVersion;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public GroupUser() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID: return pkey;
      case 1: return group;
      case 2: return user;
      case 3: return isPrimary;
      case 4: return operatingSystemVersion == -1 ? null : operatingSystemVersion;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getId() {
    return pkey;
  }

  public Group.Name getGroup_name() {
    return group;
  }

  public Group getGroup() throws SQLException, IOException {
    Group groupNameObject = table.getConnector().getLinux().getGroup().get(group);
    if (groupNameObject == null) {
      throw new SQLException("Unable to find LinuxGroup: " + group);
    }
    return groupNameObject;
  }

  public User.Name getUser_username() {
    return user;
  }

  public User getUser() throws SQLException, IOException {
    User usernameObject = table.getConnector().getLinux().getUser().get(user);
    if (usernameObject == null) {
      throw new SQLException("Unable to find LinuxAccount: " + user);
    }
    return usernameObject;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  public Integer getOperatingSystemVersion_pkey() {
    return operatingSystemVersion == -1 ? null : operatingSystemVersion;
  }

  public OperatingSystemVersion getOperatingSystemVersion() throws SQLException, IOException {
    if (operatingSystemVersion == -1) {
      return null;
    }
    OperatingSystemVersion osv = table.getConnector().getDistribution().getOperatingSystemVersion().get(operatingSystemVersion);
    if (osv == null) {
      throw new SQLException("Unable to find OperatingSystemVersion: " + operatingSystemVersion);
    }
    return osv;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      group = Group.Name.valueOf(result.getString(pos++));
      user = User.Name.valueOf(result.getString(pos++));
      isPrimary = result.getBoolean(pos++);
      operatingSystemVersion = result.getInt(pos++);
      if (result.wasNull()) {
        operatingSystemVersion = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      group = Group.Name.valueOf(in.readUTF()).intern();
      user = User.Name.valueOf(in.readUTF()).intern();
      isPrimary = in.readBoolean();
      operatingSystemVersion = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(group.toString());
    out.writeUTF(user.toString());
    out.writeBoolean(isPrimary);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
      out.writeCompressedInt(operatingSystemVersion);
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.LINUX_GROUP_ACCOUNTS;
  }

  @Override
  public List<CannotRemoveReason<GroupUser>> getCannotRemoveReasons() {
    List<CannotRemoveReason<GroupUser>> reasons=new ArrayList<>();
    if (isPrimary) {
      reasons.add(new CannotRemoveReason<>("Not allowed to drop a primary group", this));
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(
      true,
      AoservProtocol.CommandID.REMOVE,
      Table.TableID.LINUX_GROUP_ACCOUNTS,
      pkey
    );
  }

  void setAsPrimary() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(
      true,
      AoservProtocol.CommandID.SET_PRIMARY_LINUX_GROUP_ACCOUNT,
      pkey
    );
  }

  @Override
  public String toStringImpl() {
    return group.toString()+'|'+user.toString()+(isPrimary?"|p":"|a");
  }
}
