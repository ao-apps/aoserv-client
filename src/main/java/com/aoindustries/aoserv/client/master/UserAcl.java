/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.net.HostAddress;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>MasterHost</code> controls which hosts a <code>MasterUser</code>
 * is allowed to connect from.  Because <code>MasterUser</code>s have more
 * control over the system, this extra security measure is taken.
 *
 * @see  User
 *
 * @author  AO Industries, Inc.
 */
public final class UserAcl extends CachedObjectIntegerKey<UserAcl> {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_USERNAME_name = "username";
  static final String COLUMN_HOST_name = "host";

  private com.aoindustries.aoserv.client.account.User.Name username;
  private HostAddress host;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  UserAcl#init(java.sql.ResultSet)
   * @see  UserAcl#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public UserAcl() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_PKEY) {
      return pkey;
    }
    if (i == 1) {
      return username;
    }
    if (i == 2) {
      return host;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public HostAddress getHost() {
    return host;
  }

  public User getMasterUser() throws SQLException, IOException {
    User obj = table.getConnector().getMaster().getUser().get(username);
    if (obj == null) {
      throw new SQLException("Unable to find MasterUser: " + username);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_HOSTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      username = com.aoindustries.aoserv.client.account.User.Name.valueOf(result.getString(2));
      host = HostAddress.valueOf(result.getString(3));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      username = com.aoindustries.aoserv.client.account.User.Name.valueOf(in.readUTF()).intern();
      host = HostAddress.valueOf(in.readUTF()).intern();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(username.toString());
    out.writeUTF(host.toString());
  }
}
