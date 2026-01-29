/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.infrastructure;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectStringKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @author  AO Industries, Inc.
 */
public final class ServerFarm extends CachedObjectStringKey<ServerFarm> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  private String description;
  private int owner;
  private boolean useRestrictedSmtpPort;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  ServerFarm#init(java.sql.ResultSet)
   * @see  ServerFarm#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public ServerFarm() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case 1:
        return description;
      case 2:
        return owner;
      case 3:
        return useRestrictedSmtpPort;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Package getOwner() throws IOException, SQLException {
    // May be filtered
    return table.getConnector().getBilling().getPackage().get(owner);
  }

  /**
   * TODO: Remove this flag once all servers are CentOS 7+ with firewalld-based egress filters.
   */
  public boolean useRestrictedSmtpPort() {
    return useRestrictedSmtpPort;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SERVER_FARMS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    description = result.getString(2);
    owner = result.getInt(3);
    useRestrictedSmtpPort = result.getBoolean(4);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    description = in.readUTF();
    owner = in.readCompressedInt();
    useRestrictedSmtpPort = in.readBoolean();
  }

  @Override
  public String toStringImpl() {
    return description;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(description);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeUTF("192.168.0.0/16");
      out.writeBoolean(false);
      out.writeUTF("mob");
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_102) >= 0) {
      out.writeCompressedInt(owner);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_26) >= 0) {
      out.writeBoolean(useRestrictedSmtpPort);
    }
  }
}
