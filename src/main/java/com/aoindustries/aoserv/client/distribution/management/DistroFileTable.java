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

package com.aoindustries.aoserv.client.distribution.management;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.FilesystemCachedTable;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class DistroFileTable extends FilesystemCachedTable<Integer, DistroFile> {

  DistroFileTable(AoservConnector connector) {
    super(connector, DistroFile.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(DistroFile.COLUMN_PATH_name, ASCENDING),
      new OrderBy(DistroFile.COLUMN_OPERATING_SYSTEM_VERSION_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public DistroFile get(Object pkey) throws IOException, SQLException {
    return getUniqueRow(DistroFile.COLUMN_PKEY, pkey);
  }

  /**
   * @see  #get(java.lang.Object)
   */
  public DistroFile get(int pkey) throws IOException, SQLException {
    return getUniqueRow(DistroFile.COLUMN_PKEY, pkey);
  }

  @Override
  public int getRecordLength() {
    return
        4                                                       // pkey
            + 4                                                 // operating_system_version
            + 4 + DistroFile.MAX_PATH_LENGTH * 2                // path
            + 1                                                 // optional
            + 4 + DistroFile.MAX_TYPE_LENGTH * 2                // type
            + 8                                                 // mode
            + 4 + DistroFile.MAX_LINUX_ACCOUNT_LENGTH * 2       // linux_account
            + 4 + DistroFile.MAX_LINUX_GROUP_LENGTH * 2         // linux_group
            + 8                                                 // size
            + 1 + 8 + 8 + 8 + 8                                 // file_sha256
            + 1 + 4 + DistroFile.MAX_SYMLINK_TARGET_LENGTH * 2; // symlink_target
  }

  @Override
  public int getCachedRowCount() throws IOException, SQLException {
    if (isLoaded()) {
      return super.getCachedRowCount();
    } else {
      return connector.requestIntQuery(true, AoservProtocol.CommandId.GET_CACHED_ROW_COUNT, Table.TableId.DISTRO_FILES);
    }
  }

  @Override
  public int size() throws IOException, SQLException {
    if (isLoaded()) {
      return super.size();
    } else {
      return connector.requestIntQuery(true, AoservProtocol.CommandId.GET_ROW_COUNT, Table.TableId.DISTRO_FILES);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DISTRO_FILES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.START_DISTRO)) {
      if (Aosh.checkParamCount(Command.START_DISTRO, args, 2, err)) {
        connector.getSimpleClient().startDistro(
            args[1],
            Aosh.parseBoolean(args[2], "include_user")
        );
      }
      return true;
    }
    return false;
  }

  public void startDistro(Server server, boolean includeUser) throws IOException, SQLException {
    connector.requestUpdate(
        true,
        AoservProtocol.CommandId.START_DISTRO,
        server.getPkey(),
        includeUser
    );
  }
}
