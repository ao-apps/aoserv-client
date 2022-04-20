/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  InboxAddress
 *
 * @author  AO Industries, Inc.
 */
public final class InboxAddressTable extends CachedTableIntegerKey<InboxAddress> {

  InboxAddressTable(AOServConnector connector) {
    super(connector, InboxAddress.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(InboxAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
    new OrderBy(InboxAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
    new OrderBy(InboxAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_ADDRESS_name, ASCENDING),
    new OrderBy(InboxAddress.COLUMN_LINUX_SERVER_ACCOUNT_name+'.'+UserServer.COLUMN_USERNAME_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addLinuxAccAddress(Address emailAddressObject, UserServer lsa) throws IOException, SQLException {
    return connector.requestIntQueryIL(
      true,
      AoservProtocol.CommandID.ADD,
      Table.TableID.LINUX_ACC_ADDRESSES,
      emailAddressObject.getPkey(),
      lsa.getPkey()
    );
  }

  @Override
  public InboxAddress get(int pkey) throws IOException, SQLException {
    return getUniqueRow(InboxAddress.COLUMN_PKEY, pkey);
  }

  public List<Address> getEmailAddresses(UserServer lsa) throws SQLException, IOException {
    // Start with the index
    List<InboxAddress> cached = getLinuxAccAddresses(lsa);
    int len = cached.size();
    List<Address> matches=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      InboxAddress acc=cached.get(c);
      matches.add(acc.getEmailAddress());
    }
    return matches;
  }

  public List<InboxAddress> getLinuxAccAddresses(UserServer lsa) throws IOException, SQLException {
    return getIndexedRows(InboxAddress.COLUMN_LINUX_SERVER_ACCOUNT, lsa.getPkey());
  }

  public InboxAddress getLinuxAccAddress(Address address, UserServer lsa) throws IOException, SQLException {
    int address_id = address.getPkey();
    int lsaPKey=lsa.getPkey();
    List<InboxAddress> cached=getRows();
    int size=cached.size();
    for (int c=0;c<size;c++) {
      InboxAddress laa=cached.get(c);
      if (laa.getEmailAddress_id() == address_id && laa.getLinuxServerAccount_id() == lsaPKey) {
        return laa;
      }
    }
    return null;
  }

  public List<InboxAddress> getLinuxAccAddresses(Server ao) throws IOException, SQLException {
    int aoPKey=ao.getPkey();
    List<InboxAddress> cached = getRows();
    int len = cached.size();
    List<InboxAddress> matches=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      InboxAddress acc = cached.get(c);
      if (acc.getEmailAddress().getDomain().getLinuxServer_host_id() == aoPKey) {
        matches.add(acc);
      }
    }
    return matches;
  }

  List<UserServer> getLinuxServerAccounts(Address address) throws IOException, SQLException {
    int address_id = address.getPkey();
    List<InboxAddress> cached = getRows();
    int len = cached.size();
    List<UserServer> matches=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      InboxAddress acc = cached.get(c);
      if (acc.getEmailAddress_id() == address_id) {
        UserServer lsa=acc.getLinuxServerAccount();
        if (lsa != null) {
          matches.add(lsa);
        }
      }
    }
    return matches;
  }

  List<InboxAddress> getLinuxAccAddresses(Address address) throws IOException, SQLException {
    return getIndexedRows(InboxAddress.COLUMN_EMAIL_ADDRESS, address.getPkey());
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.LINUX_ACC_ADDRESSES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command=args[0];
    if (command.equalsIgnoreCase(Command.ADD_LINUX_ACC_ADDRESS)) {
      if (AOSH.checkParamCount(Command.ADD_LINUX_ACC_ADDRESS, args, 3, err)) {
        String addr=args[1];
        int pos=addr.indexOf('@');
        if (pos == -1) {
          err.print("aosh: "+Command.ADD_LINUX_ACC_ADDRESS+": invalid email address: ");
          err.println(addr);
          err.flush();
        } else {
          out.println(
            connector.getSimpleAOClient().addLinuxAccAddress(
              addr.substring(0, pos),
              AOSH.parseDomainName(addr.substring(pos+1), "address"),
              args[2],
              AOSH.parseLinuxUserName(args[3], "username")
            )
          );
          out.flush();
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_LINUX_ACC_ADDRESS)) {
      if (AOSH.checkParamCount(Command.REMOVE_LINUX_ACC_ADDRESS, args, 3, err)) {
        String addr=args[1];
        int pos=addr.indexOf('@');
        if (pos == -1) {
          err.print("aosh: "+Command.REMOVE_LINUX_ACC_ADDRESS+": invalid email address: ");
          err.println(addr);
          err.flush();
        } else {
          connector.getSimpleAOClient().removeLinuxAccAddress(
            addr.substring(0, pos),
            AOSH.parseDomainName(addr.substring(pos+1), "address"),
            args[2],
            AOSH.parseLinuxUserName(args[3], "username")
          );
        }
      }
      return true;
    }
    return false;
  }
}
