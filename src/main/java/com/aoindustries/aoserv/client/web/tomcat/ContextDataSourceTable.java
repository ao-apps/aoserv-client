/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  ContextDataSource
 *
 * @author  AO Industries, Inc.
 */
public final class ContextDataSourceTable extends CachedTableIntegerKey<ContextDataSource> {

  ContextDataSourceTable(AoservConnector connector) {
    super(connector, ContextDataSource.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(ContextDataSource.COLUMN_TOMCAT_CONTEXT_name + '.' + Context.COLUMN_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_NAME_name, ASCENDING),
      new OrderBy(ContextDataSource.COLUMN_TOMCAT_CONTEXT_name + '.' + Context.COLUMN_TOMCAT_SITE_name + '.' + Site.COLUMN_HTTPD_SITE_name
          + '.' + com.aoindustries.aoserv.client.web.Site.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(ContextDataSource.COLUMN_TOMCAT_CONTEXT_name + '.' + Context.COLUMN_PATH_name, ASCENDING),
      new OrderBy(ContextDataSource.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addHttpdTomcatDataSource(
      Context htc,
      String name,
      String driverClassName,
      String url,
      String username,
      String password,
      int maxActive,
      int maxIdle,
      int maxWait,
      String validationQuery
  ) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.HTTPD_TOMCAT_DATA_SOURCES,
        htc.getPkey(),
        name,
        driverClassName,
        url,
        username,
        password,
        maxActive,
        maxIdle,
        maxWait,
        validationQuery == null ? "" : validationQuery
    );
  }

  @Override
  public ContextDataSource get(int pkey) throws IOException, SQLException {
    return getUniqueRow(ContextDataSource.COLUMN_PKEY, pkey);
  }

  List<ContextDataSource> getHttpdTomcatDataSources(Context htc) throws IOException, SQLException {
    return getIndexedRows(ContextDataSource.COLUMN_TOMCAT_CONTEXT, htc.getPkey());
  }

  ContextDataSource getHttpdTomcatDataSource(Context htc, String name) throws IOException, SQLException {
    // Use index first
    List<ContextDataSource> dataSources = getHttpdTomcatDataSources(htc);
    for (ContextDataSource dataSource : dataSources) {
      if (dataSource.getName().equals(name)) {
        return dataSource;
      }
    }
    return null;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_DATA_SOURCES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_TOMCAT_DATA_SOURCE)) {
      if (Aosh.checkParamCount(Command.ADD_HTTPD_TOMCAT_DATA_SOURCE, args, 12, err)) {
        out.println(
            connector.getSimpleClient().addHttpdTomcatDataSource(
                args[1],
                args[2],
                args[3],
                args[4],
                args[5],
                args[6],
                args[7],
                args[8],
                Aosh.parseInt(args[9], "max_active"),
                Aosh.parseInt(args[10], "max_idle"),
                Aosh.parseInt(args[11], "max_wait"),
                args[12]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_HTTPD_TOMCAT_DATA_SOURCE)) {
      if (Aosh.checkParamCount(Command.REMOVE_HTTPD_TOMCAT_DATA_SOURCE, args, 1, err)) {
        connector.getSimpleClient().removeHttpdTomcatDataSource(Aosh.parseInt(args[1], "pkey"));
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.UPDATE_HTTPD_TOMCAT_DATA_SOURCE)) {
      if (Aosh.checkParamCount(Command.UPDATE_HTTPD_TOMCAT_DATA_SOURCE, args, 13, err)) {
        connector.getSimpleClient().updateHttpdTomcatDataSource(
            args[1],
            args[2],
            args[3],
            args[4],
            args[5],
            args[6],
            args[7],
            args[8],
            args[9],
            Aosh.parseInt(args[10], "max_active"),
            Aosh.parseInt(args[11], "max_idle"),
            Aosh.parseInt(args[12], "max_wait"),
            args[13]
        );
      }
      return true;
    } else {
      return false;
    }
  }
}
