/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One type of operating system.
 *
 * @see Host
 *
 * @author  AO Industries, Inc.
 */
public final class OperatingSystem extends GlobalObjectStringKey<OperatingSystem> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  public static final String CENTOS = "centos";
  public static final String DEBIAN = "debian";
  public static final String ROCKY = "rocky";
  public static final String WINDOWS = "windows";

  /**
   * @deprecated  What is this used for?
   *
   * @see  OperatingSystemVersion#DEFAULT_OPERATING_SYSTEM_VERSION
   */
  @Deprecated
  public static final String DEFAULT_OPERATING_SYSTEM = ROCKY;

  private String display;
  private boolean isUnix;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public OperatingSystem() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case 1:
        return display;
      case 2:
        return isUnix;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getName() {
    return pkey;
  }

  public String getDisplay() {
    return display;
  }

  public boolean isUnix() {
    return isUnix;
  }

  public OperatingSystemVersion getOperatingSystemVersion(AoservConnector conn, String version, Architecture architecture) throws IOException, SQLException {
    return conn.getDistribution().getOperatingSystemVersion().getOperatingSystemVersion(this, version, architecture);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.OPERATING_SYSTEMS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    display = result.getString(2);
    isUnix = result.getBoolean(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    display = in.readUTF();
    isUnix = in.readBoolean();
  }

  @Override
  public String toStringImpl() {
    return display;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(display);
    out.writeBoolean(isUnix);
  }
}
