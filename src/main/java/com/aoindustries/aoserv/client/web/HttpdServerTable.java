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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdServer
 *
 * @author  AO Industries, Inc.
 */
public final class HttpdServerTable extends CachedTableIntegerKey<HttpdServer> {

  HttpdServerTable(AOServConnector connector) {
    super(connector, HttpdServer.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(HttpdServer.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
    new OrderBy(HttpdServer.COLUMN_NAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public HttpdServer get(int pkey) throws IOException, SQLException {
    return getUniqueRow(HttpdServer.COLUMN_PKEY, pkey);
  }

  public List<HttpdServer> getHttpdServers(Server ao) throws IOException, SQLException {
    return getIndexedRows(HttpdServer.COLUMN_AO_SERVER, ao.getPkey());
  }

  public List<HttpdServer> getHttpdServers(Package pk) throws IOException, SQLException {
    return getIndexedRows(HttpdServer.COLUMN_PACKAGE, pk.getPkey());
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_SERVERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.GET_HTTPD_SERVER_CONCURRENCY)) {
      if (AOSH.checkParamCount(Command.GET_HTTPD_SERVER_CONCURRENCY, args, 2, err)) {
        out.write(
          Integer.toString(
            connector.getSimpleAOClient().getHttpdServerConcurrency(
              args[1],
              args[2].isEmpty() ? null : args[2]
            )
          )
        );
        out.flush();
      }
      return true;
    }
    return false;
  }
}
