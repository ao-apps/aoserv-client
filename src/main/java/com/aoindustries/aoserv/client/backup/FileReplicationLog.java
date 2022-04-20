/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplicationLog extends AOServObject<Integer, FileReplicationLog> implements SingleTableObject<Integer, FileReplicationLog> {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_REPLICATION_name = "replication";
  static final String COLUMN_END_TIME_name = "end_time";

  private AOServTable<Integer, FileReplicationLog> table;

  private int pkey;
  private int replication;
  private UnmodifiableTimestamp startTime;
  private UnmodifiableTimestamp endTime;
  private int scanned;
  private int updated;
  private long bytes;
  private boolean is_successful;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public FileReplicationLog() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
      (obj instanceof FileReplicationLog)
      && ((FileReplicationLog)obj).pkey == pkey
    ;
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case 1: return replication;
      case 2: return startTime;
      case 3: return endTime;
      case 4: return scanned;
      case 5: return updated;
      case 6: return bytes;
      case 7: return is_successful;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getStartTime() {
    return startTime;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getEndTime() {
    return endTime;
  }

  public int getPkey() {
    return pkey;
  }

  @Override
  public Integer getKey() {
    return pkey;
  }

  public int getScanned() {
    return scanned;
  }

  public FileReplication getFailoverFileReplication() throws SQLException, IOException {
    FileReplication ffr=table.getConnector().getBackup().getFileReplication().get(replication);
    if (ffr == null) {
      throw new SQLException("Unable to find FailoverFileReplication: "+replication);
    }
    return ffr;
  }

  @Override
  public AOServTable<Integer, FileReplicationLog> getTable() {
    return table;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.FAILOVER_FILE_LOG;
  }

  public int getUpdated() {
    return updated;
  }

  @Override
  public int hashCode() {
    return pkey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey=result.getInt(1);
    replication=result.getInt(2);
    startTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(3));
    endTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(4));
    scanned=result.getInt(5);
    updated=result.getInt(6);
    bytes=result.getLong(7);
    is_successful=result.getBoolean(8);
  }

  public boolean isSuccessful() {
    return is_successful;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey=in.readCompressedInt();
    replication=in.readCompressedInt();
    startTime = SQLStreamables.readUnmodifiableTimestamp(in);
    endTime = SQLStreamables.readUnmodifiableTimestamp(in);
    scanned=in.readCompressedInt();
    updated=in.readCompressedInt();
    bytes=in.readLong();
    is_successful=in.readBoolean();
  }

  @Override
  public void setTable(AOServTable<Integer, FileReplicationLog> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table=table;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(replication);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(startTime.getTime());
      out.writeLong(endTime.getTime());
    } else {
      SQLStreamables.writeTimestamp(startTime, out);
      SQLStreamables.writeTimestamp(endTime, out);
    }
    out.writeCompressedInt(scanned);
    out.writeCompressedInt(updated);
    out.writeLong(bytes);
    out.writeBoolean(is_successful);
  }
}
