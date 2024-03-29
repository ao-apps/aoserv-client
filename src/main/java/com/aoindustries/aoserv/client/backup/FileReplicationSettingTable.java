/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  FileReplicationSettingTable
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplicationSettingTable extends CachedTableIntegerKey<FileReplicationSetting> {

  FileReplicationSettingTable(AoservConnector connector) {
    super(connector, FileReplicationSetting.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(FileReplicationSetting.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_SERVER_name
          + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(FileReplicationSetting.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_SERVER_name
          + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(FileReplicationSetting.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_BACKUP_PARTITION_name
          + '.' + BackupPartition.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(FileReplicationSetting.COLUMN_REPLICATION_name + '.' + FileReplication.COLUMN_BACKUP_PARTITION_name
          + '.' + BackupPartition.COLUMN_PATH_name, ASCENDING),
      new OrderBy(FileReplicationSetting.COLUMN_PATH_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addFileBackupSetting(FileReplication replication, String path, boolean backupEnabled, boolean required) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.FILE_BACKUP_SETTINGS,
        replication.getPkey(),
        path,
        backupEnabled,
        required
    );
  }

  @Override
  public FileReplicationSetting get(int pkey) throws IOException, SQLException {
    return getUniqueRow(FileReplicationSetting.COLUMN_PKEY, pkey);
  }

  FileReplicationSetting getFileBackupSetting(FileReplication ffr, String path) throws IOException, SQLException {
    // Use index first
    for (FileReplicationSetting fbs : getFileBackupSettings(ffr)) {
      if (fbs.getPath().equals(path)) {
        return fbs;
      }
    }
    return null;
  }

  List<FileReplicationSetting> getFileBackupSettings(FileReplication ffr) throws IOException, SQLException {
    return getIndexedRows(FileReplicationSetting.COLUMN_REPLICATION, ffr.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.FILE_BACKUP_SETTINGS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_FILE_BACKUP_SETTING)) {
      if (Aosh.checkParamCount(Command.ADD_FILE_BACKUP_SETTING, args, 4, err)) {
        out.println(
            connector.getSimpleClient().addFileBackupSetting(
                Aosh.parseInt(args[1], "replication"),
                args[2],
                Aosh.parseBoolean(args[3], "backup_enabled"),
                Aosh.parseBoolean(args[4], "required")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_FILE_BACKUP_SETTING)) {
      if (Aosh.checkParamCount(Command.REMOVE_FILE_BACKUP_SETTING, args, 2, err)) {
        connector.getSimpleClient().removeFileBackupSetting(
            Aosh.parseInt(args[1], "replication"),
            args[2]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_FILE_BACKUP_SETTING)) {
      if (Aosh.checkParamCount(Command.SET_FILE_BACKUP_SETTING, args, 4, err)) {
        connector.getSimpleClient().setFileBackupSetting(
            Aosh.parseInt(args[1], "replication"),
            args[2],
            Aosh.parseBoolean(args[3], "backup_enabled"),
            Aosh.parseBoolean(args[4], "required")
        );
      }
      return true;
    }
    return false;
  }

  void setFileBackupSettings(
      final FileReplication ffr,
      final List<String> paths,
      final List<Boolean> backupEnableds,
      final List<Boolean> requireds
  ) throws IOException, SQLException {
    if (paths.size() != backupEnableds.size()) {
      throw new IllegalArgumentException("paths.size() != backupEnableds.size(): " + paths.size() + " != " + backupEnableds.size());
    }
    if (paths.size() != requireds.size()) {
      throw new IllegalArgumentException("paths.size() != requireds.size(): " + paths.size() + " != " + requireds.size());
    }

    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.SET_FILE_BACKUP_SETTINGS_ALL_AT_ONCE,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(ffr.getPkey());
            int size = paths.size();
            out.writeCompressedInt(size);
            for (int c = 0; c < size; c++) {
              out.writeUTF(paths.get(c));
              out.writeBoolean(backupEnableds.get(c));
              out.writeBoolean(requireds.get(c));
            }
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
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
