/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008, 2009, 2016, 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the types of processors.
 *
 * @author  AO Industries, Inc.
 */
public final class ProcessorType extends GlobalObjectStringKey<ProcessorType> {

  static final int COLUMN_TYPE = 0;
  static final int COLUMN_SORT_ORDER = 1;

  static final String COLUMN_SORT_ORDER_name = "sort_order";

  private short sortOrder;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public ProcessorType() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_TYPE: return pkey;
      case COLUMN_SORT_ORDER : return sortOrder;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.PROCESSOR_TYPES;
  }

  public String getType() {
    return pkey;
  }

  public short getSortOrder() {
    return sortOrder;
  }


  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    sortOrder = result.getShort(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    sortOrder = in.readShort();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeShort(sortOrder);
  }
}
