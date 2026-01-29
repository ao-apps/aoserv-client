/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>DeviceId</code> is a simple wrapper for the
 * different names of network devices used in Linux servers.
 *
 * @see  Device
 *
 * @author  AO Industries, Inc.
 */
public final class DeviceId extends GlobalObjectStringKey<DeviceId> implements Comparable<DeviceId> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  public static final String BMC = "bmc";
  public static final String BOND0 = "bond0";
  public static final String BOND1 = "bond1";
  public static final String BOND2 = "bond2";
  public static final String LO = "lo";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH0 = "eth0";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH1 = "eth1";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH2 = "eth2";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH3 = "eth3";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH4 = "eth4";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH5 = "eth5";

  /**
   * @deprecated  Device IDs are now generated to match hardware, please do not use hard-coded values.
   */
  @Deprecated
  public static final String ETH6 = "eth6";

  private boolean isLoopback;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  DeviceId#init(java.sql.ResultSet)
   * @see  DeviceId#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DeviceId() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_NAME) {
      return pkey;
    }
    if (i == 1) {
      return isLoopback;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NET_DEVICE_IDS;
  }

  @Override
  public void init(ResultSet results) throws SQLException {
    pkey = results.getString(1);
    isLoopback = results.getBoolean(2);
  }

  public boolean isLoopback() {
    return isLoopback;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    isLoopback = in.readBoolean();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeBoolean(isLoopback);
  }

  @Override
  public int compareTo(DeviceId other) {
    return pkey.compareTo(other.getName());
  }
}
