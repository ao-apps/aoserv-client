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

package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  ServerStat
 *
 * @author  AO Industries, Inc.
 */
public final class ServerStatTable extends AoservTable<String, ServerStat> {

  ServerStatTable(AoservConnector connector) {
    super(connector, ServerStat.class);
  }

  @Override
  protected OrderBy[] getDefaultOrderBy() {
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
   */
  @Deprecated
  @Override
  public ServerStat get(Object name) throws IOException, SQLException {
    if (name == null) {
      return null;
    }
    List<ServerStat> table = getRows();
    int size = table.size();
    for (int c = 0; c < size; c++) {
      ServerStat mss = table.get(c);
      if (mss.getName().equals(name)) {
        return mss;
      }
    }
    return null;
  }

  @Override
  public List<ServerStat> getRowsCopy() throws IOException, SQLException {
    List<ServerStat> list = new ArrayList<>();
    getObjects(true, list, AoservProtocol.CommandId.GET_TABLE, Table.TableId.MASTER_SERVER_STATS);
    return list;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_SERVER_STATS;
  }

  @Override
  protected ServerStat getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
    if (col == ServerStat.COLUMN_NAME) {
      return get(value);
    }
    throw new IllegalArgumentException("Not a unique column: " + col);
  }
}
