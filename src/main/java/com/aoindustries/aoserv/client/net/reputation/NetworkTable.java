/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net.reputation;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableLongKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Network
 *
 * @author  AO Industries, Inc.
 */
public final class NetworkTable extends CachedTableLongKey<Network> {

  NetworkTable(AoservConnector connector) {
    super(connector, Network.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Network.COLUMN_SET_name + '.' + Set.COLUMN_IDENTIFIER_name, ASCENDING),
      new OrderBy(Network.COLUMN_NETWORK_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Network get(long pkey) throws IOException, SQLException {
    return getUniqueRow(Network.COLUMN_PKEY, pkey);
  }

  List<Network> getNetworks(Set set) throws IOException, SQLException {
    return getIndexedRows(Network.COLUMN_SET, set.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.IP_REPUTATION_SET_NETWORKS;
  }
}
