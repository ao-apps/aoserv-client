/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.signup;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stores an option for a sign-up request, each option has a unique name per sign-up request.
 *
 * @author  AO Industries, Inc.
 */
public final class Option extends CachedObjectIntegerKey<Option> {

  static final int
      COLUMN_PKEY = 0,
      COLUMN_REQUEST = 1
  ;
  static final String COLUMN_REQUEST_name = "request";
  static final String COLUMN_NAME_name = "name";

  private int request;
  private String name;
  private String value;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Option() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_REQUEST: return request;
      case 2: return name;
      case 3: return value;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.SIGNUP_REQUEST_OPTIONS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    request = result.getInt(pos++);
    name = result.getString(pos++);
    value = result.getString(pos++);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    request = in.readCompressedInt();
    name = in.readUTF().intern();
    value = in.readNullUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(request);
    out.writeUTF(name);
    out.writeNullUTF(value);
  }

  public Request getSignupRequest() throws SQLException, IOException {
    Request sr = table.getConnector().getSignup().getRequest().get(request);
    if (sr == null) {
      throw new SQLException("Unable to find SignupRequest: " + request);
    }
    return sr;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
