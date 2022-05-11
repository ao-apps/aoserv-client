/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2014, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.master;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.CachedObjectUserNameKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MasterUser</code> is an {@link Administrator} who
 * has greater permissions.  Their access is secure on a per-<code>Server</code>
 * basis, and may also include full access to DNS, backups, and other
 * systems.
 *
 * @see  Administrator
 * @see  UserHost
 * @see  UserAcl
 *
 * @author  AO Industries, Inc.
 */
public final class User extends CachedObjectUserNameKey<User> {

  static final int COLUMN_USERNAME = 0;
  static final String COLUMN_USERNAME_name = "username";

  private boolean isActive;
  private boolean canAccessAccounting;
  private boolean canAccessBankAccount;
  private boolean canInvalidateTables;
  private boolean canAccessAdminWeb;
  private boolean isDnsAdmin;
  private boolean isRouter;
  private boolean isClusterAdmin;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public User() {
    // Do nothing
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_USERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey                   = com.aoindustries.aoserv.client.account.User.Name.valueOf(result.getString(1));
      isActive              = result.getBoolean(2);
      canAccessAccounting  = result.getBoolean(3);
      canAccessBankAccount = result.getBoolean(4);
      canInvalidateTables  = result.getBoolean(5);
      canAccessAdminWeb   = result.getBoolean(6);
      isDnsAdmin           = result.getBoolean(7);
      isRouter              = result.getBoolean(8);
      isClusterAdmin       = result.getBoolean(9);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toString());
    out.writeBoolean(isActive);
    out.writeBoolean(canAccessAccounting);
    out.writeBoolean(canAccessBankAccount);
    out.writeBoolean(canInvalidateTables);
    out.writeBoolean(canAccessAdminWeb);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      out.writeBoolean(false);
    } // is_ticket_admin
    out.writeBoolean(isDnsAdmin);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_118) < 0) {
      out.writeBoolean(false);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_65) >= 0) {
      out.writeBoolean(isRouter);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_73) >= 0) {
      out.writeBoolean(isClusterAdmin);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey                    = com.aoindustries.aoserv.client.account.User.Name.valueOf(in.readUTF()).intern();
      isActive               = in.readBoolean();
      canAccessAccounting   = in.readBoolean();
      canAccessBankAccount = in.readBoolean();
      canInvalidateTables   = in.readBoolean();
      canAccessAdminWeb    = in.readBoolean();
      isDnsAdmin            = in.readBoolean();
      isRouter               = in.readBoolean();
      isClusterAdmin        = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_USERNAME:
        return pkey;
      case 1:
        return isActive;
      case 2:
        return canAccessAccounting;
      case 3:
        return canAccessBankAccount;
      case 4:
        return canInvalidateTables;
      case 5:
        return canAccessAdminWeb;
      case 6:
        return isDnsAdmin;
      case 7:
        return isRouter;
      case 8:
        return isClusterAdmin;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Administrator getAdministrator() throws SQLException, IOException {
    Administrator obj = table.getConnector().getAccount().getAdministrator().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find Administrator: " + pkey);
    }
    return obj;
  }

  public boolean isActive() {
    return isActive;
  }

  public boolean canAccessAccounting() {
    return canAccessAccounting;
  }

  public boolean canAccessBankAccount() {
    return canAccessBankAccount;
  }

  public boolean canInvalidateTables() {
    return canInvalidateTables;
  }

  public boolean isWebAdmin() {
    return canAccessAdminWeb;
  }

  public boolean isDnsAdmin() {
    return isDnsAdmin;
  }

  public boolean isRouter() {
    return isRouter;
  }

  public boolean isClusterAdmin() {
    return isClusterAdmin;
  }
}
