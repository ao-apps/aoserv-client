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

package com.aoindustries.aoserv.client.web.jboss;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Version
 *
 * @author  AO Industries, Inc.
 */
public final class VersionTable extends GlobalTableIntegerKey<Version> {

  VersionTable(AoservConnector connector) {
    super(connector, Version.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Version.COLUMN_VERSION_name + '.' + SoftwareVersion.COLUMN_VERSION_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Version get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Version.COLUMN_VERSION, pkey);
  }

  public Version getHttpdJbossVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
    return get(connector.getDistribution().getSoftware()
        .get(Version.TECHNOLOGY_NAME)
        .getTechnologyVersion(connector, version, osv)
        .getPkey()
    );
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_JBOSS_VERSIONS;
  }
}
