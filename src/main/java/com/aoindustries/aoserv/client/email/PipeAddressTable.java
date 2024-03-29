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
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PipeAddress
 *
 * @author  AO Industries, Inc.
 */
public final class PipeAddressTable extends CachedTableIntegerKey<PipeAddress> {

  PipeAddressTable(AoservConnector connector) {
    super(connector, PipeAddress.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_DOMAIN_name, ASCENDING),
      new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_DOMAIN_name + '.' + Domain.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name + '.' + Address.COLUMN_ADDRESS_name, ASCENDING),
      new OrderBy(PipeAddress.COLUMN_EMAIL_PIPE_name + '.' + Pipe.COLUMN_COMMAND_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  int addEmailPipeAddress(Address emailAddressObject, Pipe emailPipeObject) throws IOException, SQLException {
    return connector.requestIntQueryInvalidating(
        true,
        AoservProtocol.CommandId.ADD,
        Table.TableId.EMAIL_PIPE_ADDRESSES,
        emailAddressObject.getPkey(),
        emailPipeObject.getPkey()
    );
  }

  @Override
  public PipeAddress get(int pkey) throws IOException, SQLException {
    return getUniqueRow(PipeAddress.COLUMN_PKEY, pkey);
  }

  List<Pipe> getEmailPipes(Address address) throws IOException, SQLException {
    int address_id = address.getPkey();
    List<PipeAddress> cached = getRows();
    int len = cached.size();
    List<Pipe> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      PipeAddress pipe = cached.get(c);
      if (pipe.getEmailAddress_id() == address_id) {
        // The pipe might be filtered
        Pipe ep = pipe.getEmailPipe();
        if (ep != null) {
          matches.add(pipe.getEmailPipe());
        }
      }
    }
    return matches;
  }

  List<PipeAddress> getEmailPipeAddresses(Address address) throws IOException, SQLException {
    return getIndexedRows(PipeAddress.COLUMN_EMAIL_ADDRESS, address.getPkey());
  }

  List<PipeAddress> getEnabledEmailPipeAddresses(Address address) throws IOException, SQLException {
    // Use the index first
    List<PipeAddress> cached = getEmailPipeAddresses(address);
    int len = cached.size();
    List<PipeAddress> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      PipeAddress pipe = cached.get(c);
      if (!pipe.getEmailPipe().isDisabled()) {
        matches.add(pipe);
      }
    }
    return matches;
  }

  PipeAddress getEmailPipeAddress(Address address, Pipe pipe) throws IOException, SQLException {
    int address_id = address.getPkey();
    int pipe_id = pipe.getPkey();
    List<PipeAddress> cached = getRows();
    int len = cached.size();
    for (int c = 0; c < len; c++) {
      PipeAddress epa = cached.get(c);
      if (epa.getEmailAddress_id() == address_id && epa.getEmailPipe_id() == pipe_id) {
        return epa;
      }
    }
    return null;
  }

  public List<PipeAddress> getEmailPipeAddresses(Server ao) throws IOException, SQLException {
    int aoPkey = ao.getPkey();
    List<PipeAddress> cached = getRows();
    int len = cached.size();
    List<PipeAddress> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      PipeAddress pipe = cached.get(c);
      if (pipe.getEmailAddress().getDomain().getLinuxServer_host_id() == aoPkey) {
        matches.add(pipe);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_PIPE_ADDRESSES;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_EMAIL_PIPE_ADDRESS)) {
      if (Aosh.checkMinParamCount(Command.ADD_EMAIL_PIPE_ADDRESS, args, 2, err)) {
        if ((args.length & 1) == 0) {
          err.println("aosh: " + Command.ADD_EMAIL_PIPE_ADDRESS + ": must have even number of parameters");
          err.flush();
        } else {
          for (int c = 1; c < args.length; c += 2) {
            String addr = args[c];
            int pos = addr.indexOf('@');
            if (pos == -1) {
              err.print("aosh: " + Command.ADD_EMAIL_PIPE_ADDRESS + ": invalid email address: ");
              err.println(addr);
              err.flush();
            } else {
              out.println(
                  connector.getSimpleClient().addEmailPipeAddress(
                      addr.substring(0, pos),
                      Aosh.parseDomainName(addr.substring(pos + 1), "address"),
                      Aosh.parseInt(args[c + 1], "pkey")
                  )
              );
              out.flush();
            }
          }
        }
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_EMAIL_PIPE_ADDRESS)) {
      if (Aosh.checkParamCount(Command.REMOVE_EMAIL_PIPE_ADDRESS, args, 2, err)) {
        String addr = args[1];
        int pos = addr.indexOf('@');
        if (pos == -1) {
          err.print("aosh: " + Command.REMOVE_EMAIL_PIPE_ADDRESS + ": invalid email address: ");
          err.println(addr);
          err.flush();
        } else {
          connector.getSimpleClient().removeEmailPipeAddress(
              addr.substring(0, pos),
              Aosh.parseDomainName(addr.substring(pos + 1), "address"),
              Aosh.parseInt(args[2], "pkey")
          );
        }
      }
      return true;
    }
    return false;
  }
}
