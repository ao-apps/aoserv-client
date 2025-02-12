/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MysqlDbUser</code> grants a <code>MysqlServerUser</code>
 * access to a <code>MysqlDatabase</code>.  The database and
 * user must be on the same server.
 *
 * @see  Database
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class DatabaseUser extends CachedObjectIntegerKey<DatabaseUser> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_MYSQL_DATABASE = 1;
  static final int COLUMN_MYSQL_SERVER_USER = 2;
  static final String COLUMN_MYSQL_DATABASE_name = "mysql_database";
  static final String COLUMN_MYSQL_SERVER_USER_name = "mysql_server_user";

  private int mysqlDatabase;
  private int mysqlServerUser;

  private boolean selectPriv;
  private boolean insertPriv;
  private boolean updatePriv;
  private boolean deletePriv;
  private boolean createPriv;
  private boolean dropPriv;
  private boolean grantPriv;
  private boolean referencesPriv;
  private boolean indexPriv;
  private boolean alterPriv;
  private boolean createTmpTablePriv;
  private boolean lockTablesPriv;
  private boolean createViewPriv;
  private boolean showViewPriv;
  private boolean createRoutinePriv;
  private boolean alterRoutinePriv;
  private boolean executePriv;
  private boolean eventPriv;
  private boolean triggerPriv;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DatabaseUser() {
    // Do nothing
  }

  public boolean canAlter() {
    return alterPriv;
  }

  public boolean canCreateTempTable() {
    return createTmpTablePriv;
  }

  public boolean canLockTables() {
    return lockTablesPriv;
  }

  public boolean canCreate() {
    return createPriv;
  }

  public boolean canDelete() {
    return deletePriv;
  }

  public boolean canDrop() {
    return dropPriv;
  }

  public boolean canGrant() {
    return grantPriv;
  }

  public boolean canIndex() {
    return indexPriv;
  }

  public boolean canInsert() {
    return insertPriv;
  }

  public boolean canReference() {
    return referencesPriv;
  }

  public boolean canSelect() {
    return selectPriv;
  }

  public boolean canUpdate() {
    return updatePriv;
  }

  public boolean canCreateView() {
    return createViewPriv;
  }

  public boolean canShowView() {
    return showViewPriv;
  }

  public boolean canCreateRoutine() {
    return createRoutinePriv;
  }

  public boolean canAlterRoutine() {
    return alterRoutinePriv;
  }

  public boolean canExecute() {
    return executePriv;
  }

  public boolean canEvent() {
    return eventPriv;
  }

  public boolean canTrigger() {
    return triggerPriv;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_MYSQL_DATABASE:
        return mysqlDatabase;
      case COLUMN_MYSQL_SERVER_USER:
        return mysqlServerUser;
      case 3:
        return selectPriv;
      case 4:
        return insertPriv;
      case 5:
        return updatePriv;
      case 6:
        return deletePriv;
      case 7:
        return createPriv;
      case 8:
        return dropPriv;
      case 9:
        return grantPriv;
      case 10:
        return referencesPriv;
      case 11:
        return indexPriv;
      case 12:
        return alterPriv;
      case 13:
        return createTmpTablePriv;
      case 14:
        return lockTablesPriv;
      case 15:
        return createViewPriv;
      case 16:
        return showViewPriv;
      case 17:
        return createRoutinePriv;
      case 18:
        return alterRoutinePriv;
      case 19:
        return executePriv;
      case 20:
        return eventPriv;
      case 21:
        return triggerPriv;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Database getMysqlDatabase() throws IOException, SQLException {
    // May be null due to filtering or a recently removed table
    return table.getConnector().getMysql().getDatabase().get(mysqlDatabase);
  }

  public int getMysqlServerUser_id() {
    return mysqlServerUser;
  }

  public UserServer getMysqlServerUser() throws IOException, SQLException {
    // May be null due to filtering or a recently removed row
    return table.getConnector().getMysql().getUserServer().get(mysqlServerUser);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MYSQL_DB_USERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    mysqlDatabase = result.getInt(2);
    mysqlServerUser = result.getInt(3);
    selectPriv = result.getBoolean(4);
    insertPriv = result.getBoolean(5);
    updatePriv = result.getBoolean(6);
    deletePriv = result.getBoolean(7);
    createPriv = result.getBoolean(8);
    dropPriv = result.getBoolean(9);
    grantPriv = result.getBoolean(10);
    referencesPriv = result.getBoolean(11);
    indexPriv = result.getBoolean(12);
    alterPriv = result.getBoolean(13);
    createTmpTablePriv = result.getBoolean(14);
    lockTablesPriv = result.getBoolean(15);
    createViewPriv = result.getBoolean(16);
    showViewPriv = result.getBoolean(17);
    createRoutinePriv = result.getBoolean(18);
    alterRoutinePriv = result.getBoolean(19);
    executePriv = result.getBoolean(20);
    eventPriv = result.getBoolean(21);
    triggerPriv = result.getBoolean(22);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    mysqlDatabase = in.readCompressedInt();
    mysqlServerUser = in.readCompressedInt();
    selectPriv = in.readBoolean();
    insertPriv = in.readBoolean();
    updatePriv = in.readBoolean();
    deletePriv = in.readBoolean();
    createPriv = in.readBoolean();
    dropPriv = in.readBoolean();
    grantPriv = in.readBoolean();
    referencesPriv = in.readBoolean();
    indexPriv = in.readBoolean();
    alterPriv = in.readBoolean();
    createTmpTablePriv = in.readBoolean();
    lockTablesPriv = in.readBoolean();
    createViewPriv = in.readBoolean();
    showViewPriv = in.readBoolean();
    createRoutinePriv = in.readBoolean();
    alterRoutinePriv = in.readBoolean();
    executePriv = in.readBoolean();
    eventPriv = in.readBoolean();
    triggerPriv = in.readBoolean();
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws IOException, SQLException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();
    UserServer msu = getMysqlServerUser();
    if (msu.isSpecial()) {
      Server ms = msu.getMysqlServer();
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to revoke access from a special MySQL user: "
                  + msu.getMysqlUser_username()
                  + " on "
                  + ms.getName()
                  + " on "
                  + ms.getLinuxServer().getHostname(),
              this
          )
      );
    }
    Database md = getMysqlDatabase();
    if (md.isSpecial()) {
      Server ms = md.getMysqlServer();
      reasons.add(
          new CannotRemoveReason<>(
              "Not allowed to revoke access to a special MySQL database: "
                  + md.getName()
                  + " on "
                  + ms.getName()
                  + " on "
                  + ms.getLinuxServer().getHostname(),
              this
          )
      );
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    UserServer msu = getMysqlServerUser();
    if (msu.isSpecial()) {
      Server ms = msu.getMysqlServer();
      throw new SQLException(
          "Refusing to revoke access from a special MySQL user: "
              + msu.getMysqlUser_username()
              + " on "
              + ms.getName()
              + " on "
              + ms.getLinuxServer().getHostname()
      );
    }
    Database md = getMysqlDatabase();
    if (md.isSpecial()) {
      Server ms = md.getMysqlServer();
      throw new SQLException(
          "Refusing to revoke access to a special MySQL database: "
              + md.getName()
              + " on "
              + ms.getName()
              + " on "
              + ms.getLinuxServer().getHostname()
      );
    }
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.MYSQL_DB_USERS,
        pkey
    );
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(mysqlDatabase);
    out.writeCompressedInt(mysqlServerUser);
    out.writeBoolean(selectPriv);
    out.writeBoolean(insertPriv);
    out.writeBoolean(updatePriv);
    out.writeBoolean(deletePriv);
    out.writeBoolean(createPriv);
    out.writeBoolean(dropPriv);
    out.writeBoolean(grantPriv);
    out.writeBoolean(referencesPriv);
    out.writeBoolean(indexPriv);
    out.writeBoolean(alterPriv);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_111) >= 0) {
      out.writeBoolean(createTmpTablePriv);
      out.writeBoolean(lockTablesPriv);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4) >= 0) {
      out.writeBoolean(createViewPriv);
      out.writeBoolean(showViewPriv);
      out.writeBoolean(createRoutinePriv);
      out.writeBoolean(alterRoutinePriv);
      out.writeBoolean(executePriv);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_54) >= 0) {
      out.writeBoolean(eventPriv);
      out.writeBoolean(triggerPriv);
    }
  }
}
