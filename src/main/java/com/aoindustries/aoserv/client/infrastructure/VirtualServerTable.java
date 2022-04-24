/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2008-2012, 2014, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.infrastructure;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @see  VirtualServer
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualServerTable extends CachedTableIntegerKey<VirtualServer> {

  VirtualServerTable(AOServConnector connector) {
    super(connector, VirtualServer.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(VirtualServer.COLUMN_SERVER_name + '.' + Host.COLUMN_PACKAGE_name + '.' + Package.COLUMN_NAME_name, ASCENDING),
      new OrderBy(VirtualServer.COLUMN_SERVER_name + '.' + Host.COLUMN_NAME_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public VirtualServer get(int server) throws IOException, SQLException {
    return getUniqueRow(VirtualServer.COLUMN_SERVER, server);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.VIRTUAL_SERVERS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.CREATE_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.CREATE_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().createVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.REBOOT_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.REBOOT_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().rebootVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.SHUTDOWN_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.SHUTDOWN_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().shutdownVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.DESTROY_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.DESTROY_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().destroyVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.PAUSE_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.PAUSE_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().pauseVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.UNPAUSE_VIRTUAL_SERVER)) {
      if (AOSH.checkParamCount(Command.UNPAUSE_VIRTUAL_SERVER, args, 1, err)) {
        out.print(connector.getSimpleAOClient().unpauseVirtualServer(args[1]));
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.GET_VIRTUAL_SERVER_STATUS)) {
      if (AOSH.checkParamCount(Command.GET_VIRTUAL_SERVER_STATUS, args, 1, err)) {
        out.println(
            VirtualServer.getStatusList(
                connector.getSimpleAOClient().getVirtualServerStatus(args[1])
            )
        );
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.GET_PRIMARY_PHYSICAL_SERVER)) {
      if (AOSH.checkParamCount(Command.GET_PRIMARY_PHYSICAL_SERVER, args, 1, err)) {
        out.println(
            connector.getSimpleAOClient().getPrimaryVirtualServer(args[1])
        );
        out.flush();
      }
      return true;
    }
    if (command.equalsIgnoreCase(Command.GET_SECONDARY_PHYSICAL_SERVER)) {
      if (AOSH.checkParamCount(Command.GET_SECONDARY_PHYSICAL_SERVER, args, 1, err)) {
        out.println(
            connector.getSimpleAOClient().getSecondaryVirtualServer(args[1])
        );
        out.flush();
      }
      return true;
    }
    return false;
  }
}
