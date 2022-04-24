/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  WhoisHistory
 *
 * @author  AO Industries, Inc.
 */
public final class WhoisHistoryTable extends CachedTableIntegerKey<WhoisHistory> {

  WhoisHistoryTable(AOServConnector connector) {
    super(connector, WhoisHistory.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(WhoisHistory.COLUMN_registrableDomain_name, ASCENDING),
      new OrderBy(WhoisHistory.COLUMN_time_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public WhoisHistory get(int id) throws IOException, SQLException {
    return getUniqueRow(WhoisHistory.COLUMN_id, id);
  }

  @Override
  public List<WhoisHistory> getIndexedRows(int col, Object value) throws IOException, SQLException {
    if (col == WhoisHistory.COLUMN_output) {
      throw new UnsupportedOperationException("getIndexedRows not supported for WhoisHistory.output because each access is a round-trip to the server");
    }
    if (col == WhoisHistory.COLUMN_error) {
      throw new UnsupportedOperationException("getIndexedRows not supported for WhoisHistory.error because each access is a round-trip to the server");
    }
    return super.getIndexedRows(col, value);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.WhoisHistory;
  }
}
