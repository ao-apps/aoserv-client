/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  StaticSite
 *
 * @author  AO Industries, Inc.
 */
public final class StaticSiteTable extends CachedTableIntegerKey<StaticSite> {

  StaticSiteTable(AOServConnector connector) {
    super(connector, StaticSite.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(StaticSite.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_NAME_name, ASCENDING),
    new OrderBy(StaticSite.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public StaticSite get(int pkey) throws IOException, SQLException {
    return getUniqueRow(StaticSite.COLUMN_HTTPD_SITE, pkey);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_STATIC_SITES;
  }
}
