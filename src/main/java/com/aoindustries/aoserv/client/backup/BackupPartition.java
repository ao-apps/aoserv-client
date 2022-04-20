/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>BackupPartition</code> stores backup data.
 *
 * @author  AO Industries, Inc.
 */
public final class BackupPartition extends CachedObjectIntegerKey<BackupPartition> {

  static final int
    COLUMN_PKEY=0,
    COLUMN_AO_SERVER=1
  ;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_PATH_name = "path";

  private int ao_server;
  private PosixPath path;
  private boolean enabled;
  private boolean quota_enabled;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public BackupPartition() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_AO_SERVER: return ao_server;
      case 2: return path;
      case 3: return enabled;
      case 4: return quota_enabled;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public long getDiskTotalSize() throws IOException, SQLException {
    return table.getConnector().requestLongQuery(true, AoservProtocol.CommandID.GET_BACKUP_PARTITION_DISK_TOTAL_SIZE, pkey);
  }

  public long getDiskUsedSize() throws IOException, SQLException {
    return table.getConnector().requestLongQuery(true, AoservProtocol.CommandID.GET_BACKUP_PARTITION_DISK_USED_SIZE, pkey);
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server ao=table.getConnector().getLinux().getServer().get(ao_server);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: "+ao_server);
    }
    return ao;
  }

  public PosixPath getPath() {
    return path;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.BACKUP_PARTITIONS;
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getLinuxServer().getHostname()+":"+path;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey=result.getInt(1);
      ao_server=result.getInt(2);
      path = PosixPath.valueOf(result.getString(3));
      enabled=result.getBoolean(4);
      quota_enabled=result.getBoolean(5);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * When quota is enabled, all replications/backups into the partition must have quota_gid set.
   * When quota is disabled, all replications/backups into the partition must have quota_gid not set.
   * This generally means that ao_servers, which backup full Unix permissions, will be backed-up to non-quota partitions,
   * while other backups (such as from Windows) will go to quota-enabled partitions for billing purposes.
   *
   * @return the enabled flag
   */
  public boolean isQuotaEnabled() {
    return quota_enabled;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey=in.readCompressedInt();
      ao_server=in.readCompressedInt();
      path = PosixPath.valueOf(in.readUTF()).intern();
      enabled=in.readBoolean();
      quota_enabled=in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(ao_server);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeUTF(path.toString());
    }
    out.writeUTF(path.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeLong(512L * 1024 * 1024); // min free space
      out.writeLong(1024L * 1024 * 1024); // desired free space
    }
    out.writeBoolean(enabled);
    if (
      protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_117) >= 0
      && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0
    ) {
      out.writeCompressedInt(1); // fill_order
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31) >= 0) {
      out.writeBoolean(quota_enabled);
    }
  }
}
