/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  FileReplicationLog
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplicationLogTable extends AoservTable<Integer, FileReplicationLog> {

  FileReplicationLogTable(AoservConnector connector) {
    super(connector, FileReplicationLog.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(FileReplicationLog.COLUMN_END_TIME_name, DESCENDING),
      new OrderBy(FileReplicationLog.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_SERVER_name
          + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(FileReplicationLog.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_SERVER_name
          + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(FileReplicationLog.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_BACKUP_PARTITION_name
          + '.' + BackupPartition.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(FileReplicationLog.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_BACKUP_PARTITION_name
          + '.' + BackupPartition.COLUMN_PATH_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addFailoverFileLog(
      FileReplication replication,
      long startTime,
      long endTime,
      int scanned,
      int updated,
      long bytes,
      boolean isSuccessful
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.FAILOVER_FILE_LOG,
        replication.getPkey(),
        startTime,
        endTime,
        scanned,
        updated,
        bytes,
        isSuccessful
    );
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public FileReplicationLog get(Object pkey) throws IOException, SQLException {
    if (pkey == null) {
      return null;
    }
    return get(((Integer) pkey).intValue());
  }

  /**
   * @see  FileReplicationLogTable#get(java.lang.Object)
   */
  public FileReplicationLog get(int pkey) throws IOException, SQLException {
    return getObject(true, AoservProtocol.CommandId.GET_OBJECT, Table.TableId.FAILOVER_FILE_LOG, pkey);
  }

  @Override
  public List<FileReplicationLog> getRowsCopy() throws IOException, SQLException {
    List<FileReplicationLog> list = new ArrayList<>();
    getObjects(true, list, AoservProtocol.CommandId.GET_TABLE, Table.TableId.FAILOVER_FILE_LOG);
    return list;
  }

  List<FileReplicationLog> getFailoverFileLogs(FileReplication replication, int maxRows) throws IOException, SQLException {
    List<FileReplicationLog> list = new ArrayList<>();
    getObjectsNoProgress(true, list, AoservProtocol.CommandId.GET_FAILOVER_FILE_LOGS_FOR_REPLICATION, replication.getPkey(), maxRows);
    return list;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.FAILOVER_FILE_LOG;
  }

  @Override
  protected FileReplicationLog getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
    if (col == FileReplicationLog.COLUMN_PKEY) {
      return get(value);
    }
    throw new IllegalArgumentException("Not a unique column: " + col);
  }
}
