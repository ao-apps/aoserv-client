/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  IpAddress
 *
 * @author  AO Industries, Inc.
 */
public final class IpAddressTable extends CachedTableIntegerKey<IpAddress> {

  IpAddressTable(AOServConnector connector) {
    super(connector, IpAddress.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
      new OrderBy(IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING),
      new OrderBy(IpAddress.COLUMN_DEVICE_name + '.' + Device.COLUMN_DEVICE_ID_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public IpAddress get(int id) throws IOException, SQLException {
    return getUniqueRow(IpAddress.COLUMN_ID, id);
  }

  IpAddress getIPAddress(Device device, InetAddress inetAddress) throws IOException, SQLException {
    int device_id = device.getPkey();

    List<IpAddress> cached = getRows();
    int len = cached.size();
    for (int c = 0; c < len; c++) {
      IpAddress address = cached.get(c);
      if (
          address.getDevice_id() == device_id
              && address.getInetAddress().equals(inetAddress)
      ) {
        return address;
      }
    }
    return null;
  }

  List<IpAddress> getIPAddresses(Device device) throws IOException, SQLException {
    return getIndexedRows(IpAddress.COLUMN_DEVICE, device.getId());
  }

  public List<IpAddress> getIPAddresses(InetAddress inetAddress) throws IOException, SQLException {
    List<IpAddress> cached = getRows();
    int len = cached.size();
    List<IpAddress> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      IpAddress address = cached.get(c);
      if (address.getInetAddress().equals(inetAddress)) {
        matches.add(address);
      }
    }
    return matches;
  }

  public List<IpAddress> getIPAddresses(Package pack) throws IOException, SQLException {
    return getIndexedRows(IpAddress.COLUMN_PACKAGE, pack.getPkey());
  }

  public List<IpAddress> getIPAddresses(Host se) throws IOException, SQLException {
    int sePKey = se.getPkey();

    List<IpAddress> cached = getRows();
    int len = cached.size();
    List<IpAddress> matches = new ArrayList<>(len);
    for (IpAddress address : cached) {
      if (address.getDevice_id() == -1 && address.getInetAddress().isUnspecified()) {
        matches.add(address);
      } else {
        Device device = address.getDevice();
        if (device != null && device.getServer_pkey() == sePKey) {
          matches.add(address);
        }
      }
    }
    return matches;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.IP_ADDRESSES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.IS_IP_ADDRESS_USED)) {
      if (AOSH.checkParamCount(Command.IS_IP_ADDRESS_USED, args, 3, err)) {
        out.println(
            connector.getSimpleAOClient().isIPAddressUsed(
                AOSH.parseInetAddress(args[1], "ip_address"),
                args[2],
                args[3]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.MOVE_IP_ADDRESS)) {
      if (AOSH.checkParamCount(Command.MOVE_IP_ADDRESS, args, 4, err)) {
        connector.getSimpleAOClient().moveIPAddress(
            AOSH.parseInetAddress(args[1], "ip_address"),
            args[2],
            args[3],
            args[4]
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_IP_ADDRESS_DHCP_ADDRESS)) {
      if (AOSH.checkParamCount(Command.SET_IP_ADDRESS_DHCP_ADDRESS, args, 2, err)) {
        connector.getSimpleAOClient().setIPAddressDHCPAddress(
            AOSH.parseInt(args[1], "pkey"),
            AOSH.parseInetAddress(args[2], "ip_address")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_IP_ADDRESS_HOSTNAME)) {
      if (AOSH.checkParamCount(Command.SET_IP_ADDRESS_HOSTNAME, args, 4, err)) {
        connector.getSimpleAOClient().setIPAddressHostname(
            AOSH.parseInetAddress(args[1], "ip_address"),
            args[2],
            args[3],
            AOSH.parseDomainName(args[4], "hostname")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.SET_IP_ADDRESS_PACKAGE)) {
      if (AOSH.checkParamCount(Command.SET_IP_ADDRESS_PACKAGE, args, 4, err)) {
        connector.getSimpleAOClient().setIPAddressPackage(
            AOSH.parseInetAddress(args[1], "ip_address"),
            args[2],
            args[3],
            AOSH.parseAccountingCode(args[4], "package")
        );
      }
      return true;
    }
    return false;
  }
}
