/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2009, 2016, 2017, 2018, 2020, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalTableStringKey;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  SpamAssassinMode
 *
 * @author  AO Industries, Inc.
 */
public final class SpamAssassinModeTable extends GlobalTableStringKey<SpamAssassinMode> {

  SpamAssassinModeTable(AoservConnector connector) {
    super(connector, SpamAssassinMode.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(SpamAssassinMode.COLUMN_SORT_ORDER_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public SpamAssassinMode get(String name) throws IOException, SQLException {
    return getUniqueRow(SpamAssassinMode.COLUMN_NAME, name);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
  }
}
