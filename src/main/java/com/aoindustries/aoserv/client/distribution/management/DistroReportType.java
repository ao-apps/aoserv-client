/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution.management;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class DistroReportType extends GlobalObjectStringKey<DistroReportType> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  private String display;

  /**
   * The different report types.
   */
  public static final String
      BIG_DIRECTORY  = "BD",
      DIGEST         = "DI",
      EXTRA          = "EX",
      GROUP_MISMATCH = "GR",
      HIDDEN         = "HI",
      LENGTH         = "LN",
      MISSING        = "MI",
      OWNER_MISMATCH = "OW",
      NO_OWNER       = "NO",
      NO_GROUP       = "NG",
      PERMISSIONS    = "PR",
      SETUID         = "SU",
      SYMLINK        = "SY",
      TYPE           = "TY";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DistroReportType() {
    // Do nothing
  }

  @Override
  public String toStringImpl() {
    return display;
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_NAME) {
      return pkey;
    }
    if (i == 1) {
      return display;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public String getDisplay() {
    return display;
  }

  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DISTRO_REPORT_TYPES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    display = result.getString(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    display = in.readUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(display);
  }
}
