/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2004-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2023  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.HostAddress;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each server may perform TCP redirects via xinetd.
 *
 * @author  AO Industries, Inc.
 */
public final class TcpRedirect extends CachedObjectIntegerKey<TcpRedirect> {

  static final int COLUMN_NET_BIND = 0;
  static final String COLUMN_NET_BIND_name = "net_bind";

  private int cps;
  private int cpsOverloadSleepTime;
  private HostAddress destinationHost;
  private Port destinationPort;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public TcpRedirect() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NET_BIND:
        return pkey;
      case 1:
        return cps;
      case 2:
        return cpsOverloadSleepTime;
      case 3:
        return destinationHost;
      case 4:
        return destinationPort;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Bind getNetBind() throws IOException, SQLException {
    Bind nb = table.getConnector().getNet().getBind().get(pkey);
    if (nb == null) {
      throw new SQLException("Unable to find NetBind: " + pkey);
    }
    return nb;
  }

  public int getConnectionsPerSecond() {
    return cps;
  }

  public int getConnectionsPerSecondOverloadSleepTime() {
    return cpsOverloadSleepTime;
  }

  public HostAddress getDestinationHost() {
    return destinationHost;
  }

  public Port getDestinationPort() {
    return destinationPort;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NET_TCP_REDIRECTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      cps = result.getInt(2);
      cpsOverloadSleepTime = result.getInt(3);
      destinationHost = HostAddress.valueOf(result.getString(4));
      destinationPort = Port.valueOf(
          result.getInt(5),
          com.aoapps.net.Protocol.TCP
      );
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      cps = in.readCompressedInt();
      cpsOverloadSleepTime = in.readCompressedInt();
      destinationHost = HostAddress.valueOf(in.readUTF()).intern();
      destinationPort = Port.valueOf(
          in.readCompressedInt(),
          com.aoapps.net.Protocol.TCP
      );
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getNetBind().toStringImpl() + "→" + destinationHost.toBracketedString() + ':' + destinationPort.getPort();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(cps);
    out.writeCompressedInt(cpsOverloadSleepTime);
    out.writeUTF(destinationHost.toString());
    out.writeCompressedInt(destinationPort.getPort());
  }
}
