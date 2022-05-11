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
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Limiter
 *
 * @author  AO Industries, Inc.
 */
public final class LimiterTable extends CachedTableIntegerKey<Limiter> {

  LimiterTable(AoservConnector connector) {
    super(connector, Limiter.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Limiter.COLUMN_NET_DEVICE_name + '.' + Device.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Limiter.COLUMN_NET_DEVICE_name + '.' + Device.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Limiter.COLUMN_NET_DEVICE_name + '.' + Device.COLUMN_DEVICE_ID_name, ASCENDING),
      new OrderBy(Limiter.COLUMN_IDENTIFIER_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Limiter get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Limiter.COLUMN_PKEY, pkey);
  }

  /*
  List<IpReputationLimiter> getIpReputationLimiters(NetDevice nd) throws IOException, SQLException {
    return getIndexedRows(IpReputationLimiter.COLUMN_NET_DEVICE, nd.getPkey());
  }
   */

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.IP_REPUTATION_LIMITERS;
  }
}
