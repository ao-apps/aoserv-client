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

package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Location
 *
 * @author  AO Industries, Inc.
 */
public final class LocationTable extends CachedTableIntegerKey<Location> {

  LocationTable(AoservConnector connector) {
    super(connector, Location.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Location.COLUMN_HTTPD_SITE_name + '.' + Site.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Location.COLUMN_HTTPD_SITE_name + '.' + Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addHttpdSiteAuthenticatedLocation(
      Site hs,
      String path,
      boolean isRegularExpression,
      String authName,
      PosixPath authGroupFile,
      PosixPath authUserFile,
      String require,
      String handler
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.HTTPD_SITE_AUTHENTICATED_LOCATIONS,
        hs.getPkey(),
        path,
        isRegularExpression,
        authName,
        authGroupFile == null ? "" : authGroupFile.toString(),
        authUserFile == null ? "" : authUserFile.toString(),
        require,
        handler == null ? "" : handler
    );
  }

  @Override
  public Location get(int pkey) throws IOException, SQLException {
    return getUniqueRow(Location.COLUMN_PKEY, pkey);
  }

  List<Location> getHttpdSiteAuthenticatedLocations(Site site) throws IOException, SQLException {
    return getIndexedRows(Location.COLUMN_HTTPD_SITE, site.getPkey());
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_SITE_AUTHENTICATED_LOCATION)) {
      if (Aosh.checkParamCount(Command.ADD_HTTPD_SITE_AUTHENTICATED_LOCATION, args, 9, err)) {
        out.println(
            connector.getSimpleClient().addHttpdSiteAuthenticatedLocation(
                args[1],
                args[2],
                args[3],
                Aosh.parseBoolean(args[4], "is_regular_expression"),
                args[5],
                args[6].isEmpty() ? null : Aosh.parseUnixPath(args[6], "auth_group_file"),
                args[7].isEmpty() ? null : Aosh.parseUnixPath(args[7], "auth_user_file"),
                args[8],
                args[9].isEmpty() ? null : args[9]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES)) {
      if (Aosh.checkParamCount(Command.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES, args, 9, err)) {
        connector.getSimpleClient().setHttpdSiteAuthenticatedLocationAttributes(
            args[1],
            args[2],
            args[3],
            Aosh.parseBoolean(args[4], "is_regular_expression"),
            args[5],
            args[6].isEmpty() ? null : Aosh.parseUnixPath(args[6], "auth_group_file"),
            args[7].isEmpty() ? null : Aosh.parseUnixPath(args[7], "auth_user_file"),
            args[8],
            args[9].isEmpty() ? null : args[9]
        );
      }
      return true;
    } else {
      return false;
    }
  }
}
