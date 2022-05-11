/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.account;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.mysql.Database;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.Site;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AccountHost} grants an {@link Account} permission to
 * access resources on a <code>Server</code>.
 *
 * @see  Account
 * @see  Host
 *
 * @author  AO Industries, Inc.
 */
public final class AccountHost extends CachedObjectIntegerKey<AccountHost> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_ACCOUNTING = 1;
  static final int COLUMN_SERVER = 2;
  static final String COLUMN_ACCOUNTING_name = "accounting";
  static final String COLUMN_SERVER_name = "server";

  private Account.Name accounting;
  private int server;
  private boolean isDefault;
  private boolean canControlApache;
  private boolean canControlCron;
  private boolean canControlMysql;
  private boolean canControlPostgresql;
  private boolean canControlXfs;
  private boolean canControlXvfb;
  private boolean canVncConsole;
  private boolean canControlVirtualServer;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public AccountHost() {
    // Do nothing
  }

  public boolean canControlApache() {
    return canControlApache;
  }

  public boolean canControlCron() {
    return canControlCron;
  }

  public boolean canControlMysql() {
    return canControlMysql;
  }

  public boolean canControlPostgresql() {
    return canControlPostgresql;
  }

  public boolean canControlXfs() {
    return canControlXfs;
  }

  public boolean canControlXvfb() {
    return canControlXvfb;
  }

  public boolean canVncConsole() {
    return canVncConsole;
  }

  public boolean canControlVirtualServer() {
    return canControlVirtualServer;
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  public Account getAccount() throws IOException, SQLException {
    Account obj = table.getConnector().getAccount().getAccount().get(accounting);
    if (obj == null) {
      throw new SQLException("Unable to find Account: " + accounting);
    }
    return obj;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_ACCOUNTING:
        return accounting;
      case COLUMN_SERVER:
        return server;
      case 3:
        return isDefault;
      case 4:
        return canControlApache;
      case 5:
        return canControlCron;
      case 6:
        return canControlMysql;
      case 7:
        return canControlPostgresql;
      case 8:
        return canControlXfs;
      case 9:
        return canControlXvfb;
      case 10:
        return canVncConsole;
      case 11:
        return canControlVirtualServer;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getHost_id() {
    return server;
  }

  public Host getHost() throws IOException, SQLException {
    Host obj = table.getConnector().getNet().getHost().get(server);
    if (obj == null) {
      throw new SQLException("Unable to find Host: " + server);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BUSINESS_SERVERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      accounting = Account.Name.valueOf(result.getString(2));
      server = result.getInt(3);
      isDefault = result.getBoolean(4);
      canControlApache = result.getBoolean(5);
      canControlCron = result.getBoolean(6);
      canControlMysql = result.getBoolean(7);
      canControlPostgresql = result.getBoolean(8);
      canControlXfs = result.getBoolean(9);
      canControlXvfb = result.getBoolean(10);
      canVncConsole = result.getBoolean(11);
      canControlVirtualServer = result.getBoolean(12);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isDefault() {
    return isDefault;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      accounting = Account.Name.valueOf(in.readUTF()).intern();
      server = in.readCompressedInt();
      isDefault = in.readBoolean();
      canControlApache = in.readBoolean();
      canControlCron = in.readBoolean();
      canControlMysql = in.readBoolean();
      canControlPostgresql = in.readBoolean();
      canControlXfs = in.readBoolean();
      canControlXvfb = in.readBoolean();
      canVncConsole = in.readBoolean();
      canControlVirtualServer = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<?>> reasons = new ArrayList<>();

    Account bu = getAccount();

    // Do not remove the default unless it is the only one left
    if (
        isDefault
            && bu.getAccountHosts().size() > 1
    ) {
      reasons.add(new CannotRemoveReason<>("Not allowed to remove access to the default host while access to other hosts remains", bu));
    }

    Host se = getHost();
    Server ao = se.getLinuxServer();

    // No children should be able to access the server
    List<Account> bus = table.getConnector().getAccount().getAccount().getRows();
    for (int c = 0; c < bus.size(); c++) {
      if (bu.isAccountOrParentOf(bus.get(c))) {
        Account bu2 = bus.get(c);
        if (!bu.equals(bu2) && bu2.getAccountHost(se) != null) {
          reasons.add(new CannotRemoveReason<>("Child business " + bu2.getName() + " still has access to " + se, bu2));
        }
        List<Package> pks = bu2.getPackages();
        for (int d = 0; d < pks.size(); d++) {
          Package pk = pks.get(d);

          // net_binds
          for (Bind nb : pk.getNetBinds()) {
            if (nb.getHost().equals(se)) {
              String details = nb.getDetails();
              if (details != null) {
                reasons.add(new CannotRemoveReason<>("Used for " + details + " on " + se.toStringImpl(), nb));
              } else {
                IpAddress ia = nb.getIpAddress();
                Device nd = ia.getDevice();
                if (nd != null) {
                  reasons.add(new CannotRemoveReason<>("Used for port " + nb.getPort() + " on " + ia.getInetAddress() + " on " + nd.getDeviceId().getName() + " on " + se.toStringImpl(), nb));
                } else {
                  reasons.add(new CannotRemoveReason<>("Used for port " + nb.getPort() + " on " + ia.getInetAddress() + " on " + se.toStringImpl(), nb));
                }
              }
            }
          }

          // ip_addresses
          for (IpAddress ia : pk.getIpAddresses()) {
            Device nd = ia.getDevice();
            if (
                nd != null
                    && se.equals(nd.getHost())
            ) {
              reasons.add(new CannotRemoveReason<>("Used by IP address " + ia.getInetAddress() + " on " + nd.getDeviceId().getName() + " on " + se.toStringImpl(), ia));
            }
          }

          if (ao != null) {
            // email_pipes
            for (Pipe ep : pk.getEmailPipes()) {
              if (ep.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by email pipe '" + ep.getCommand() + "' on " + ao.getHostname(), ep));
              }
            }

            // httpd_sites
            for (Site hs : pk.getHttpdSites()) {
              if (hs.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by website " + hs.getInstallDirectory() + " on " + ao.getHostname(), hs));
              }
            }

            for (User un : pk.getUsernames()) {
              // linux_server_accounts
              com.aoindustries.aoserv.client.linux.User la = un.getLinuxAccount();
              if (la != null) {
                UserServer lsa = la.getLinuxServerAccount(ao);
                if (lsa != null) {
                  reasons.add(new CannotRemoveReason<>("Used by Linux account " + un.getUsername() + " on " + ao.getHostname(), lsa));
                }
              }

              // mysql_server_users
              com.aoindustries.aoserv.client.mysql.User mu = un.getMysqlUser();
              if (mu != null) {
                for (com.aoindustries.aoserv.client.mysql.Server ms : ao.getMysqlServers()) {
                  com.aoindustries.aoserv.client.mysql.UserServer msu = mu.getMysqlServerUser(ms);
                  if (msu != null) {
                    reasons.add(new CannotRemoveReason<>("Used by MySQL user " + un.getUsername() + " on " + ms.getName() + " on " + ao.getHostname(), msu));
                  }
                }
              }

              // postgres_server_users
              com.aoindustries.aoserv.client.postgresql.User pu = un.getPostgresUser();
              if (pu != null) {
                for (com.aoindustries.aoserv.client.postgresql.Server ps : ao.getPostgresServers()) {
                  com.aoindustries.aoserv.client.postgresql.UserServer psu = pu.getPostgresServerUser(ps);
                  if (psu != null) {
                    reasons.add(new CannotRemoveReason<>("Used by PostgreSQL user " + un.getUsername() + " on " + ps.getName() + " on " + ao.getHostname(), psu));
                  }
                }
              }
            }

            for (Group lg : pk.getLinuxGroups()) {
              // linux_server_groups
              GroupServer lsg = lg.getLinuxServerGroup(ao);
              if (lsg != null) {
                reasons.add(new CannotRemoveReason<>("Used by Linux group " + lg.getName() + " on " + ao.getHostname(), lsg));
              }
            }

            // mysql_databases
            for (Database md : pk.getMysqlDatabases()) {
              com.aoindustries.aoserv.client.mysql.Server ms = md.getMysqlServer();
              if (ms.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by MySQL database " + md.getName() + " on " + ms.getName() + " on " + ao.getHostname(), md));
              }
            }

            // postgres_databases
            for (com.aoindustries.aoserv.client.postgresql.Database pd : pk.getPostgresDatabases()) {
              com.aoindustries.aoserv.client.postgresql.Server ps = pd.getPostgresServer();
              if (ps.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by PostgreSQL database " + pd.getName() + " on " + ps.getName() + " on " + ao.getHostname(), pd));
              }
            }

            // email_domains
            for (Domain ed : pk.getEmailDomains()) {
              if (ed.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by email domain " + ed.getDomain() + " on " + ao.getHostname(), ed));
              }
            }

            // email_smtp_relays
            for (SmtpRelay esr : pk.getEmailSmtpRelays()) {
              if (esr.getLinuxServer().equals(ao)) {
                reasons.add(new CannotRemoveReason<>("Used by email SMTP rule " + esr, esr));
              }
            }
          }
        }
      }
    }
    return reasons;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.BUSINESS_SERVERS, pkey);
  }

  public void setAsDefault() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_DEFAULT_BUSINESS_SERVER, pkey);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(accounting.toString());
    out.writeCompressedInt(server);
    out.writeBoolean(isDefault);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      // can_configure_backup
      out.writeBoolean(false);
    }
    out.writeBoolean(canControlApache);
    out.writeBoolean(canControlCron);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      // can_control_interbase
      out.writeBoolean(false);
    }
    out.writeBoolean(canControlMysql);
    out.writeBoolean(canControlPostgresql);
    out.writeBoolean(canControlXfs);
    out.writeBoolean(canControlXvfb);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_51) >= 0) {
      out.writeBoolean(canVncConsole);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_64) >= 0) {
      out.writeBoolean(canControlVirtualServer);
    }
  }
}
