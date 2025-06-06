/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net.reputation;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>IpReputationLimiter</code> rate-limits traffic by class and type.
 *
 * @author  AO Industries, Inc.
 */
public final class Limiter extends CachedObjectIntegerKey<Limiter> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_NET_DEVICE = 1;

  static final String COLUMN_NET_DEVICE_name = "net_device";
  static final String COLUMN_IDENTIFIER_name = "identifier";

  private int netDevice;
  private String identifier;
  private String description;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Limiter() {
    // Do nothing
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.IP_REPUTATION_LIMITERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey        = result.getInt(pos++);
    netDevice   = result.getInt(pos++);
    identifier  = result.getString(pos++);
    description = result.getString(pos++);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(netDevice);
    out.writeUTF(identifier);
    out.writeNullUTF(description);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey        = in.readCompressedInt();
    netDevice   = in.readCompressedInt();
    identifier  = in.readUTF();
    description = in.readNullUTF();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_NET_DEVICE:
        return netDevice;
      case 2:
        return identifier;
      case 3:
        return description;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Device getNetDevice() throws SQLException, IOException {
    Device nd = table.getConnector().getNet().getDevice().get(netDevice);
    if (nd == null) {
      throw new SQLException("Unable to find NetDevice: " + netDevice);
    }
    return nd;
  }

  /**
   * Gets the per-net device unique identifier for this reputation limiter.
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Gets the optional description of the limiter.
   */
  public String getDescription() {
    return description;
  }

  public List<LimiterClass> getLimits() throws IOException, SQLException {
    return table.getConnector().getNet().getReputation().getLimiterClass().getLimits(this);
  }

  public List<LimiterSet> getSets() throws IOException, SQLException {
    return table.getConnector().getNet().getReputation().getLimiterSet().getSets(this);
  }
}
