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

package com.aoindustries.aoserv.client.web.tomcat;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.VirtualHost;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see  Worker
 *
 * @author  AO Industries, Inc.
 */
public final class WorkerTable extends CachedTableIntegerKey<Worker> {

  private static final Logger logger = Logger.getLogger(WorkerTable.class.getName());

  WorkerTable(AOServConnector connector) {
    super(connector, Worker.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(Worker.COLUMN_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Worker.COLUMN_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(Worker.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public Worker get(int bind) throws IOException, SQLException {
    return getUniqueRow(Worker.COLUMN_BIND, bind);
  }

  public List<Worker> getHttpdWorkers(HttpdServer server) throws IOException, SQLException {
    int serverPKey = server.getPkey();
    List<Worker> cached = getRows();
    int size = cached.size();
    List<Worker> matches = new ArrayList<>(size);
    Loop:
    for (int c = 0; c < size; c++) {
      Worker worker = cached.get(c);
      Site hts = worker.getTomcatSite();
      if (hts != null) {
        List<VirtualHost> binds = hts.getHttpdSite().getHttpdSiteBinds();
        // If one of the binds is this server, then count as a match
        for (VirtualHost bind : binds) {
          if (bind.getHttpdBind().getHttpdServer_pkey() == serverPKey) {
            matches.add(worker);
            continue Loop;
          }
        }
      } else {
        SharedTomcat hst = worker.getHttpdSharedTomcat();
        if (hst != null) {
          // If one of the binds is this server, then count as a match
          for (SharedTomcatSite htss : hst.getHttpdTomcatSharedSites()) {
            List<VirtualHost> binds = htss.getHttpdTomcatSite().getHttpdSite().getHttpdSiteBinds();
            for (VirtualHost bind : binds) {
              if (bind.getHttpdBind().getHttpdServer_pkey() == serverPKey) {
                matches.add(worker);
                continue Loop;
              }
            }
          }
        } else {
          logger.log(Level.WARNING, "pkey=" + worker.getPkey(), new SQLException("HttpdWorker doesn't have either HttpdTomcatSite or HttpdSharedTomcat"));
        }
      }
    }
    return matches;
  }

  List<Worker> getHttpdWorkers(Site tomcatSite) throws IOException, SQLException {
    return getIndexedRows(Worker.COLUMN_TOMCAT_SITE, tomcatSite.getPkey());
  }

  public Worker getHttpdWorker(Bind nb) throws IOException, SQLException {
    return getUniqueRow(Worker.COLUMN_BIND, nb.getId());
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_WORKERS;
  }
}
