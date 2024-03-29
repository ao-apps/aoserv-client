/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2012, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  BackupPartitionTable
 *
 * @author  AO Industries, Inc.
 */
public final class BackupPartitionTable extends CachedTableIntegerKey<BackupPartition> {

  BackupPartitionTable(AoservConnector connector) {
    super(connector, BackupPartition.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(BackupPartition.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(BackupPartition.COLUMN_PATH_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public BackupPartition get(int pkey) throws IOException, SQLException {
    return getUniqueRow(BackupPartition.COLUMN_PKEY, pkey);
  }

  public List<BackupPartition> getBackupPartitions(Server ao) throws IOException, SQLException {
    return getIndexedRows(BackupPartition.COLUMN_AO_SERVER, ao.getPkey());
  }

  public BackupPartition getBackupPartitionForPath(Server ao, String path) throws IOException, SQLException {
    // Use index first
    List<BackupPartition> cached = getBackupPartitions(ao);
    int size = cached.size();
    for (int c = 0; c < size; c++) {
      BackupPartition bp = cached.get(c);
      if (bp.getPath().toString().equals(path)) {
        return bp;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BACKUP_PARTITIONS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.GET_BACKUP_PARTITION_TOTAL_SIZE)) {
      if (Aosh.checkParamCount(Command.GET_BACKUP_PARTITION_TOTAL_SIZE, args, 2, err)) {
        long size = connector.getSimpleClient().getBackupPartitionTotalSize(
            args[1],
            args[2]
        );
        if (size == -1) {
          out.println("Server unavailable");
        } else {
          out.println(size);
        }
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.GET_BACKUP_PARTITION_USED_SIZE)) {
      if (Aosh.checkParamCount(Command.GET_BACKUP_PARTITION_USED_SIZE, args, 2, err)) {
        long size = connector.getSimpleClient().getBackupPartitionUsedSize(
            args[1],
            args[2]
        );
        if (size == -1) {
          out.println("Server unavailable");
        } else {
          out.println(size);
        }
        out.flush();
      }
      return true;
    }
    return false;
  }
}
