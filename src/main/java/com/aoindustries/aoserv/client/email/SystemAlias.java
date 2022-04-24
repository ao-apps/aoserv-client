/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each {@link Server} has several entries in <code>/etc/aliases</code>
 * that do not belong to any particular <code>EmailDomain</code> or
 * <code>Package</code>.  These are a standard part of the email
 * configuration and are contained in <code>SystemEmailAlias</code>es.
 *
 * @author  AO Industries, Inc.
 */
public final class SystemAlias extends CachedObjectIntegerKey<SystemAlias> {

  static final int
      COLUMN_PKEY = 0,
      COLUMN_AO_SERVER = 1
  ;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_ADDRESS_name = "address";

  private int ao_server;
  private String address;
  private String destination;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public SystemAlias() {
    // Do nothing
  }

  public String getAddress() {
    return address;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_AO_SERVER: return ao_server;
      case 2: return address;
      case 3: return destination;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDestination() {
    return destination;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server ao = table.getConnector().getLinux().getServer().get(ao_server);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + ao_server);
    }
    return ao;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.SYSTEM_EMAIL_ALIASES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    ao_server = result.getInt(2);
    address = result.getString(3);
    destination = result.getString(4);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    ao_server = in.readCompressedInt();
    address = in.readUTF().intern();
    destination = in.readUTF().intern();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(ao_server);
    out.writeUTF(address);
    out.writeUTF(destination);
  }
}
