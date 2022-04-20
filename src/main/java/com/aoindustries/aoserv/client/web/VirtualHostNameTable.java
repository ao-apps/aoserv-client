/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  VirtualHostName
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualHostNameTable extends CachedTableIntegerKey<VirtualHostName> {

  VirtualHostNameTable(AOServConnector connector) {
    super(connector, VirtualHostName.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(VirtualHostName.COLUMN_HOSTNAME_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_NAME_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_HTTPD_SITE_name+'.'+Site.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_PORT_name, ASCENDING),
    new OrderBy(VirtualHostName.COLUMN_HTTPD_SITE_BIND_name+'.'+VirtualHost.COLUMN_NAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addHttpdSiteURL(VirtualHost hsb, DomainName hostname) throws IOException, SQLException {
    return connector.requestIntQueryIL(
      true,
      AoservProtocol.CommandID.ADD,
      Table.TableID.HTTPD_SITE_URLS,
      hsb.getPkey(),
      hostname
    );
  }

  @Override
  public VirtualHostName get(int pkey) throws IOException, SQLException {
    return getUniqueRow(VirtualHostName.COLUMN_PKEY, pkey);
  }

  List<VirtualHostName> getHttpdSiteURLs(VirtualHost bind) throws IOException, SQLException {
    return getIndexedRows(VirtualHostName.COLUMN_HTTPD_SITE_BIND, bind.getPkey());
  }

  VirtualHostName getPrimaryHttpdSiteURL(VirtualHost bind) throws SQLException, IOException {
    // Use the index first
    List<VirtualHostName> cached=getHttpdSiteURLs(bind);
    int size=cached.size();
    for (int c=0;c<size;c++) {
      VirtualHostName hsu=cached.get(c);
      if (hsu.isPrimary()) {
        return hsu;
      }
    }
    throw new SQLException("Unable to find primary HttpdSiteURL for HttpdSiteBind with pkey="+bind.getPkey());
  }

  List<VirtualHostName> getAltHttpdSiteURLs(VirtualHost bind) throws IOException, SQLException {
    // Use the index first
    List<VirtualHostName> cached=getHttpdSiteURLs(bind);
    int size=cached.size();
    List<VirtualHostName> matches=new ArrayList<>(size-1);
    for (int c=0;c<size;c++) {
      VirtualHostName hsu=cached.get(c);
      if (!hsu.isPrimary()) {
        matches.add(hsu);
      }
    }
    return matches;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_SITE_URLS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command=args[0];
    if (command.equalsIgnoreCase(Command.ADD_HTTPD_SITE_URL)) {
      if (AOSH.checkParamCount(Command.ADD_HTTPD_SITE_URL, args, 2, err)) {
        out.println(
          connector.getSimpleAOClient().addHttpdSiteURL(
            AOSH.parseInt(args[1], "httpd_site_bind_pkey"),
            AOSH.parseDomainName(args[2], "hostname")
          )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_HTTPD_SITE_URL)) {
      if (AOSH.checkParamCount(Command.REMOVE_HTTPD_SITE_URL, args, 1, err)) {
        connector.getSimpleAOClient().removeHttpdSiteURL(AOSH.parseInt(args[1], "pkey"));
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_PRIMARY_HTTPD_SITE_URL)) {
      if (AOSH.checkParamCount(Command.SET_PRIMARY_HTTPD_SITE_URL, args, 1, err)) {
        connector.getSimpleAOClient().setPrimaryHttpdSiteURL(AOSH.parseInt(args[1], "pkey"));
      }
      return true;
    } else {
      return false;
    }
  }
}
