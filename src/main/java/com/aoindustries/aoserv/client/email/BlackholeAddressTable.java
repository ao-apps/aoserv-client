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

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see BlackholeAddress
 *
 * @author  AO Industries, Inc.
 */
public final class BlackholeAddressTable extends CachedTableIntegerKey<BlackholeAddress> {

  BlackholeAddressTable(AoservConnector connector) {
    super(connector, BlackholeAddress.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_DOMAIN_name, ASCENDING),
      new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_ADDRESS_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public BlackholeAddress get(int address) throws IOException, SQLException {
    return getUniqueRow(BlackholeAddress.COLUMN_EMAIL_ADDRESS, address);
  }

  public List<BlackholeAddress> getBlackholeEmailAddresses(Server ao) throws IOException, SQLException {
    int aoPkey = ao.getPkey();
    List<BlackholeAddress> cached = getRows();
    int len = cached.size();
    List<BlackholeAddress> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      BlackholeAddress blackhole = cached.get(c);
      if (blackhole.getEmailAddress().getDomain().getLinuxServer_host_id() == aoPkey) {
        matches.add(blackhole);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BLACKHOLE_EMAIL_ADDRESSES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS)) {
      if (Aosh.checkParamCount(Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS, args, 2, err)) {
        String addr = args[1];
        int pos = addr.indexOf('@');
        if (pos == -1) {
          err.print("aosh: " + Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS + ": invalid email address: ");
          err.println(addr);
          err.flush();
        } else {
          connector.getSimpleClient().removeBlackholeEmailAddress(
              addr.substring(0, pos),
              Aosh.parseDomainName(addr.substring(pos + 1), "address"),
              args[2]
          );
        }
      }
      return true;
    }
    return false;
  }
}
