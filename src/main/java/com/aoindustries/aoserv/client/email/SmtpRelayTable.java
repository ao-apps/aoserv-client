/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.net.HostAddress;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  SmtpRelay
 *
 * @author  AO Industries, Inc.
 */
public final class SmtpRelayTable extends CachedTableIntegerKey<SmtpRelay> {

  SmtpRelayTable(AoservConnector connector) {
    super(connector, SmtpRelay.class);
  }

  private static final OrderBy[] defaultOrderBy = {
      new OrderBy(SmtpRelay.COLUMN_AO_SERVER_name + '.' + Server.COLUMN_HOSTNAME_name, ASCENDING),
      new OrderBy(SmtpRelay.COLUMN_HOST_name, ASCENDING),
      new OrderBy(SmtpRelay.COLUMN_PACKAGE_name, ASCENDING)
  };

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  public int addEmailSmtpRelay(final Package pack, final Server aoServer, final HostAddress host, final SmtpRelayType type, final long duration) throws IOException, SQLException {
    return connector.requestResult(
        true,
        AoservProtocol.CommandId.ADD,
        new AoservConnector.ResultRequest<>() {
          private int pkey;
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(Table.TableId.EMAIL_SMTP_RELAYS.ordinal());
            out.writeUTF(pack.getName().toString());
            out.writeCompressedInt(aoServer == null ? -1 : aoServer.getPkey());
            out.writeUTF(host.toString());
            out.writeUTF(type.getName());
            out.writeLong(duration);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              pkey = in.readCompressedInt();
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public Integer afterRelease() {
            connector.tablesUpdated(invalidateList);
            return pkey;
          }
        }
    );
  }

  @Override
  public SmtpRelay get(int pkey) throws IOException, SQLException {
    return getUniqueRow(SmtpRelay.COLUMN_PKEY, pkey);
  }

  public SmtpRelay getEmailSmtpRelay(Package pk, Server ao, HostAddress host) throws IOException, SQLException {
    Account.Name packageName = pk.getName();
    int aoPkey = ao.getPkey();

    List<SmtpRelay> cached = getRows();
    int len = cached.size();
    for (int c = 0; c < len; c++) {
      SmtpRelay relay = cached.get(c);
      Integer hostId;
      if (
          packageName.equals(relay.getPackage_name())
              && (
              (hostId = relay.getLinuxServer_host_id()) == null
                  || hostId == aoPkey
            )
              && host.equals(relay.getHost())
      ) {
        return relay;
      }
    }
    return null;
  }

  public List<SmtpRelay> getEmailSmtpRelays(Package pk) throws IOException, SQLException {
    return getIndexedRows(SmtpRelay.COLUMN_PACKAGE, pk.getName());
  }

  public List<SmtpRelay> getEmailSmtpRelays(Server ao) throws IOException, SQLException {
    int aoPkey = ao.getPkey();

    List<SmtpRelay> cached = getRows();
    int len = cached.size();
    List<SmtpRelay> matches = new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      SmtpRelay relay = cached.get(c);
      Integer hostId;
      if (
          (hostId = relay.getLinuxServer_host_id()) == null
              || hostId == aoPkey
      ) {
        matches.add(relay);
      }
    }
    return matches;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SMTP_RELAYS;
  }

  @Override
  public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
    String command = args[0];
    if (command.equalsIgnoreCase(Command.ADD_EMAIL_SMTP_RELAY)) {
      if (Aosh.checkParamCount(Command.ADD_EMAIL_SMTP_RELAY, args, 5, err)) {
        String s = args[5].trim();
        out.println(
            connector.getSimpleClient().addEmailSmtpRelay(
                Aosh.parseAccountingCode(args[1], "package"),
                args[2],
                Aosh.parseHostAddress(args[3], "host"),
                args[4],
                s.length() == 0 ? -1 : Aosh.parseLong(s, "duration")
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.DISABLE_EMAIL_SMTP_RELAY)) {
      if (Aosh.checkParamCount(Command.DISABLE_EMAIL_SMTP_RELAY, args, 2, err)) {
        out.println(
            connector.getSimpleClient().disableEmailSmtpRelay(
                Aosh.parseInt(args[1], "pkey"),
                args[2]
            )
        );
        out.flush();
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.ENABLE_EMAIL_SMTP_RELAY)) {
      if (Aosh.checkParamCount(Command.ENABLE_EMAIL_SMTP_RELAY, args, 1, err)) {
        connector.getSimpleClient().enableEmailSmtpRelay(Aosh.parseInt(args[1], "pkey"));
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REFRESH_EMAIL_SMTP_RELAY)) {
      if (Aosh.checkParamCount(Command.REFRESH_EMAIL_SMTP_RELAY, args, 1, err)) {
        connector.getSimpleClient().refreshEmailSmtpRelay(
            Aosh.parseInt(args[1], "pkey"),
            args[2].trim().length() == 0 ? -1 : Aosh.parseLong(args[2], "min_duration")
        );
      }
      return true;
    } else if (command.equalsIgnoreCase(Command.REMOVE_EMAIL_SMTP_RELAY)) {
      if (Aosh.checkParamCount(Command.REMOVE_EMAIL_SMTP_RELAY, args, 1, err)) {
        connector.getSimpleClient().removeEmailSmtpRelay(Aosh.parseInt(args[1], "pkey"));
      }
      return true;
    }
    return false;
  }
}
