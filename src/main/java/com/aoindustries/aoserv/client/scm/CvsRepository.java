/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.scm;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>CvsRepository</code> represents on repository directory for the CVS pserver.
 *
 * @author  AO Industries, Inc.
 */
public final class CvsRepository extends CachedObjectIntegerKey<CvsRepository> implements Removable, Disablable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_LINUX_SERVER_ACCOUNT = 2;
  static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
  static final String COLUMN_PATH_name = "path";

  /**
   * The default directory containing CVS repositories.
   */
  public static final PosixPath DEFAULT_CVS_DIRECTORY;

  static {
    try {
      DEFAULT_CVS_DIRECTORY = PosixPath.valueOf("/var/cvs").intern();
    } catch (ValidationException e) {
      throw new AssertionError("These hard-coded values are valid", e);
    }
  }

  /**
   * The default permissions for a CVS repository.
   */
  public static final long DEFAULT_MODE = 02770;

  public static long[] getValidModes() {
    return new long[]{
        0700,
        0750,
        0770,
        0755,
        0775,
        DEFAULT_MODE,
        03770
    };
  }

  /**
   * Allowed CVS repository paths are constrained beyond the general
   * requirements of {@link PosixPath}.
   * May only contain characters in the set:
   * <code>[a-z] [A-Z] [0-9] _ . - /</code>
   */
  public static boolean isValidPath(PosixPath path) {
    if (path == null) {
      return false;
    }
    String pathStr = path.toString();
    int len = pathStr.length();
    for (int c = 1; c < len; c++) {
      char ch = pathStr.charAt(c);
      if (
          (ch < 'a' || ch > 'z')
              && (ch < 'A' || ch > 'Z')
              && (ch < '0' || ch > '9')
              && ch != '_'
              && ch != '.'
              && ch != '-'
              && ch != '/'
      ) {
        return false;
      }
    }
    return true;
  }

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public CvsRepository() {
    // Do nothing
  }

  private PosixPath path;
  private int linuxServerAccount;
  private int linuxServerGroup;
  private long mode;
  private UnmodifiableTimestamp created;
  private int disableLog;

  @Override
  public boolean canDisable() {
    return disableLog == -1;
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getLinuxServerAccount().isDisabled();
    }
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.CVS_REPOSITORIES, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.CVS_REPOSITORIES, pkey);
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return path;
      case COLUMN_LINUX_SERVER_ACCOUNT:
        return linuxServerAccount;
      case 3:
        return linuxServerGroup;
      case 4:
        return mode;
      case 5:
        return created;
      case 6:
        return disableLog == -1 ? null : disableLog;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  @Override
  public DisableLog getDisableLog() throws SQLException, IOException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  public PosixPath getPath() {
    return path;
  }

  public int getLinuxServerAccount_pkey() {
    return linuxServerAccount;
  }

  public UserServer getLinuxServerAccount() throws SQLException, IOException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(linuxServerAccount);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + linuxServerAccount);
    }
    return lsa;
  }

  public int getLinuxServerGroup_pkey() {
    return linuxServerGroup;
  }

  public GroupServer getLinuxServerGroup() throws SQLException, IOException {
    GroupServer lsg = table.getConnector().getLinux().getGroupServer().get(linuxServerGroup);
    if (lsg == null) {
      throw new SQLException("Unable to find LinuxServerGroup: " + linuxServerGroup);
    }
    return lsg;
  }

  public long getMode() {
    return mode;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.CVS_REPOSITORIES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      path = PosixPath.valueOf(result.getString(2));
      linuxServerAccount = result.getInt(3);
      linuxServerGroup = result.getInt(4);
      mode = result.getLong(5);
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(6));
      disableLog = result.getInt(7);
      if (result.wasNull()) {
        disableLog = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      path = PosixPath.valueOf(in.readUTF());
      linuxServerAccount = in.readCompressedInt();
      linuxServerGroup = in.readCompressedInt();
      mode = in.readLong();
      created = SQLStreamables.readUnmodifiableTimestamp(in);
      disableLog = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.CVS_REPOSITORIES, pkey);
  }

  public void setMode(long mode) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_CVS_REPOSITORY_MODE, pkey, mode);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(path.toString());
    out.writeCompressedInt(linuxServerAccount);
    out.writeCompressedInt(linuxServerGroup);
    out.writeLong(mode);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(created.getTime());
    } else {
      SQLStreamables.writeTimestamp(created, out);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeShort(0);
      out.writeShort(7);
    }
    out.writeCompressedInt(disableLog);
  }
}
