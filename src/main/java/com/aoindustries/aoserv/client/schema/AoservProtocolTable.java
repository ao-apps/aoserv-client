/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  AoservProtocol
 *
 * @author  AO Industries, Inc.
 */
public final class AoservProtocolTable extends GlobalTableStringKey<AoservProtocol> {

  AoservProtocolTable(AOServConnector connector) {
    super(connector, AoservProtocol.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(AoservProtocol.COLUMN_CREATED_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public AoservProtocol get(String version) throws IOException, SQLException {
    return getUniqueRow(AoservProtocol.COLUMN_VERSION, version);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.AOSERV_PROTOCOLS;
  }
}
