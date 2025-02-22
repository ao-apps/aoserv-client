/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Associates a permission with a business administrator.
 *
 * @author  AO Industries, Inc.
 */
public final class AdministratorPermission extends CachedObjectIntegerKey<AdministratorPermission> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_USERNAME = 1;
  static final String COLUMN_USERNAME_name = "username";
  static final String COLUMN_PERMISSION_name = "permission";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public AdministratorPermission() {
    // Do nothing
  }

  private User.Name username;
  private String permission;

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_USERNAME:
        return username;
      case 2:
        return permission;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public User.Name getAdministrator_username() {
    return username;
  }

  public Administrator getAdministrator() throws SQLException, IOException {
    Administrator obj = table.getConnector().getAccount().getAdministrator().get(username);
    if (obj == null) {
      throw new SQLException("Unable to find Administrator: " + username);
    }
    return obj;
  }

  public String getAoservPermission_name() {
    return permission;
  }

  public Permission getAoservPermission() throws SQLException, IOException {
    Permission ap = table.getConnector().getMaster().getPermission().get(permission);
    if (ap == null) {
      throw new SQLException("Unable to find AoservPermission: " + permission);
    }
    return ap;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BUSINESS_ADMINISTRATOR_PERMISSIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      username = User.Name.valueOf(result.getString(2));
      permission = result.getString(3);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      username = User.Name.valueOf(in.readUTF()).intern();
      permission = in.readUTF().intern();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(username.toString());
    out.writeUTF(permission);
  }
}
