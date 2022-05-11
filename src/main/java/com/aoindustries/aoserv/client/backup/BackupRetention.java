/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.backup;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The possible backup retention values allowed in the system.
 *
 * @author  AO Industries, Inc.
 */
public final class BackupRetention extends GlobalObject<Short, BackupRetention> {

  static final int COLUMN_DAYS = 0;
  static final String COLUMN_DAYS_name = "days";

  // public static final short DEFAULT_BACKUP_RETENTION=7;

  private short days;
  private String display;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public BackupRetention() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
        (obj instanceof BackupRetention)
            && ((BackupRetention) obj).days == days;
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_DAYS) {
      return days;
    }
    if (i == 1) {
      return display;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public short getDays() {
    return days;
  }

  public String getDisplay() {
    return display;
  }

  @Override
  public Short getKey() {
    return days;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BACKUP_RETENTIONS;
  }

  @Override
  public int hashCode() {
    return days;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    days = result.getShort(1);
    display = result.getString(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    days = in.readShort();
    display = in.readUTF();
  }

  @Override
  public String toStringImpl() {
    return display;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeShort(days);
    out.writeUTF(display);
  }
}
