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

package com.aoindustries.aoserv.client.ftp;

import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PrivateServer
 *
 * @author  AO Industries, Inc.
 */
public final class PrivateServerTable extends CachedTableIntegerKey<PrivateServer> {

  PrivateServerTable(AoservConnector connector) {
    super(connector, PrivateServer.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(PrivateServer.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(PrivateServer.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(PrivateServer.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
      new OrderBy(PrivateServer.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_IP_ADDRESS_name + '.' + IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_DEVICE_ID_name, ASCENDING),
      new OrderBy(PrivateServer.COLUMN_NET_BIND_name + '.' + Bind.COLUMN_PORT_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public PrivateServer get(int pkey) throws IOException, SQLException {
    return getUniqueRow(PrivateServer.COLUMN_NET_BIND, pkey);
  }

  public List<PrivateServer> getPrivateFtpServers(Server ao) throws IOException, SQLException {
    int aoPkey = ao.getPkey();

    List<PrivateServer> cached = getRows();
    int size = cached.size();
    List<PrivateServer> matches = new ArrayList<>(size);
    for (int c = 0; c < size; c++) {
      PrivateServer obj = cached.get(c);
      if (obj.getNetBind().getServer_pkey() == aoPkey) {
        matches.add(obj);
      }
    }
    return matches;
  }

  /*
  PrivateFtpServer getPrivateFtpServer(Server ao, String path) {
    int aoPkey = ao.getPkey();

    List<PrivateFtpServer> cached = getRows();
    int size = cached.size();
    for (int c = 0; c < size;c++) {
      PrivateFtpServer obj = cached.get(c);
      if (
          obj.getRoot().equals(path)
          && obj.getNetBind().server == aoPkey
      ) {
        return obj;
      }
    }
    return null;
  }*/

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PRIVATE_FTP_SERVERS;
  }
}
