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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see FileReplicationSchedule
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplicationScheduleTable extends CachedTableIntegerKey<FileReplicationSchedule> {

  FileReplicationScheduleTable(AOServConnector connector) {
    super(connector, FileReplicationSchedule.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(FileReplicationSchedule.COLUMN_REPLICATION_name+'.'+FileReplication.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
    new OrderBy(FileReplicationSchedule.COLUMN_REPLICATION_name+'.'+FileReplication.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
    new OrderBy(FileReplicationSchedule.COLUMN_REPLICATION_name+'.'+FileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
    new OrderBy(FileReplicationSchedule.COLUMN_REPLICATION_name+'.'+FileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
    new OrderBy(FileReplicationSchedule.COLUMN_HOUR_name, ASCENDING),
    new OrderBy(FileReplicationSchedule.COLUMN_MINUTE_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  List<FileReplicationSchedule> getFailoverFileSchedules(FileReplication replication) throws IOException, SQLException {
    return getIndexedRows(FileReplicationSchedule.COLUMN_REPLICATION, replication.getPkey());
  }

  @Override
  public FileReplicationSchedule get(int pkey) throws IOException, SQLException {
    return getUniqueRow(FileReplicationSchedule.COLUMN_PKEY, pkey);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.FAILOVER_FILE_SCHEDULE;
  }

  void setFailoverFileSchedules(final FileReplication ffr, final List<Short> hours, final List<Short> minutes) throws IOException, SQLException {
    if (hours.size() != minutes.size()) {
      throw new IllegalArgumentException("hours.size() != minutes.size(): "+hours.size()+" != "+minutes.size());
    }

    connector.requestUpdate(
      true,
      AoservProtocol.CommandID.SET_FAILOVER_FILE_SCHEDULES,
      new AOServConnector.UpdateRequest() {
        private IntList invalidateList;

        @Override
        public void writeRequest(StreamableOutput out) throws IOException {
          out.writeCompressedInt(ffr.getPkey());
          int size = hours.size();
          out.writeCompressedInt(size);
          for (int c=0;c<size;c++) {
            out.writeShort(hours.get(c));
            out.writeShort(minutes.get(c));
          }
        }

        @Override
        public void readResponse(StreamableInput in) throws IOException, SQLException {
          int code=in.readByte();
          if (code == AoservProtocol.DONE) {
            invalidateList=AOServConnector.readInvalidateList(in);
          } else {
            AoservProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
          }
        }

        @Override
        public void afterRelease() {
          connector.tablesUpdated(invalidateList);
        }
      }
    );
  }
}
