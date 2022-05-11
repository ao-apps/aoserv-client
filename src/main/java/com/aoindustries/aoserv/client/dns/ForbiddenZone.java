/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.dns;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>DNSForbiddenZone</code> is a zone that may not be hosted by
 * AO Industries' name servers.  These domains are forbidden to protect
 * systems that require accurate name resolution for security or reliability.
 *
 * @see  Zone
 *
 * @author  AO Industries, Inc.
 */
public final class ForbiddenZone extends GlobalObjectStringKey<ForbiddenZone> {

  static final int COLUMN_ZONE = 0;
  static final String COLUMN_ZONE_name = "zone";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public ForbiddenZone() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_ZONE) {
      return pkey;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DNS_FORBIDDEN_ZONES;
  }

  public String getZone() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
  }
}
