/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client;

import com.aoapps.collections.SortedArrayList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.Strings;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoapps.security.HashedPassword;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.AccountHost;
import com.aoindustries.aoserv.client.account.AccountTable;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.account.UserTable;
import com.aoindustries.aoserv.client.aosh.Aosh;
import com.aoindustries.aoserv.client.backup.BackupPartition;
import com.aoindustries.aoserv.client.backup.FileReplication;
import com.aoindustries.aoserv.client.backup.FileReplicationSetting;
import com.aoindustries.aoserv.client.billing.NoticeLog;
import com.aoindustries.aoserv.client.billing.NoticeLogTable;
import com.aoindustries.aoserv.client.billing.NoticeType;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.billing.PackageDefinitionLimit;
import com.aoindustries.aoserv.client.billing.PackageTable;
import com.aoindustries.aoserv.client.billing.Transaction;
import com.aoindustries.aoserv.client.billing.TransactionType;
import com.aoindustries.aoserv.client.distribution.Architecture;
import com.aoindustries.aoserv.client.distribution.OperatingSystem;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.distribution.Software;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.dns.Record;
import com.aoindustries.aoserv.client.dns.RecordType;
import com.aoindustries.aoserv.client.dns.TopLevelDomain;
import com.aoindustries.aoserv.client.dns.Zone;
import com.aoindustries.aoserv.client.dns.ZoneTable;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.email.BlackholeAddress;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Forwarding;
import com.aoindustries.aoserv.client.email.InboxAddress;
import com.aoindustries.aoserv.client.email.InboxAttributes;
import com.aoindustries.aoserv.client.email.ListAddress;
import com.aoindustries.aoserv.client.email.MajordomoList;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.email.MajordomoVersion;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.PipeAddress;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.email.SmtpRelayType;
import com.aoindustries.aoserv.client.email.SpamAssassinMode;
import com.aoindustries.aoserv.client.email.SpamMessage;
import com.aoindustries.aoserv.client.ftp.GuestUser;
import com.aoindustries.aoserv.client.infrastructure.ServerFarm;
import com.aoindustries.aoserv.client.infrastructure.VirtualDisk;
import com.aoindustries.aoserv.client.infrastructure.VirtualServer;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.GroupTable;
import com.aoindustries.aoserv.client.linux.GroupType;
import com.aoindustries.aoserv.client.linux.GroupUser;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.Shell;
import com.aoindustries.aoserv.client.linux.User.Gecos;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.linux.UserType;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.FirewallZone;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.HostTable;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.password.PasswordGenerator;
import com.aoindustries.aoserv.client.password.PasswordProtected;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.postgresql.Encoding;
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.ticket.Language;
import com.aoindustries.aoserv.client.ticket.Priority;
import com.aoindustries.aoserv.client.ticket.TicketType;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Location;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.SiteTable;
import com.aoindustries.aoserv.client.web.VirtualHost;
import com.aoindustries.aoserv.client.web.VirtualHostName;
import com.aoindustries.aoserv.client.web.tomcat.Context;
import com.aoindustries.aoserv.client.web.tomcat.ContextDataSource;
import com.aoindustries.aoserv.client.web.tomcat.ContextParameter;
import com.aoindustries.aoserv.client.web.tomcat.JkMount;
import com.aoindustries.aoserv.client.web.tomcat.PrivateTomcatSite;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcatSite;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcatTable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * {@link SimpleAoservClient} is a simplified interface into the client
 * code.  Not all information is available, but less knowledge is required
 * to accomplish some common tasks.  All methods are invoked using standard
 * data types.  The underlying implementation changes over time, but
 * this access point does not change as frequently.
 * <p>
 * Most of the {@link Aosh} commands resolve to these method calls.
 * </p>
 *
 * @see  Aosh
 * @see  AoservConnector
 *
 * @author  AO Industries, Inc.
 */
// TODO: This 8700 line monstrosity should be split into appropriate structure
// TODO: as it is used primarily by Aosh.  Either do this directly in Aosh,
// TODO: or have an aoserv-client-simple project that is used by Aosh.
@SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"})
public final class SimpleAoservClient {

  final AoservConnector connector;

  /**
   * Creates a new {@link SimpleAoservClient} using the provided
   * {@link AoservConnector}.
   *
   * @param  connector  the {@link AoservConnector} that will be
   *                    used for communication with the server.
   *
   * @see  AoservConnector#getConnector()
   * @see  TcpConnector#getTcpConnector
   * @see  SslConnector#getSslConnector
   */
  public SimpleAoservClient(AoservConnector connector) {
    this.connector = connector;
  }

  private Architecture getArchitecture(String architecture) throws IllegalArgumentException, IOException, SQLException {
    Architecture ar = connector.getDistribution().getArchitecture().get(architecture);
    if (ar == null) {
      throw new IllegalArgumentException("Unable to find Architecture: " + architecture);
    }
    return ar;
  }

  private Server getLinuxServer(String hostname) throws IllegalArgumentException, IOException, SQLException {
    try {
      Server ao = DomainName.validate(hostname).isValid() ? connector.getLinux().getServer().get(DomainName.valueOf(hostname)) : null;
      if (ao == null) {
        throw new IllegalArgumentException("net.Host is not a linux.Server: " + hostname);
      }
      return ao;
    } catch (ValidationException e) {
      // Should not happen since isValid checked first
      throw new WrappedException(e);
    }
  }

  private Account getAccount(Account.Name name) throws IllegalArgumentException, IOException, SQLException {
    Account account = connector.getAccount().getAccount().get(name);
    if (account == null) {
      throw new IllegalArgumentException("Unable to find Account: " + name);
    }
    return account;
  }

  private Zone getZone(String zone) throws IllegalArgumentException, IOException, SQLException {
    Zone dz = connector.getDns().getZone().get(zone);
    if (dz == null) {
      throw new IllegalArgumentException("Unable to find Zone: " + zone);
    }
    return dz;
  }

  private Address getEmailAddress(String aoServer, DomainName domain, String address) throws IllegalArgumentException, IOException, SQLException {
    Address ea = getEmailDomain(aoServer, domain).getEmailAddress(address);
    if (ea == null) {
      throw new IllegalArgumentException("Unable to find EmailAddress: " + address + '@' + domain + " on " + aoServer);
    }
    return ea;
  }

  private Domain getEmailDomain(String aoServer, DomainName domain) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getLinuxServer(aoServer).getEmailDomain(domain);
    if (ed == null) {
      throw new IllegalArgumentException("Unable to find EmailDomain: " + domain + " on " + aoServer);
    }
    return ed;
  }

  private com.aoindustries.aoserv.client.email.List getEmailList(String aoServer, PosixPath path) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.email.List el = getLinuxServer(aoServer).getEmailList(path);
    if (el == null) {
      throw new IllegalArgumentException("Unable to find EmailList: " + path + " on " + aoServer);
    }
    return el;
  }

  private SpamAssassinMode getEmailSpamAssassinIntegrationMode(String mode) throws IllegalArgumentException, IOException, SQLException {
    SpamAssassinMode esaim = connector.getEmail().getSpamAssassinMode().get(mode);
    if (esaim == null) {
      throw new IllegalArgumentException("Unable to find EmailSpamAssassinIntegrationMode: " + mode);
    }
    return esaim;
  }

  private FileReplication getFailoverFileReplication(String fromServer, String toServer, String path) throws IllegalArgumentException, IOException, SQLException {
    Host fromSe = getHost(fromServer);
    BackupPartition bp = getLinuxServer(toServer).getBackupPartitionForPath(path);
    if (bp == null) {
      throw new IllegalArgumentException("Unable to find BackupPartition: " + path + " on " + toServer);
    }
    FileReplication replication = null;
    for (FileReplication ffr : fromSe.getFailoverFileReplications()) {
      if (ffr.getBackupPartition().equals(bp)) {
        replication = ffr;
        break;
      }
    }
    if (replication == null) {
      throw new IllegalArgumentException("Unable to find FailoverFileReplication: From " + fromServer + " to " + toServer + " at " + path);
    }
    return replication;
  }

  private HttpdServer getHttpdServer(String aoServer, String name) throws IllegalArgumentException, IOException, SQLException {
    for (HttpdServer hs : getLinuxServer(aoServer).getHttpdServers()) {
      if (Objects.equals(name, hs.getName())) {
        return hs;
      }
    }
    throw new IllegalArgumentException("Unable to find HttpdServer: " + (name == null ? "\"\"" : name) + " on " + aoServer);
  }

  private SharedTomcat getHttpdSharedTomcat(String aoServer, String name) throws IllegalArgumentException, IOException, SQLException {
    SharedTomcat hst = getLinuxServer(aoServer).getHttpdSharedTomcat(name);
    if (hst == null) {
      throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: " + name + " on " + aoServer);
    }
    return hst;
  }

  private Site getHttpdSite(String aoServer, String siteName) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getLinuxServer(aoServer).getHttpdSite(siteName);
    if (hs == null) {
      throw new IllegalArgumentException("Unable to find Site: " + siteName + " on " + aoServer);
    }
    return hs;
  }

  private IpAddress getIpAddress(String server, String netDevice, InetAddress ipAddress) throws IllegalArgumentException, SQLException, IOException {
    IpAddress ia = getNetDevice(server, netDevice).getIpAddress(ipAddress);
    if (ia == null) {
      throw new IllegalArgumentException("Unable to find IpAddress: " + ipAddress + " on " + netDevice + " on " + server);
    }
    return ia;
  }

  private Language getLanguage(String code) throws IllegalArgumentException, IOException, SQLException {
    Language la = connector.getTicket().getLanguage().get(code);
    if (la == null) {
      throw new IllegalArgumentException("Unable to find Language: " + code);
    }
    return la;
  }

  private com.aoindustries.aoserv.client.linux.User getLinuxAccount(com.aoindustries.aoserv.client.linux.User.Name username) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.linux.User la = connector.getLinux().getUser().get(username);
    if (la == null) {
      throw new IllegalArgumentException("Unable to find LinuxAccount: " + username);
    }
    return la;
  }

  private Group getLinuxGroup(Group.Name name) throws IllegalArgumentException, IOException, SQLException {
    Group lg = connector.getLinux().getGroup().get(name);
    if (lg == null) {
      throw new IllegalArgumentException("Unable to find LinuxGroup: " + name);
    }
    return lg;
  }

  private UserServer getLinuxServerAccount(
      String aoServer,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    UserServer lsa = getLinuxServer(aoServer).getLinuxServerAccount(username);
    if (lsa == null) {
      throw new IllegalArgumentException("Unable to find LinuxServerAccount: " + username + " on " + aoServer);
    }
    return lsa;
  }

  private GroupServer getLinuxServerGroup(String server, Group.Name name) throws IllegalArgumentException, IOException, SQLException {
    GroupServer lsg = getLinuxServer(server).getLinuxServerGroup(name);
    if (lsg == null) {
      throw new IllegalArgumentException("Unable to find LinuxServerGroup: " + name + " on " + server);
    }
    return lsg;
  }

  private com.aoindustries.aoserv.client.mysql.Server getMysqlServer(
      String aoServer,
      com.aoindustries.aoserv.client.mysql.Server.Name name
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.Server ms = getLinuxServer(aoServer).getMysqlServer(name);
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MysqlServer: " + name + " on " + aoServer);
    }
    return ms;
  }

  private com.aoindustries.aoserv.client.mysql.Database getMysqlDatabase(
      String aoServer,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      com.aoindustries.aoserv.client.mysql.Database.Name name
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.Server ms = getMysqlServer(aoServer, mysqlServer);
    com.aoindustries.aoserv.client.mysql.Database md = ms.getMysqlDatabase(name);
    if (md == null) {
      throw new IllegalArgumentException("Unable to find MysqlDatabase: " + name + " on " + mysqlServer + " on " + aoServer);
    }
    return md;
  }

  private com.aoindustries.aoserv.client.mysql.UserServer getMysqlServerUser(
      String aoServer,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.UserServer msu = getMysqlServer(aoServer, mysqlServer).getMysqlServerUser(username);
    if (msu == null) {
      throw new IllegalArgumentException("Unable to find MysqlServerUser: " + username + " on " + aoServer);
    }
    return msu;
  }

  private com.aoindustries.aoserv.client.mysql.User getMysqlUser(
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.User mu = connector.getMysql().getUser().get(username);
    if (mu == null) {
      throw new IllegalArgumentException("Unable to find MysqlUser: " + username);
    }
    return mu;
  }

  private Bind getNetBind(int pkey) throws IllegalArgumentException, IOException, SQLException {
    Bind nb = connector.getNet().getBind().get(pkey);
    if (nb == null) {
      throw new IllegalArgumentException("Unable to find NetBind: " + pkey);
    }
    return nb;
  }

  private Device getNetDevice(String server, String netDevice) throws IllegalArgumentException, SQLException, IOException {
    Device nd = getHost(server).getNetDevice(netDevice);
    if (nd == null) {
      throw new IllegalArgumentException("Unable to find NetDevice: " + netDevice + " on " + server);
    }
    return nd;
  }

  private OperatingSystem getOperatingSystem(String name) throws IllegalArgumentException, IOException, SQLException {
    OperatingSystem os = connector.getDistribution().getOperatingSystem().get(name);
    if (os == null) {
      throw new IllegalArgumentException("Unable to find OperatingSystem: " + name);
    }
    return os;
  }

  private OperatingSystemVersion getOperatingSystemVersion(String name, String version, Architecture architecture) throws IllegalArgumentException, IOException, SQLException {
    OperatingSystemVersion ov = getOperatingSystem(name).getOperatingSystemVersion(connector, version, architecture);
    if (ov == null) {
      throw new IllegalArgumentException("Unable to find OperatingSystemVersion: " + name + " version " + version + " for architecture of " + architecture);
    }
    return ov;
  }

  private PackageDefinition getPackageDefinition(int packageDefinition) throws IllegalArgumentException, IOException, SQLException {
    PackageDefinition pd = connector.getBilling().getPackageDefinition().get(packageDefinition);
    if (pd == null) {
      throw new IllegalArgumentException("Unable to find PackageDefinition: " + packageDefinition);
    }
    return pd;
  }

  private Package getPackage(Account.Name name) throws IllegalArgumentException, IOException, SQLException {
    Package pk = connector.getBilling().getPackage().get(name);
    if (pk == null) {
      throw new IllegalArgumentException("Unable to find Package: " + name);
    }
    return pk;
  }

  private com.aoindustries.aoserv.client.postgresql.Database getPostgresDatabase(
      String aoServer,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      com.aoindustries.aoserv.client.postgresql.Database.Name name
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.postgresql.Server ps = getPostgresServer(aoServer, postgresServer);
    com.aoindustries.aoserv.client.postgresql.Database pd = ps.getPostgresDatabase(name);
    if (pd == null) {
      throw new IllegalArgumentException("Unable to find PostgresDatabase: " + name + " on " + postgresServer + " on " + aoServer);
    }
    return pd;
  }

  private com.aoindustries.aoserv.client.postgresql.Server getPostgresServer(
      String aoServer,
      com.aoindustries.aoserv.client.postgresql.Server.Name name
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.postgresql.Server ps = getLinuxServer(aoServer).getPostgresServer(name);
    if (ps == null) {
      throw new IllegalArgumentException("Unable to find PostgresServer: " + name + " on " + aoServer);
    }
    return ps;
  }

  private com.aoindustries.aoserv.client.postgresql.UserServer getPostgresServerUser(
      String aoServer,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      com.aoindustries.aoserv.client.postgresql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.postgresql.UserServer psu = getPostgresServer(aoServer, postgresServer).getPostgresServerUser(username);
    if (psu == null) {
      throw new IllegalArgumentException("Unable to find PostgresServerUser: " + username + " on " + postgresServer + " on " + aoServer);
    }
    return psu;
  }

  private com.aoindustries.aoserv.client.postgresql.User getPostgresUser(com.aoindustries.aoserv.client.postgresql.User.Name username) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.postgresql.User pu = connector.getPostgresql().getUser().get(username);
    if (pu == null) {
      throw new IllegalArgumentException("Unable to find PostgresUser: " + username);
    }
    return pu;
  }

  private Host getHost(String server) throws IllegalArgumentException, SQLException, IOException {
    Host se = connector.getNet().getHost().get(server);
    if (se == null) {
      throw new IllegalArgumentException("Unable to find Host: " + server);
    }
    return se;
  }

  private ServerFarm getServerFarm(String name) throws IllegalArgumentException, SQLException, IOException {
    ServerFarm sf = connector.getInfrastructure().getServerFarm().get(name);
    if (sf == null) {
      throw new IllegalArgumentException("Unable to find ServerFarm: " + name);
    }
    return sf;
  }

  private Certificate getSslCertificate(String aoServer, String keyFileOrCertbotName) throws IllegalArgumentException, SQLException, IOException {
    for (Certificate cert : getLinuxServer(aoServer).getSslCertificates()) {
      if (
          cert.getKeyFile().toString().equals(keyFileOrCertbotName)
              || keyFileOrCertbotName.equals(cert.getCertbotName())
      ) {
        return cert;
      }
    }
    throw new IllegalArgumentException("Unable to find SslCertificate: " + keyFileOrCertbotName + " on " + aoServer);
  }

  /**
   * Gets the ticket category in "/ path" form.
   */
  private Category getTicketCategory(String path) throws IllegalArgumentException, IOException, SQLException {
    Category tc = null;
    for (String name : Strings.split(path, '/')) {
      Category newTc = connector.getReseller().getCategory().getTicketCategory(tc, name);
      if (newTc == null) {
        if (tc == null) {
          throw new IllegalArgumentException("Unable to find top-level TicketCategory: " + name);
        } else {
          throw new IllegalArgumentException("Unable to TicketCategory: " + name + " in " + tc);
        }
      }
      tc = newTc;
    }
    if (tc == null) {
      throw new IllegalArgumentException("Unable to find TicketCategory: " + path);
    }
    return tc;
  }

  private Priority getTicketPriority(String priority) throws IllegalArgumentException, IOException, SQLException {
    Priority tp = connector.getTicket().getPriority().get(priority);
    if (tp == null) {
      throw new IllegalArgumentException("Unable to find TicketPriority: " + priority);
    }
    return tp;
  }

  private TicketType getTicketType(String type) throws IllegalArgumentException, IOException, SQLException {
    TicketType tt = connector.getTicket().getTicketType().get(type);
    if (tt == null) {
      throw new IllegalArgumentException("Unable to find TicketType: " + type);
    }
    return tt;
  }

  private com.aoindustries.aoserv.client.account.User getUsername(com.aoindustries.aoserv.client.account.User.Name username) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.account.User un = connector.getAccount().getUser().get(username);
    if (un == null) {
      throw new IllegalArgumentException("Unable to find User: " + username);
    }
    return un;
  }

  private VirtualServer getVirtualServer(String virtualServer) throws IllegalArgumentException, SQLException, IOException {
    Host se = getHost(virtualServer);
    VirtualServer vs = se.getVirtualServer();
    if (vs == null) {
      throw new IllegalArgumentException("Unable to find VirtualServer: " + virtualServer);
    }
    return vs;
  }

  private VirtualDisk getVirtualDisk(String virtualServer, String device) throws IllegalArgumentException, SQLException, IOException {
    VirtualServer vs = getVirtualServer(virtualServer);
    VirtualDisk vd = vs.getVirtualDisk(device);
    if (vd == null) {
      throw new IllegalArgumentException("Unable to find VirtualDisk: " + virtualServer + ":/dev/" + device);
    }
    return vd;
  }

  private com.aoindustries.aoserv.client.net.reputation.Set getIpReputationSet(String identifier) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.net.reputation.Set set = connector.getNet().getReputation().getSet().get(identifier);
    if (set == null) {
      throw new IllegalArgumentException("Unable to find IpReputationSet: " + identifier);
    }
    return set;
  }

  /**
   * Adds a new backup {@link Host}.
   *
   * @param  hostname  the desired hostname for the server
   * @param  farm  the farm the server is part of
   * @param  owner  the package the server belongs to
   * @param  description  a description of the server
   * @param  backupHour  the hour the backup will be run if used in daemon mode,
   *                      expressed in server-local time
   * @param  osType  the type of operating system on the server
   * @param  osVersion  the version of operating system on the server
   * @param  architecture  the type of CPU(s) on the server
   * @param  username  the desired backup account username
   * @param  password  the desired backup account password
   * @param  contactPhone  the phone number to call for anything related to this server
   * @param  contactEmail  the email address to contact for anything related to this server
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link ServerFarm}, {@link Account}, {@link Architecture},
   *                                       {@link OperatingSystem}, or {@link OperatingSystemVersion}
   *
   * @see  Host
   * @see  HostTable#addBackupHost(java.lang.String, com.aoindustries.aoserv.client.infrastructure.ServerFarm, com.aoindustries.aoserv.client.billing.Package, java.lang.String, int, com.aoindustries.aoserv.client.distribution.OperatingSystemVersion, com.aoindustries.aoserv.client.account.User.Name, java.lang.String, java.lang.String, java.lang.String)
   */
  public int addBackupHost(
      String hostname,
      String farm,
      Account.Name owner,
      String description,
      int backupHour,
      String osType,
      String osVersion,
      String architecture,
      com.aoindustries.aoserv.client.account.User.Name username,
      String password,
      String contactPhone,
      String contactEmail
  ) throws IllegalArgumentException, IOException, SQLException {
    return connector.getNet().getHost().addBackupHost(
        hostname,
        getServerFarm(farm),
        getPackage(owner),
        description,
        backupHour,
        getOperatingSystemVersion(osType, osVersion, getArchitecture(architecture)),
        username,
        password,
        contactPhone,
        contactEmail
    );
  }

  /**
   * Adds a new {@link Account} to the system.
   *
   * @param  accounting  the accounting code of the new business
   * @param  contractVersion  the version number of the digitally signed contract
   * @param  defaultServer  the hostname of the default server
   * @param  parent  the parent business of the new business
   * @param  canAddBackupServers  allows backup servers to be added to the system
   * @param  canAddBusinesses  if {@code true}, the new business
   *                             is allowed to add additional businesses
   * @param  billParent  if {@code true} the parent account will be billed instead
   *                                  of this account
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the server or parent business
   *
   * @see  Account
   * @see  Host#addAccount
   */
  public void addAccount(
      Account.Name accounting,
      String contractVersion,
      String defaultServer,
      Account.Name parent,
      boolean canAddBackupServers,
      boolean canAddBusinesses,
      boolean canSeePrices,
      boolean billParent
  ) throws IllegalArgumentException, SQLException, IOException {
    if (contractVersion != null && contractVersion.length() == 0) {
      contractVersion = null;
    }
    getHost(defaultServer).addAccount(
        accounting,
        contractVersion,
        getAccount(parent),
        canAddBackupServers,
        canAddBusinesses,
        canSeePrices,
        billParent
    );
  }

  /**
   * Adds a new {@link Administrator} to an {@link Account}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  Administrator
   * @see  Account
   * @see  User#addAdministrator
   */
  public void addAdministrator(
      com.aoindustries.aoserv.client.account.User.Name username,
      String name,
      String title,
      Date birthday,
      boolean isPrivate,
      String workPhone,
      String homePhone,
      String cellPhone,
      String fax,
      Email email,
      String address1,
      String address2,
      String city,
      String state,
      String country,
      String zip,
      boolean enableEmailSupport
  ) throws IllegalArgumentException, IOException, SQLException {
    getUsername(username).addAdministrator(
        name,
        title,
        birthday,
        isPrivate,
        workPhone,
        homePhone,
        cellPhone,
        fax,
        email,
        address1,
        address2,
        city,
        state,
        country,
        zip,
        enableEmailSupport
    );
  }

  /**
   * Adds a new {@link Profile} to an {@link Account}.  The
   * profile is a complete set of contact information about a business.  New
   * profiles can be added, and they are used as the contact information, but
   * old profiles are still available.
   *
   * @exception  IllegalArgumentException  if unable to find the {@link Account}
   *
   * @see  Profile
   * @see  Account
   * @see  Account#addProfile
   */
  public int addProfile(
      Account.Name business,
      String name,
      boolean isPrivate,
      String phone,
      String fax,
      String address1,
      String address2,
      String city,
      String state,
      String country,
      String zip,
      boolean sendInvoice,
      String billingContact,
      Set<Email> billingEmail,
      String billingEmailFormat,
      String technicalContact,
      Set<Email> technicalEmail,
      String technicalEmailFormat
  ) throws IllegalArgumentException, IOException, SQLException {
    return getAccount(business).addProfile(
        name,
        isPrivate,
        phone,
        fax,
        address1,
        address2,
        city,
        state,
        country,
        zip,
        sendInvoice,
        billingContact,
        billingEmail,
        Profile.EmailFormat.valueOf(billingEmailFormat.toUpperCase(Locale.ROOT)),
        technicalContact,
        technicalEmail,
        Profile.EmailFormat.valueOf(technicalEmailFormat.toUpperCase(Locale.ROOT))
    );
  }

  /**
   * Grants an {@link Account} access to a {@link Host}.
   *
   * @param  accounting  the accounting code of the business
   * @param  host  the hostname of the server
   *
   * @return  the pkey of the new {@link AccountHost}
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the business or server
   *
   * @see  AccountHost
   * @see  Account#addAccountHost
   */
  public int addAccountHost(
      Account.Name accounting,
      String host
  ) throws IllegalArgumentException, SQLException, IOException {
    return getAccount(accounting).addAccountHost(getHost(host));
  }

  /**
   * Adds a new {@link CvsRepository} to a {@link Server}.
   *
   * @param  aoServer  the hostname of the server
   * @param  path    the full path of the repository
   * @param  username  the name of the shell account that owns the directory
   * @param  group     the group that owns the directory
   * @param  mode      the permissions of the directory
   *
   * @return  the {@code id} of the new {@link CvsRepository}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link UserServer},
   *                                       or {@link GroupServer}
   *
   * @see  Server#addCvsRepository
   */
  public int addCvsRepository(
      String aoServer,
      PosixPath path,
      com.aoindustries.aoserv.client.linux.User.Name username,
      Group.Name group,
      long mode
  ) throws IllegalArgumentException, IOException, SQLException {
    Server ao = getLinuxServer(aoServer);
    return ao.addCvsRepository(
        path,
        getLinuxServerAccount(aoServer, username),
        getLinuxServerGroup(aoServer, group),
        mode
    );
  }

  /**
   * Adds a new {@link Record} to a {@link Zone}.  Each {@link Zone}
   * can have multiple DNS records in it, each being a {@link Record}.
   *
   * @param  zone      the zone, in the <code>name.<i>topleveldomain</i>.</code> format.  Please note the
   *                   trailing period (<code>.</code>)
   * @param  domain    the part of the name before the zone or <code>@</code> for the zone itself.  For example,
   *                   the domain for the hostname of <code>www.aoindustries.com.</code> in the
   *                   <code>aoindustries.com.</code> zone is <code>www</code>.
   * @param  type      the {@link RecordType}
   * @param  priority  if a {@link RecordType#MX} or {@link RecordType#SRV} type, then the value is the priority of the record, otherwise
   *                   it is {@link Record#NO_PRIORITY}.
   * @param  weight    if a {@link RecordType#SRV} type, then the value is the weight of the record, otherwise
   *                   it is {@link Record#NO_WEIGHT}.
   * @param  port      if a {@link RecordType#SRV} type, then the value is the port of the record, otherwise
   *                   it is {@link Record#NO_PORT}.
   *
   * @return  the {@code id} of the new {@link Record}
   *
   * @exception  IOException   if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the priority is provided for a non-{@link RecordType#MX} and non-{@link RecordType#SRV} record,
   *                                       the priority is not provided for a {@link RecordType#MX} or {@link RecordType#SRV} record,
   *                                       if the weight is provided for a non-{@link RecordType#SRV} record,
   *                                       the weight is not provided for a {@link RecordType#SRV} record,
   *                                       if the port is provided for a non-{@link RecordType#SRV} record,
   *                                       the port is not provided for a {@link RecordType#SRV} record,
   *                                       the destination is not the correct format for the {@link RecordType},
   *                                       or  unable to find the {@link Zone} or {@link RecordType}
   *
   * @see  Zone#addDnsRecord
   * @see  Record
   * @see  #addDnsZone(com.aoindustries.aoserv.client.account.Account.Name, java.lang.String, com.aoapps.net.InetAddress, int)
   * @see  RecordType#checkDestination
   */
  public int addDnsRecord(
      String zone,
      String domain,
      String type,
      int priority,
      int weight,
      int port,
      short flag,
      String tag,
      String destination,
      int ttl
  ) throws IllegalArgumentException, IOException, SQLException {
    final Zone nz = getZone(zone);

    // Must be a valid type
    RecordType nt = connector.getDns().getRecordType().get(type);
    if (nt == null) {
      throw new IllegalArgumentException("Unable to find RecordType: " + type);
    }

    // Must have appropriate priority
    if (nt.hasPriority()) {
      if (priority == Record.NO_PRIORITY) {
        throw new IllegalArgumentException("priority required for type=" + type);
      } else if (priority <= 0) {
        throw new IllegalArgumentException("Invalid priority: " + priority);
      }
    } else {
      if (priority != Record.NO_PRIORITY) {
        throw new IllegalArgumentException("No priority allowed for type=" + type);
      }
    }

    // Must have appropriate weight
    if (nt.hasWeight()) {
      if (weight == Record.NO_WEIGHT) {
        throw new IllegalArgumentException("weight required for type=" + type);
      } else if (weight <= 0) {
        throw new IllegalArgumentException("Invalid weight: " + weight);
      }
    } else {
      if (weight != Record.NO_WEIGHT) {
        throw new IllegalArgumentException("No weight allowed for type=" + type);
      }
    }

    // Must have appropriate port
    if (nt.hasPort()) {
      if (port == Record.NO_PORT) {
        throw new IllegalArgumentException("port required for type=" + type);
      } else if (port < 1 || port > 65535) {
        throw new IllegalArgumentException("Invalid port: " + port);
      }
    } else {
      if (port != Record.NO_PORT) {
        throw new IllegalArgumentException("No port allowed for type=" + type);
      }
    }

    // Must have appropriate flag
    if (nt.hasFlag()) {
      if (flag == Record.NO_FLAG) {
        throw new IllegalArgumentException("flag required for type=" + type);
      } else if (flag < 0 || flag > 0xFF) {
        throw new IllegalArgumentException("Invalid flag: " + flag);
      }
    } else {
      if (flag != Record.NO_FLAG) {
        throw new IllegalArgumentException("No flag allowed for type=" + type);
      }
    }

    // Must have appropriate tag
    if (nt.hasTag()) {
      if (tag == null) {
        throw new IllegalArgumentException("tag required for type=" + type);
      }
    } else {
      if (tag != null) {
        throw new IllegalArgumentException("No tag allowed for type=" + type);
      }
    }

    // Must have a valid destination type
    nt.checkDestination(tag, destination);

    return nz.addDnsRecord(
        domain,
        nt,
        priority,
        weight,
        port,
        flag,
        tag,
        destination,
        ttl
    );
  }

  /**
   * Adds a new {@link Zone} to a system.  A {@link Zone} is one unique domain in
   * the name servers.  It is always one host up from a top level domain.  In <code><i>mydomain</i>.com.</code>
   * <code>com</code> is the top level domain, which are defined by {@link TopLevelDomain}s.
   *
   * @param  packageName  the name of the {@link Package} that owns this domain
   * @param  zone  the complete domain of the new {@link Zone}
   * @param  ip  the IP address that will be used for the default {@link Record}s
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Package} or either parameter is not in
   *                                  the proper format.
   *
   * @see  Package#addDnsZone
   * @see  Zone
   * @see  #addDnsRecord
   * @see  IpAddress
   * @see  TopLevelDomain
   */
  public void addDnsZone(
      Account.Name packageName,
      String zone,
      InetAddress ip,
      int ttl
  ) throws IllegalArgumentException, IOException, SQLException {
    if (!connector.getDns().getZone().checkDnsZone(zone)) {
      throw new IllegalArgumentException("Invalid zone: " + zone);
    }
    getPackage(packageName).addDnsZone(zone, ip, ttl);
  }

  /**
   * Forwards email addressed to an address at a {@link Domain} to
   * a different email address.  The destination email address may be any email
   * address, not just those in a {@link Domain}.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  aoServer  the hostname of the server hosting the domain
   * @param  destination  the completed email address of the final delivery address
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable find the {@link Domain}
   *
   * @see  Address#addEmailForwarding(com.aoapps.net.Email)
   * @see  Domain
   */
  public int addEmailForwarding(
      String address,
      DomainName domain,
      String aoServer,
      Email destination
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain sd = getEmailDomain(aoServer, domain);
    Address ea = sd.getEmailAddress(address);
    boolean added = false;
    if (ea == null) {
      ea = connector.getEmail().getAddress().get(sd.addEmailAddress(address));
      added = true;
    }
    try {
      return ea.addEmailForwarding(destination);
    } catch (Error | RuntimeException | IOException | SQLException e) {
      try {
        if (added && !ea.isUsed()) {
          ea.remove();
        }
      } catch (ThreadDeath td) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t = Throwables.addSuppressed(td, e);
        assert t == td;
        throw td;
      } catch (Throwable t) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t2 = Throwables.addSuppressed(e, t);
        assert t2 == e;
      }
      throw e;
    }
  }

  /**
   * Adds a new {@link com.aoindustries.aoserv.client.email.List} to the system.  When an email is sent
   * to an {@link com.aoindustries.aoserv.client.email.List}, it is immediately forwarded to all addresses
   * contained in the list.  The list may accept mail on any number of addresses
   * and forward to any number of recipients.
   * <p>
   * Even though the {@link com.aoindustries.aoserv.client.email.List} may receive email on any number of
   * addresses, each address must be part of a {@link Domain} that
   * is hosted on the same {@link Server} as the {@link com.aoindustries.aoserv.client.email.List}.
   * If email in a domain on another {@link Server} is required to be sent
   * to this list, it must be forwarded from the other {@link Server} via
   * a {@link Forwarding}.
   * </p>
   * <p>
   * The list of destinations for the {@link com.aoindustries.aoserv.client.email.List} is stored on the
   * {@link Server} in a flat file of one address per line.  This file
   * may be either manipulated through the API or used directly on the
   * filesystem.
   * </p>
   *
   * @param  aoServer  the hostname of the server the list is hosted on
   * @param  path  the name of the file that stores the list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find find the {@link Server},
   *                                       {@link UserServer}, or {@link GroupServer}
   *
   * @see  #checkEmailListPath
   * @see  #addEmailListAddress
   * @see  ListAddress
   * @see  Domain
   * @see  Forwarding
   * @see  Host
   * @see  UserServer
   * @see  GroupServer
   */
  public int addEmailList(
      String aoServer,
      PosixPath path,
      com.aoindustries.aoserv.client.linux.User.Name username,
      Group.Name group
  ) throws IllegalArgumentException, IOException, SQLException {
    return connector.getEmail().getList().addEmailList(
        path,
        getLinuxServerAccount(aoServer, username),
        getLinuxServerGroup(aoServer, group)
    );
  }

  /**
   * Adds to the list of {@link Address} to which the {@link com.aoindustries.aoserv.client.email.List}
   * will accept mail.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  path  the path of the list
   * @param  aoServer  the hostname of the server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain}
   *                                       or {@link com.aoindustries.aoserv.client.email.List}
   *
   * @see  #addEmailList
   * @see  List
   * @see  Address
   * @see  Domain
   */
  public int addEmailListAddress(
      String address,
      DomainName domain,
      PosixPath path,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain sd = getEmailDomain(aoServer, domain);
    com.aoindustries.aoserv.client.email.List el = getEmailList(aoServer, path);
    Address ea = sd.getEmailAddress(address);
    boolean added = false;
    if (ea == null) {
      ea = connector.getEmail().getAddress().get(connector.getEmail().getAddress().addEmailAddress(address, sd));
      added = true;
    }
    try {
      return el.addEmailAddress(ea);
    } catch (Error | RuntimeException | IOException | SQLException e) {
      try {
        if (added && !ea.isUsed()) {
          ea.remove();
        }
      } catch (ThreadDeath td) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t = Throwables.addSuppressed(td, e);
        assert t == td;
        throw td;
      } catch (Throwable t) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t2 = Throwables.addSuppressed(e, t);
        assert t2 == e;
      }
      throw e;
    }
  }

  /**
   * Adds a new {@link Pipe} to the system.  When an email is sent
   * to an {@link Pipe}, a process is invoked with the email pipes into
   * the process' standard input.
   *
   * @param  aoServer  the hostname of the server that the process exists on
   * @param  command  the full command line of the program to launch
   * @param  packageName  the package that this {@link Pipe} belongs to
   *
   * @return  the pkey of the new pipe
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find find the {@link Server} or
   *                                       {@link Package}
   *
   * @see  #addEmailPipeAddress
   * @see  Server#addEmailPipe
   */
  public int addEmailPipe(
      String aoServer,
      String command,
      Account.Name packageName
  ) throws IllegalArgumentException, IOException, SQLException {
    return connector.getEmail().getPipe().addEmailPipe(
        getLinuxServer(aoServer),
        command,
        getPackage(packageName)
    );
  }

  /**
   * Adds an address to the list of email addresses that will be piped to
   * an {@link Pipe}.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  pkey  the pkey of the {@link com.aoindustries.aoserv.client.email.List}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain}
   *                                       or {@link Pipe}
   *
   * @see  #addEmailPipe
   * @see  Pipe
   * @see  Address
   * @see  Domain
   */
  public int addEmailPipeAddress(
      String address,
      DomainName domain,
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    Pipe ep = connector.getEmail().getPipe().get(pkey);
    if (ep == null) {
      throw new IllegalArgumentException("Unable to find EmailPipe: " + ep);
    }
    Server ao = ep.getLinuxServer();
    Domain sd = ao.getEmailDomain(domain);
    if (sd == null) {
      throw new IllegalArgumentException("Unable to find EmailDomain: " + domain + " on " + ao.getHostname());
    }
    Address ea = sd.getEmailAddress(address);
    boolean added = false;
    if (ea == null) {
      ea = connector.getEmail().getAddress().get(sd.addEmailAddress(address));
      added = true;
    }
    try {
      return ep.addEmailAddress(ea);
    } catch (Error | RuntimeException | IOException | SQLException e) {
      try {
        if (added && !ea.isUsed()) {
          ea.remove();
        }
      } catch (ThreadDeath td) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t = Throwables.addSuppressed(td, e);
        assert t == td;
        throw td;
      } catch (Throwable t) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t2 = Throwables.addSuppressed(e, t);
        assert t2 == e;
      }
      throw e;
    }
  }

  /**
   * Adds a {@link FileReplicationSetting} to a {@link FileReplication}.
   *
   * @param  replication  the pkey of the FailoverFileReplication
   * @param  path  the path that is being configured
   * @param  backupEnabled  the enabled flag for the prefix
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link FileReplication}, or {@link Package}
   *
   * @return  the pkey of the newly created {@link FileReplicationSetting}
   *
   * @see  FileReplication#addFileBackupSetting
   * @see  FileReplicationSetting
   */
  public int addFileBackupSetting(
      int replication,
      String path,
      boolean backupEnabled,
      boolean required
  ) throws IllegalArgumentException, IOException, SQLException {
    FileReplication ffr = getConnector().getBackup().getFileReplication().get(replication);
    if (ffr == null) {
      throw new IllegalArgumentException("Unable to find FailoverFileReplication: " + replication);
    }
    return ffr.addFileBackupSetting(
        path,
        backupEnabled,
        required
    );
  }

  /**
   * Flags a {@link com.aoindustries.aoserv.client.linux.User} as being a {@link GuestUser}.  Once
   * flagged, FTP connections as that user will be limited to transfers in their
   * home directory.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  #addLinuxAccount(com.aoindustries.aoserv.client.linux.User.Name, com.aoindustries.aoserv.client.linux.Group.Name, com.aoindustries.aoserv.client.linux.User.Gecos, com.aoindustries.aoserv.client.linux.User.Gecos, com.aoindustries.aoserv.client.linux.User.Gecos, com.aoindustries.aoserv.client.linux.User.Gecos, java.lang.String, com.aoindustries.aoserv.client.linux.PosixPath)
   * @see  com.aoindustries.aoserv.client.linux.User#addFtpGuestUser()
   */
  public void addFtpGuestUser(
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).addFtpGuestUser();
  }

  /**
   * Finds a PHP version given its version, allowing prefix matches.
   */
  private SoftwareVersion findPhpVersion(Server aoServer, String phpVersion) throws IllegalArgumentException, IOException, SQLException {
    if (phpVersion == null || phpVersion.isEmpty()) {
      return null;
    }
    String prefix = phpVersion;
    if (!prefix.endsWith(".")) {
      prefix += '.';
    }
    int osvId = aoServer.getHost().getOperatingSystemVersion_id();
    List<SoftwareVersion> matches = new ArrayList<>();
    for (SoftwareVersion tv : connector.getDistribution().getSoftwareVersion()) {
      if (
          tv.getOperatingSystemVersion_id() == osvId
              && tv.getTechnologyName_name().equals(Software.PHP)
              && (
              tv.getVersion().equals(phpVersion)
                  || tv.getVersion().startsWith(prefix)
          )
      ) {
        matches.add(tv);
      }
    }
    if (matches.isEmpty()) {
      throw new IllegalArgumentException("Unable to find PHP version: " + phpVersion);
    } else if (matches.size() > 1) {
      StringBuilder sb = new StringBuilder();
      sb.append("Found more than one matching PHP version, please be more specific: ");
      boolean didOne = false;
      for (SoftwareVersion match : matches) {
        if (didOne) {
          sb.append(", ");
        } else {
          didOne = true;
        }
        sb.append(match.getVersion());
      }
      throw new IllegalArgumentException(sb.toString());
    } else {
      return matches.get(0);
    }
  }

  /**
   * Finds a Tomcat version given its version, allowing prefix matches.
   */
  private com.aoindustries.aoserv.client.web.tomcat.Version findTomcatVersion(Server aoServer, String version) throws IllegalArgumentException, IOException, SQLException {
    String prefix = version;
    if (!prefix.endsWith(".")) {
      prefix += '.';
    }
    int osvId = aoServer.getHost().getOperatingSystemVersion_id();
    List<com.aoindustries.aoserv.client.web.tomcat.Version> matches = new ArrayList<>();
    for (com.aoindustries.aoserv.client.web.tomcat.Version htv : connector.getWeb_tomcat().getVersion()) {
      SoftwareVersion tv = htv.getTechnologyVersion(connector);
      if (
          tv.getOperatingSystemVersion_id() == osvId
              && (
              tv.getVersion().equals(version)
                  || tv.getVersion().startsWith(prefix)
          )
      ) {
        matches.add(htv);
      }
    }
    if (matches.isEmpty()) {
      throw new IllegalArgumentException("Unable to find Tomcat version: " + version);
    } else if (matches.size() > 1) {
      StringBuilder sb = new StringBuilder();
      sb.append("Found more than one matching Tomcat version, please be more specific: ");
      boolean didOne = false;
      for (com.aoindustries.aoserv.client.web.tomcat.Version match : matches) {
        if (didOne) {
          sb.append(", ");
        } else {
          didOne = true;
        }
        sb.append(match.getTechnologyVersion(connector).getVersion());
      }
      throw new IllegalArgumentException(sb.toString());
    } else {
      return matches.get(0);
    }
  }

  /**
   * Adds a new {@link com.aoindustries.aoserv.client.web.jboss.Site} to the system.  An {@link com.aoindustries.aoserv.client.web.jboss.Site} is
   * an {@link Site} that uses the Tomcat servlet engine and JBoss as an EJB container.
   *
   * @param  aoServer  the hostname of the {@link Server}
   * @param  siteName  the name of the {@link PrivateTomcatSite}
   * @param  packageName  the name of the {@link Package}
   * @param  jvmUsername  the username of the {@link com.aoindustries.aoserv.client.linux.User} that the Java VM
   *                      will run as
   * @param  groupName  the name of the {@link Group} that the web site will
   *                    be owned by
   * @param  serverAdmin  the email address of the person who is responsible for the site
   *                      content and reliability
   * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
   *                    comes at the price of less request control through Tomcat
   * @param  ipAddress  the {@link IpAddress} that the web site will bind to.  In
   *                    order for HTTP requests to succeed, {@link Record} entries
   *                    must point the hostnames of this {@link PrivateTomcatSite} to this
   *                    {@link IpAddress}.  If {@code null}, the system will assign a
   *                                      shared IP address.
   * @param  primaryHttpHostname  the primary hostname of the {@link PrivateTomcatSite} for the
   *                              HTTP protocol
   * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
   *                           {@code null} for none
   * @param  jbossVersion  the version number of <code>JBoss</code> to install in the site
   *
   * @return  the {@code id} of the new {@link PrivateTomcatSite}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find a referenced object or a
   *                                       parameter is not in the right format
   *
   * @see  Site
   */
  public int addHttpdJbossSite(
      String aoServer,
      String siteName,
      Account.Name packageName,
      com.aoindustries.aoserv.client.linux.User.Name jvmUsername,
      Group.Name groupName,
      Email serverAdmin,
      boolean useApache,
      InetAddress ipAddress,
      String netDevice,
      DomainName primaryHttpHostname,
      DomainName[] altHttpHostnames,
      String jbossVersion
  ) throws IllegalArgumentException, SQLException, IOException {
    final Server ao = getLinuxServer(aoServer);
    checkSiteName(siteName);

    IpAddress ip;
    if (netDevice != null && (netDevice = netDevice.trim()).length() == 0) {
      netDevice = null;
    }
    if (ipAddress != null && netDevice != null) {
      ip = getIpAddress(aoServer, netDevice, ipAddress);
    } else if (ipAddress == null && netDevice == null) {
      ip = null;
    } else {
      throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
    }
    com.aoindustries.aoserv.client.web.jboss.Version hjv = connector.getWeb_jboss().getVersion().getHttpdJbossVersion(jbossVersion, ao.getHost().getOperatingSystemVersion());
    if (hjv == null) {
      throw new IllegalArgumentException("Unable to find HttpdJbossVersion: " + jbossVersion);
    }
    return ao.addHttpdJbossSite(
        siteName,
        getPackage(packageName),
        getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
        getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
        serverAdmin,
        useApache,
        ip,
        primaryHttpHostname,
        altHttpHostnames,
        hjv
    );
  }

  /**
   * Adds a new {@link SharedTomcat} to a server.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link UserServer},
   *                                       the {@link GroupServer}, or the {@link Server}
   *
   * @see  SharedTomcat
   * @see  UserServer
   * @see  GroupServer
   * @see  Host
   */
  public int addHttpdSharedTomcat(
      String name,
      String aoServer,
      String version,
      com.aoindustries.aoserv.client.linux.User.Name linuxServerAccount,
      Group.Name linuxServerGroup
  ) throws IllegalArgumentException, SQLException, IOException {
    Server ao = getLinuxServer(aoServer);
    return ao.addHttpdSharedTomcat(
        name,
        findTomcatVersion(ao, version),
        getLinuxServerAccount(aoServer, linuxServerAccount),
        getLinuxServerGroup(aoServer, linuxServerGroup)
    );
  }

  /**
   * Adds a new {@link VirtualHostName} to a {@link VirtualHost}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHost}
   */
  public int addVirtualHostName(
      int hsbPkey,
      DomainName hostname
  ) throws IllegalArgumentException, IOException, SQLException {
    VirtualHost hsb = connector.getWeb().getVirtualHost().get(hsbPkey);
    if (hsb == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteBind: " + hsbPkey);
    }
    return hsb.addVirtualHostName(hostname);
  }

  /**
   * Adds a new {@link Location} to a {@link Site}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @throws IllegalArgumentException if unable to find the {@link Site}
   */
  public int addHttpdSiteAuthenticatedLocation(
      String siteName,
      String aoServer,
      String path,
      boolean isRegularExpression,
      String authName,
      PosixPath authGroupFile,
      PosixPath authUserFile,
      String require,
      String handler
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getLinuxServer(aoServer).getHttpdSite(siteName);
    if (hs == null) {
      throw new IllegalArgumentException("Unable to find HttpdSite: " + siteName + " on " + aoServer);
    }
    return hs.addHttpdSiteAuthenticatedLocation(path, isRegularExpression, authName, authGroupFile, authUserFile, require, handler);
  }

  /**
   * Updates a {@link Location}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @throws IllegalArgumentException if unable to find the {@link Site}
   */
  public void setHttpdSiteAuthenticatedLocationAttributes(
      String siteName,
      String aoServer,
      String path,
      boolean isRegularExpression,
      String authName,
      PosixPath authGroupFile,
      PosixPath authUserFile,
      String require,
      String handler
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getLinuxServer(aoServer).getHttpdSite(siteName);
    if (hs == null) {
      throw new IllegalArgumentException("Unable to find HttpdSite: " + siteName + " on " + aoServer);
    }
    Location hsal = null;
    for (Location location : hs.getHttpdSiteAuthenticatedLocations()) {
      if (path.equals(location.getPath())) {
        hsal = location;
        break;
      }
    }
    if (hsal == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteAuthenticatedLocation: " + siteName + " on " + aoServer + " at " + path);
    }
    hsal.setAttributes(path, isRegularExpression, authName, authGroupFile, authUserFile, require, handler);
  }

  /**
   * Adds a new {@link Context} to a {@link com.aoindustries.aoserv.client.web.tomcat.Site}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   */
  public int addHttpdTomcatContext(
      String siteName,
      String aoServer,
      String className,
      boolean cookies,
      boolean crossContext,
      PosixPath docBase,
      boolean override,
      String path,
      boolean privileged,
      boolean reloadable,
      boolean useNaming,
      String wrapperClass,
      int debug,
      PosixPath workDir,
      boolean serverXmlConfigured
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    return hts.addHttpdTomcatContext(
        className == null || (className = className.trim()).length() == 0 ? null : className,
        cookies,
        crossContext,
        docBase,
        override,
        path,
        privileged,
        reloadable,
        useNaming,
        wrapperClass == null || (wrapperClass = wrapperClass.trim()).length() == 0 ? null : wrapperClass,
        debug,
        workDir,
        serverXmlConfigured
    );
  }

  /**
   * Adds a new data source to a {@link Context}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  {@link com.aoindustries.aoserv.client.web.tomcat.Site} or {@link Context}.
   */
  public int addHttpdTomcatDataSource(
      String siteName,
      String aoServer,
      String path,
      String name,
      String driverClassName,
      String url,
      String username,
      String password,
      int maxActive,
      int maxIdle,
      int maxWait,
      String validationQuery
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    Context htc = hts.getHttpdTomcatContext(path);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + siteName + " on " + aoServer + " path='" + path + '\'');
    }
    return htc.addHttpdTomcatDataSource(
        name,
        driverClassName,
        url,
        username,
        password,
        maxActive,
        maxIdle,
        maxWait,
        validationQuery
    );
  }

  /**
   * Adds a new parameter to a {@link Context}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  {@link com.aoindustries.aoserv.client.web.tomcat.Site} or {@link Context}.
   */
  public int addHttpdTomcatParameter(
      String siteName,
      String aoServer,
      String path,
      String name,
      String value,
      boolean override,
      String description
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    Context htc = hts.getHttpdTomcatContext(path);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + siteName + " on " + aoServer + " path='" + path + '\'');
    }
    return htc.addHttpdTomcatParameter(
        name,
        value,
        override,
        description
    );
  }

  /**
   * Adds a new {@link JkMount} to a {@link Site}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   *
   * @see  com.aoindustries.aoserv.client.web.tomcat.Site#addJkMount(java.lang.String, boolean)
   */
  public int addHttpdTomcatSiteJkMount(
      String siteName,
      String aoServer,
      String path,
      boolean mount
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    return hts.addJkMount(path, mount);
  }

  /**
   * Removes a {@link JkMount} from a {@link Site}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   *
   * @see  JkMount#remove()
   */
  public void removeHttpdTomcatSiteJkMount(
      String siteName,
      String aoServer,
      String path
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    JkMount match = null;
    for (JkMount htsjm : hts.getJkMounts()) {
      if (htsjm.getPath().equals(path)) {
        match = htsjm;
        break;
      }
    }
    if (match == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSiteJkMount: " + siteName + " on " + aoServer + " at " + path);
    }
    match.remove();
  }

  /**
   * Adds a new {@link SharedTomcatSite} to the system.  An {@link SharedTomcatSite} is
   * an {@link Site} that uses a shared Tomcat servlet engine in a virtual-hosting configuration.  It
   * hosts multiple sites per Java VM.
   *
   * @param  aoServer  the hostname of the {@link Server}
   * @param  siteName  the name of the {@link SharedTomcatSite}
   * @param  packageName  the name of the {@link Package}
   * @param  jvmUsername  the username of the {@link com.aoindustries.aoserv.client.linux.User} that the Java VM
   *                      will run as
   * @param  groupName  the name of the {@link Group} that the web site will
   *                    be owned by
   * @param  serverAdmin  the email address of the person who is responsible for the site
   *                      content and reliability
   * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
   *                    comes at the price of less request control through Tomcat
   * @param  ipAddress  the {@link IpAddress} that the web site will bind to.  In
   *                    order for HTTP requests to succeed, {@link Record} entries
   *                    must point the hostnames of this {@link SharedTomcatSite} to this
   *                    {@link IpAddress}.  If {@code null}, the system will assign a
   *                    shared IP address.
   * @param  primaryHttpHostname  the primary hostname of the {@link SharedTomcatSite} for the
   *                    HTTP protocol
   * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
   *                    {@code null} for none
   * @param  sharedTomcatName   the shared Tomcat JVM under which this site runs
   *
   * @return  the {@code id} of the new {@link SharedTomcatSite}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find a referenced object or a
   *                                       parameter is not in the right format
   *
   * @see  Server#addHttpdTomcatSharedSite
   * @see  SharedTomcatSite
   * @see  Site
   * @see  com.aoindustries.aoserv.client.web.tomcat.Site
   */
  public int addHttpdTomcatSharedSite(
      String aoServer,
      String siteName,
      Account.Name packageName,
      com.aoindustries.aoserv.client.linux.User.Name jvmUsername,
      Group.Name groupName,
      Email serverAdmin,
      boolean useApache,
      InetAddress ipAddress,
      String netDevice,
      DomainName primaryHttpHostname,
      DomainName[] altHttpHostnames,
      String sharedTomcatName
  ) throws IllegalArgumentException, SQLException, IOException {
    final Server ao = getLinuxServer(aoServer);
    checkSiteName(siteName);

    IpAddress ip;
    if (netDevice != null && (netDevice = netDevice.trim()).length() == 0) {
      netDevice = null;
    }
    if (ipAddress != null && netDevice != null) {
      ip = getIpAddress(aoServer, netDevice, ipAddress);
    } else if (ipAddress == null && netDevice == null) {
      ip = null;
    } else {
      throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
    }
    SharedTomcat sht = ao.getHttpdSharedTomcat(sharedTomcatName);
    if (sht == null) {
      throw new IllegalArgumentException("Unable to find HttpdSharedTomcat: " + sharedTomcatName + " on " + aoServer);
    }

    return ao.addHttpdTomcatSharedSite(
        siteName,
        getPackage(packageName),
        getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
        getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
        serverAdmin,
        useApache,
        ip,
        primaryHttpHostname,
        altHttpHostnames,
        sharedTomcatName
    );
  }

  /**
   * Adds a new {@link PrivateTomcatSite} to the system.  An {@link PrivateTomcatSite} is
   * an {@link Site} that contains a Tomcat servlet engine in the standard configuration.  It
   * only hosts one site per Java VM, but is arranged in the stock Tomcat structure and uses no
   * special code.
   *
   * @param  aoServer  the hostname of the {@link Server}
   * @param  siteName  the name of the {@link PrivateTomcatSite}
   * @param  packageName  the name of the {@link Package}
   * @param  jvmUsername  the username of the {@link com.aoindustries.aoserv.client.linux.User} that the Java VM
   *                      will run as
   * @param  groupName  the name of the {@link Group} that the web site will
   *                    be owned by
   * @param  serverAdmin  the email address of the person who is responsible for the site
   *                    content and reliability
   * @param  useApache  instructs the system to host static content, shtml, CGI, and PHP using Apache,
   *                    comes at the price of less request control through Tomcat
   * @param  ipAddress  the {@link IpAddress} that the web site will bind to.  In
   *                    order for HTTP requests to succeed, {@link Record} entries
   *                    must point the hostnames of this {@link PrivateTomcatSite} to this
   *                    {@link IpAddress}.  If {@code null}, the system will assign a
   *                    shared IP address.
   * @param  primaryHttpHostname  the primary hostname of the {@link PrivateTomcatSite} for the
   *                              HTTP protocol
   * @param  altHttpHostnames  any number of alternate hostnames for the HTTP protocol or
   *                           {@code null} for none
   * @param  tomcatVersion  the version number of <code>Tomcat</code> to install in the site
   *
   * @return  the {@code id} of the new {@link PrivateTomcatSite}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find a referenced object or a
   *                                       parameter is not in the right format
   *
   * @see  Server#addHttpdTomcatStdSite
   * @see  PrivateTomcatSite
   * @see  Site
   * @see  Site
   */
  public int addHttpdTomcatStdSite(
      String aoServer,
      String siteName,
      Account.Name packageName,
      com.aoindustries.aoserv.client.linux.User.Name jvmUsername,
      Group.Name groupName,
      Email serverAdmin,
      boolean useApache,
      InetAddress ipAddress,
      String netDevice,
      DomainName primaryHttpHostname,
      DomainName[] altHttpHostnames,
      String tomcatVersion
  ) throws IllegalArgumentException, SQLException, IOException {
    final Server ao = getLinuxServer(aoServer);
    checkSiteName(siteName);

    IpAddress ip;
    if (netDevice != null && (netDevice = netDevice.trim()).length() == 0) {
      netDevice = null;
    }
    if (ipAddress != null && netDevice != null) {
      ip = getIpAddress(aoServer, netDevice, ipAddress);
    } else if (ipAddress == null && netDevice == null) {
      ip = null;
    } else {
      throw new IllegalArgumentException("ip_address and net_device must both be null or both be not null");
    }
    return ao.addHttpdTomcatStdSite(
        siteName,
        getPackage(packageName),
        getLinuxServerAccount(aoServer, jvmUsername).getLinuxAccount(),
        getLinuxServerGroup(aoServer, groupName).getLinuxGroup(),
        serverAdmin,
        useApache,
        ip,
        primaryHttpHostname,
        altHttpHostnames,
        findTomcatVersion(ao, tomcatVersion)
    );
  }

  /**
   * Adds an {@link Address} to a {@link com.aoindustries.aoserv.client.linux.User}.  Not all
   * {@link com.aoindustries.aoserv.client.linux.User}s may be used as an email inbox.  The {@link UserType}
   * of the account determines which accounts may store email.  When email is allowed for the account,
   * an {@link Address} is associated with the account as a {@link InboxAddress}.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  aoServer  the hostname of the server storing the email account
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User} to route the emails to
   *
   * @return  the pkey of the new LinuxAccAddress
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain} or
   *                                       {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  UserServer#addEmailAddress
   * @see  InboxAddress
   * @see  #addLinuxAccount
   * @see  #addEmailDomain
   * @see  Address
   */
  public int addLinuxAccAddress(
      String address,
      DomainName domain,
      String aoServer,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain sd = getEmailDomain(aoServer, domain);
    UserServer lsa = getLinuxServerAccount(aoServer, username);
    Address ea = sd.getEmailAddress(address);
    boolean added;
    if (ea == null) {
      ea = connector.getEmail().getAddress().get(sd.addEmailAddress(address));
      added = true;
    } else {
      added = false;
    }
    try {
      return lsa.addEmailAddress(ea);
    } catch (Error | RuntimeException | IOException | SQLException e) {
      try {
        if (added && !ea.isUsed()) {
          ea.remove();
        }
      } catch (ThreadDeath td) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t = Throwables.addSuppressed(td, e);
        assert t == td;
        throw td;
      } catch (Throwable t) {
        @SuppressWarnings("ThrowableResultIgnored")
        Throwable t2 = Throwables.addSuppressed(e, t);
        assert t2 == e;
      }
      throw e;
    }
  }

  /**
   * Adds a new {@link com.aoindustries.aoserv.client.linux.User} the system.  A {@link com.aoindustries.aoserv.client.linux.User} does not
   * grant access to any {@link Server servers}, {@link #addLinuxServerAccount(com.aoindustries.aoserv.client.linux.User.Name, java.lang.String, com.aoindustries.aoserv.client.linux.PosixPath)} must be used
   * after the {@link com.aoindustries.aoserv.client.linux.User} has been created.
   *
   * @param  username  the username of the new {@link com.aoindustries.aoserv.client.linux.User}
   * @param  primaryGroup  the primary group of the new account
   * @param  name  the account's full name
   * @param  officeLocation  optional office location available via the Unix <code>finger</code> command
   * @param  officePhone  optional phone number available via the Unix <code>finger</code> command
   * @param  homePhone  optional home phone number available vie the Unix <code>finger</code> command
   * @param  type  the {@link UserType}
   * @param  shell  the login {@link Shell}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the name is not a valid format or unable to find
   *                                       the {@link User}, {@link UserType},
   *                                       or {@link Shell}
   *
   * @see  User#addLinuxAccount
   * @see  #addUsername
   * @see  #addLinuxServerAccount
   * @see  User
   * @see  UserType
   * @see  UserServer
   */
  public void addLinuxAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      Group.Name primaryGroup,
      Gecos name,
      Gecos officeLocation,
      Gecos officePhone,
      Gecos homePhone,
      String type,
      PosixPath shell
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    // Make sure group exists
    Group lg = getLinuxGroup(primaryGroup);
    UserType lat = connector.getLinux().getUserType().get(type);
    if (lat == null) {
      throw new IllegalArgumentException("Unable to find LinuxAccountType: " + type);
    }
    Shell sh = connector.getLinux().getShell().get(shell);
    if (sh == null) {
      throw new IllegalArgumentException("Unable to find Shell: " + shell);
    }
    un.addLinuxAccount(
        primaryGroup,
        name,
        officeLocation,
        officePhone,
        homePhone,
        type,
        shell
    );
  }

  /**
   * Adds a {@link Group} to the system.  After adding the {@link Group}, the group
   * may be added to a {@link Server} via a {@link GroupServer}.  Also, {@link com.aoindustries.aoserv.client.linux.User}s
   * may be granted access to the group using {@link GroupUser}.
   *
   * @param  name  the name of the new {@link Group}
   * @param  packageName  the name of the {@link Package} that the group belongs to
   * @param  type  the {@link GroupType}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Package} or
   *                                       {@link GroupType}
   *
   * @see  Package#addLinuxGroup
   * @see  Group
   * @see  GroupType
   * @see  Package
   * @see  #addLinuxServerGroup
   * @see  #addLinuxGroupAccount
   */
  public void addLinuxGroup(
      Group.Name name,
      Account.Name packageName,
      String type
  ) throws IllegalArgumentException, IOException, SQLException {
    GroupType lgt = connector.getLinux().getGroupType().get(type);
    if (lgt == null) {
      throw new IllegalArgumentException("Unable to find LinuxGroupType: " + type);
    }
    connector.getLinux().getGroup().addLinuxGroup(
        name,
        getPackage(packageName),
        type
    );
  }

  /**
   * Once a {@link com.aoindustries.aoserv.client.linux.User} and a {@link Group} have been established,
   * permission for the {@link com.aoindustries.aoserv.client.linux.User} to access the {@link Group} may
   * be granted using a {@link GroupUser}.
   *
   * @param  group  the name of the {@link Group}
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Group} or
   *                                       {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  Group#addLinuxAccount(com.aoindustries.aoserv.client.linux.User)
   * @see  GroupUser
   * @see  Group
   * @see  User
   * @see  UserServer
   * @see  GroupServer
   */
  public int addLinuxGroupAccount(
      Group.Name group,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxGroup(group).addLinuxAccount(getLinuxAccount(username));
  }

  /**
   * Grants a {@link com.aoindustries.aoserv.client.linux.User} access to a {@link Server}.  The primary
   * {@link Group} for this account must already have a {@link GroupServer}
   * for the {@link Server}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  home  the home directory of the user, typically <code>/home/<i>username</i></code>.
   *                  If {@code null}, the {@linkplain UserServer#getDefaultHomeDirectory(com.aoindustries.aoserv.client.linux.User.Name) default home directory} for <code>username</code>
   *                  is used.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}, {@link Host}
   *                                       or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#addLinuxServerAccount
   * @see  #addLinuxAccount
   * @see  #addLinuxGroupAccount
   * @see  #addLinuxServerGroup
   * @see  Server
   */
  public int addLinuxServerAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      PosixPath home
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount(username);
    Server ao = getLinuxServer(aoServer);
    if (home == null) {
      home = UserServer.getDefaultHomeDirectory(username);
    }
    return la.addLinuxServerAccount(ao, home);
  }

  /**
   * Grants a {@link Group} access to a {@link Server}.  If the group is
   * the primary {@link Group} for any {@link com.aoindustries.aoserv.client.linux.User} that will be
   * added to the {@link Server}, the {@link Group} must be added to the
   * {@link Server} first via a {@link GroupServer}.
   *
   * @param  group  the name of the {@link Group}
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Group} or
   *                                       {@link Server}
   *
   * @see  Group#addLinuxServerGroup
   * @see  #addLinuxGroup
   * @see  #addLinuxGroupAccount
   * @see  Host
   */
  public int addLinuxServerGroup(
      Group.Name group,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxGroup(group).addLinuxServerGroup(getLinuxServer(aoServer));
  }

  /**
   * Adds a new {@link MajordomoList} to a {@link MajordomoServer}.
   *
   * @param  domain  the domain of the {@link MajordomoServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  listName  the name of the new list
   *
   * @return  the pkey of the new {@link com.aoindustries.aoserv.client.email.List}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the name is not valid or unable to find the
   *                                  {@link Server}, {@link Domain}, or
   *                                  {@link MajordomoServer}
   *
   * @see  MajordomoServer#addMajordomoList
   * @see  #removeEmailList
   */
  public int addMajordomoList(
      DomainName domain,
      String aoServer,
      String listName
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoServer ms = ed.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    checkMajordomoListName(listName);
    return ms.addMajordomoList(listName);
  }

  /**
   * Adds a new {@link MajordomoServer} to an {@link Domain}.
   *
   * @param  domain  the domain of the {@link Domain}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  linuxAccount  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  linuxGroup  the naem of the {@link Group}
   * @param  version  the version of the {@link MajordomoVersion}
   *
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                  {@link Domain}, {@link UserServer},
   *                                  {@link GroupServer}, or {@link MajordomoVersion}
   *
   * @see  Domain#addMajordomoServer
   * @see  #removeMajordomoServer
   */
  public void addMajordomoServer(
      DomainName domain,
      String aoServer,
      com.aoindustries.aoserv.client.linux.User.Name linuxAccount,
      Group.Name linuxGroup,
      String version
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoVersion mv = connector.getEmail().getMajordomoVersion().get(version);
    if (mv == null) {
      throw new IllegalArgumentException("Unable to find MajordomoVersion: " + version);
    }
    ed.addMajordomoServer(
        getLinuxServerAccount(aoServer, linuxAccount),
        getLinuxServerGroup(aoServer, linuxGroup),
        mv
    );
  }

  /**
   * Adds a new {@link com.aoindustries.aoserv.client.mysql.Database} to the system.  Once added, {@link com.aoindustries.aoserv.client.mysql.User}s may
   * be granted access to the {@link com.aoindustries.aoserv.client.mysql.Database} using a {@link com.aoindustries.aoserv.client.mysql.DatabaseUser}.
   * <p>
   * Because updates the the MySQL configurations are batched, the database may not be immediately
   * created in the MySQL system.  To ensure the database is ready for use, call {@link #waitForMysqlDatabaseRebuild(java.lang.String)}.
   * </p>
   *
   * @param  name  the name of the new database
   * @param  aoServer  the hostname of the {@link Server}
   * @param  packageName  the name of the {@link Package} that owns the database
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the database name is not valid or unable to
   *                                       find the {@link Server} or {@link Package}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Server#addMysqlDatabase
   * @see  #addMysqlUser
   * @see  #addMysqlServerUser
   * @see  #addMysqlDbUser
   * @see  #removeMysqlDatabase
   * @see  #waitForMysqlDatabaseRebuild
   */
  public int addMysqlDatabase(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      Account.Name packageName
  ) throws IllegalArgumentException, IOException, SQLException {
    return connector.getMysql().getDatabase().addMysqlDatabase(
        name,
        getMysqlServer(aoServer, mysqlServer),
        getPackage(packageName)
    );
  }

  /**
   * Grants a {@link com.aoindustries.aoserv.client.mysql.UserServer} permission to access a {@link com.aoindustries.aoserv.client.mysql.Database}.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.mysql.Database}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.User}
   * @param  canSelect  grants the user <code>SELECT</code> privileges
   * @param  canInsert  grants the user <code>INSERT</code> privileges
   * @param  canUpdate  grants the user <code>UPDATE</code> privileges
   * @param  canDelete  grants the user <code>DELETE</code> privileges
   * @param  canCreate  grants the user <code>CREATE</code> privileges
   * @param  canDrop  grants the user <code>DROP</code> privileges
   * @param  canIndex  grants the user <code>INDEX</code> privileges
   * @param  canAlter  grants the user <code>ALTER</code> privileges
   * @param  canCreateTempTable  grants the user <code>CREATE TEMPORARY TABLE</code> privileges
   * @param  canLockTables  grants the user <code>LOCK TABLE</code> privileges
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                       {@link com.aoindustries.aoserv.client.mysql.Database},
   *                                       or {@link com.aoindustries.aoserv.client.mysql.UserServer}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Database#addMysqlServerUser
   * @see  #addMysqlUser
   * @see  #addMysqlServerUser
   * @see  #addMysqlDatabase
   */
  public int addMysqlDbUser(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      com.aoindustries.aoserv.client.mysql.User.Name username,
      boolean canSelect,
      boolean canInsert,
      boolean canUpdate,
      boolean canDelete,
      boolean canCreate,
      boolean canDrop,
      boolean canReference,
      boolean canIndex,
      boolean canAlter,
      boolean canCreateTempTable,
      boolean canLockTables,
      boolean canCreateView,
      boolean canShowView,
      boolean canCreateRoutine,
      boolean canAlterRoutine,
      boolean canExecute,
      boolean canEvent,
      boolean canTrigger
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.Database md = getMysqlDatabase(aoServer, mysqlServer, name);
    return connector.getMysql().getDatabaseUser().addMysqlDbUser(
        md,
        getMysqlServerUser(aoServer, mysqlServer, username),
        canSelect,
        canInsert,
        canUpdate,
        canDelete,
        canCreate,
        canDrop,
        canReference,
        canIndex,
        canAlter,
        canCreateTempTable,
        canLockTables,
        canCreateView,
        canShowView,
        canCreateRoutine,
        canAlterRoutine,
        canExecute,
        canEvent,
        canTrigger
    );
  }

  /**
   * Grants a {@link com.aoindustries.aoserv.client.mysql.User} access to a {@link Server} by adding a
   * {@link com.aoindustries.aoserv.client.mysql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.User}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  host  the host the user is allowed to connect from, almost always
   *               {@link com.aoindustries.aoserv.client.mysql.UserServer#ANY_LOCAL_HOST} because the host limitation
   *               is provided on a per-database level by {@link com.aoindustries.aoserv.client.mysql.DatabaseUser}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.mysql.User} or
   *                                       {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.mysql.User#addMysqlServerUser
   * @see  com.aoindustries.aoserv.client.mysql.UserServer#ANY_LOCAL_HOST
   * @see  #addMysqlUser
   * @see  #addMysqlDbUser
   */
  public int addMysqlServerUser(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      String host
  ) throws IllegalArgumentException, IOException, SQLException {
    return getMysqlUser(username).addMysqlServerUser(getMysqlServer(aoServer, mysqlServer), host == null || host.length() == 0 ? null : host);
  }

  /**
   * Adds a {@link com.aoindustries.aoserv.client.mysql.User} to the system.  A {@link com.aoindustries.aoserv.client.mysql.User} does not
   * exist on any {@link Server}, it merely indicates that a {@link User}
   * will be used for accessing a {@link com.aoindustries.aoserv.client.mysql.Database}.  In order to grant
   * the new {@link com.aoindustries.aoserv.client.mysql.User} access to a {@link com.aoindustries.aoserv.client.mysql.Database}, first
   * add a {@link com.aoindustries.aoserv.client.mysql.UserServer} on the same {@link Server} as the
   * {@link com.aoindustries.aoserv.client.mysql.Database}, then add a {@link com.aoindustries.aoserv.client.mysql.DatabaseUser} granting
   * permission to the {@link com.aoindustries.aoserv.client.mysql.Database}.
   *
   * @param  username  the {@link User} that will be used for accessing MySQL
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  User#addMysqlUser
   * @see  #addUsername
   * @see  #addMysqlServerUser
   * @see  #addMysqlDatabase
   * @see  #addMysqlDbUser
   * @see  User
   */
  public void addMysqlUser(
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getUsername(username).addMysqlUser();
  }

  /**
   * Adds a network bind to the system.
   *
   * @exception  IOException  if unable to access the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find a referenced object.
   *
   * @see  Host#addNetBind
   */
  public int addNetBind(
      String server,
      Account.Name packageName,
      InetAddress ipAddress,
      String netDevice,
      Port port,
      String appProtocol,
      boolean monitoringEnabled,
      Set<FirewallZone.Name> firewalldZoneNames
  ) throws IllegalArgumentException, SQLException, IOException {
    IpAddress ia = getIpAddress(server, netDevice, ipAddress);
    AppProtocol appProt = connector.getNet().getAppProtocol().get(appProtocol);
    if (appProt == null) {
      throw new IllegalArgumentException("Unable to find Protocol: " + appProtocol);
    }
    return getHost(server).addNetBind(
        getPackage(packageName),
        ia,
        port,
        appProt,
        monitoringEnabled,
        firewalldZoneNames
    );
  }

  /**
   * Whenever a credit card transaction fails, or when an account has not been paid for
   * over month, the billing contact for the {@link Account} is notified.  The details
   * of this notification are logged as a {@link NoticeLog}.
   *
   * @param  accounting  the accounting code of the {@link Account}
   * @param  billingContact  the name of the person who was contacted
   * @param  emailAddress  the email address that the email was sent to
   * @param  type  the {@link NoticeType}
   * @param  transid  the transaction ID associated with this notification or
   *                  {@link NoticeLog#NO_TRANSACTION} for none
   *
   * @exception  IOException  if unable to access the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Account},
   *                                       {@link NoticeType}, or {@link Transaction}.
   *
   * @see  NoticeLogTable#addNoticeLog(com.aoindustries.aoserv.client.account.Account, java.lang.String, com.aoapps.net.Email, com.aoindustries.aoserv.client.billing.NoticeType, com.aoindustries.aoserv.client.billing.Transaction)
   * @see  NoticeType
   * @see  Account
   * @see  Transaction
   */
  public int addNoticeLog(
      Account.Name accounting,
      String billingContact,
      Email emailAddress,
      String type,
      int transid
  ) throws IllegalArgumentException, IOException, SQLException {
    Account account = getAccount(accounting);
    NoticeType nt = connector.getBilling().getNoticeType().get(type);
    if (nt == null) {
      throw new IllegalArgumentException("Unable to find NoticeType: " + type);
    }
    Transaction trans;
    if (transid != NoticeLog.NO_TRANSACTION) {
      trans = connector.getBilling().getTransaction().get(transid);
      if (trans == null) {
        throw new IllegalArgumentException("Unable to find Transaction: " + transid);
      }
    } else {
      trans = null;
    }
    return connector.getBilling().getNoticeLog().addNoticeLog(
        account,
        billingContact,
        emailAddress,
        nt,
        trans
    );
  }

  /**
   * Each {@link Account} can have multiple {@link Package}s associated with it.
   * Each {@link Package} is an allotment of resources with a monthly charge.
   * <p>
   * To determine if this connection can set prices:
   * </p>
   * <pre>
   * SimpleAoservClient client=new SimpleAoservClient();
   *
   * boolean canSetPrices=client
   *     .getConnector()
   *     .getCurrentAdministrator()
   *     .getUsername()
   *     .getPackage()
   *     .getAccount()
   *     .canSetPrices();
   * </pre>
   *
   * @param  packageName  the name for the new package
   * @param  accounting  the accounting code of the {@link Account}
   * @param  packageDefinition  the unique identifier of the {@link PackageDefinition}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find
   *
   * @see  #addAccount
   * @see  PackageDefinition
   */
  public int addPackage(
      Account.Name packageName,
      Account.Name accounting,
      int packageDefinition
  ) throws IllegalArgumentException, IOException, SQLException {
    Account business = getAccount(accounting);
    PackageDefinition pd = getPackageDefinition(packageDefinition);

    return business.addPackage(packageName, pd);
  }

  /**
   * Adds a new {@link com.aoindustries.aoserv.client.postgresql.Database} to the system.
   * <p>
   * Because updates the the PostgreSQL configurations are batched, the database may not be immediately
   * created in the PostgreSQL system.  To ensure the database is ready for use, call
   * {@link #waitForPostgresDatabaseRebuild(java.lang.String)}.
   * </p>
   *
   * @param  name  the name of the new database
   * @param  aoServer  the hostname of the {@link Server}
   * @param  datdba  the username of the {@link com.aoindustries.aoserv.client.postgresql.UserServer} who owns the database
   * @param  encoding  the encoding of the database
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the database name is not valid or unable to
   *                                       find the {@link Server}, {@link com.aoindustries.aoserv.client.postgresql.User},
   *                                       {@link com.aoindustries.aoserv.client.postgresql.UserServer}, or {@link Encoding}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Server#addPostgresDatabase
   * @see  #addPostgresUser
   * @see  #addPostgresServerUser
   * @see  #removePostgresDatabase
   * @see  #waitForPostgresDatabaseRebuild
   * @see  Encoding
   */
  public int addPostgresDatabase(
      com.aoindustries.aoserv.client.postgresql.Database.Name name,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer,
      com.aoindustries.aoserv.client.postgresql.User.Name datdba,
      String encoding,
      boolean enablePostgis
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.postgresql.UserServer psu = getPostgresServerUser(aoServer, postgresServer, datdba);
    com.aoindustries.aoserv.client.postgresql.Server ps = psu.getPostgresServer();
    com.aoindustries.aoserv.client.postgresql.Version pv = ps.getVersion();
    Encoding pe = pv.getPostgresEncoding(connector, encoding);
    if (pe == null) {
      throw new IllegalArgumentException("Unable to find PostgresEncoding for PostgresVersion " + pv.getTechnologyVersion(connector).getVersion() + ": " + encoding);
    }
    if (enablePostgis && pv.getPostgisVersion(connector) == null) {
      throw new IllegalArgumentException("Unable to enable PostGIS, PostgresVersion " + pv.getTechnologyVersion(connector).getVersion() + " doesn't support PostGIS");
    }
    return ps.addPostgresDatabase(
        name,
        psu,
        pe,
        enablePostgis
    );
  }

  /**
   * Grants a {@link com.aoindustries.aoserv.client.postgresql.User} access to a {@link Server} by adding a
   * {@link com.aoindustries.aoserv.client.postgresql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.User}
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.postgresql.User}
   *                                       or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.User#addPostgresServerUser
   * @see  #addPostgresUser
   */
  public int addPostgresServerUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getPostgresUser(username).addPostgresServerUser(getPostgresServer(aoServer, postgresServer));
  }

  /**
   * Adds a {@link com.aoindustries.aoserv.client.postgresql.User} to the system.  A {@link com.aoindustries.aoserv.client.postgresql.User} does not
   * exist on any {@link Server}, it merely indicates that a {@link User}
   * will be used for accessing a {@link com.aoindustries.aoserv.client.postgresql.Database}.  In order to grant
   * the new {@link com.aoindustries.aoserv.client.postgresql.User} access to a {@link com.aoindustries.aoserv.client.postgresql.Database}, first
   * add a {@link com.aoindustries.aoserv.client.postgresql.UserServer} on the same {@link Server} as the
   * {@link com.aoindustries.aoserv.client.postgresql.Database}, then use the PostgreSQL <code>grant</code> and
   * <code>revoke</code> commands.
   *
   * @param  username  the {@link User} that will be used for accessing PostgreSQL
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  User#addPostgresUser
   * @see  #addUsername
   * @see  #addPostgresServerUser
   * @see  #addPostgresDatabase
   * @see  User
   */
  public void addPostgresUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getUsername(username).addPostgresUser();
  }

  /**
   * Adds a new {@link Domain} to a {@link Server}.  Once added, the {@link Server}
   * will accept email for the provided domain.  In order for the email to function, however, a DNS
   * {@link RecordType#MX} entry for the domain must point to a hostname that resolves to an
   * {@link IpAddress} on the {@link Server}.
   *
   * @param  domain  the email domain that will be hosted
   * @param  aoServer  the hostname of the {@link Server} that is being added
   * @param  packageName  the name of the {@link Package} that owns the email domain
   *
   * @exception  IOException  if unable to access the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the domain is not in the correct format or
   *                                       unable to find the {@link Package}
   *
   * @see  Server#addEmailDomain
   * @see  #addDnsRecord
   * @see  #addEmailForwarding
   * @see  #addEmailListAddress
   * @see  #addEmailPipeAddress
   * @see  #addLinuxAccAddress
   */
  public int addEmailDomain(
      DomainName domain,
      String aoServer,
      Account.Name packageName
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServer(aoServer).addEmailDomain(domain, getPackage(packageName));
  }

  /**
   * Grants access to the SMTP server.  Access to the SMTP server is granted when an
   * email client successfully logs into either the IMAP or POP3 servers.  If desired,
   * access to the SMTP server may also be granted from the API.  In either case,
   * the SMTP access will be revoked after 24 hours unless refresh.
   *
   * @param  packageName  the name of the {@link Package} that is granted access
   * @param  aoServer  the hostname of the {@link Server}
   * @param  host  the hostname or IP address that is being configured
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the IP address is for valid or unable to
   *                                       find the {@link Package} or {@link Server}
   *
   * @see  Package#addEmailSmtpRelay
   */
  public int addEmailSmtpRelay(
      Account.Name packageName,
      String aoServer,
      HostAddress host,
      String type,
      long duration
  ) throws IllegalArgumentException, SQLException, IOException {
    Server ao;
    if (aoServer != null && (aoServer = aoServer.trim()).length() == 0) {
      aoServer = null;
    }
    if (aoServer == null) {
      ao = null;
    } else {
      ao = getLinuxServer(aoServer);
    }
    SmtpRelayType esrt = connector.getEmail().getSmtpRelayType().get(type);
    if (esrt == null) {
      throw new SQLException("Unable to find EmailSmtpRelayType: " + type);
    }

    return getPackage(packageName).addEmailSmtpRelay(ao, host, esrt, duration);
  }

  /**
   * Adds a {@link SpamMessage}.
   *
   * @return  the pkey of the {@link SpamMessage} that was created
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to the {@link SmtpRelay}
   *
   * @see  SmtpRelay#addSpamEmailMessage
   */
  public int addSpamEmailMessage(
      int emailRelay,
      String message
  ) throws IllegalArgumentException, IOException, SQLException {
    SmtpRelay esr = connector.getEmail().getSmtpRelay().get(emailRelay);
    if (esr == null) {
      throw new IllegalArgumentException("Unable to find EmailSmtpRelay: " + emailRelay);
    }
    return esr.addSpamEmailMessage(message);
  }

  ///**
  // * Adds a new support request {@link Ticket} to the system.
  // *
  // * @param  accounting  the name of the {@link Account} that the support
  // *                      request relates to
  // * @param  business_administrator  the person to contact regarding the ticket
  // * @param  ticket_type  the {@link TicketType}
  // * @param  details  the content of the {@link Ticket}
  // * @param  deadline  the requested deadline for ticket completion or
  // *                   {@link Ticket#NO_DEADLINE} for none
  // * @param  client_priority  the priority assigned by the client
  // * @param  admin_priority  the priority assigned by the ticket administrator
  // * @param  technology  the {@link Software} that this {@link Ticket}
  // *                     relates to or {@code null} for none
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Package},
  // *                                       {@link Administrator}, {@link TicketType},
  // *                                       client {@link Priority}, admin {@link Priority},
  // *                                       or {@link Software}
  // *
  // * @see  Administrator#addTicket(Account,TicketType,String,long,TicketPriority,TicketPriority,TechnologyName,Administrator,String,String)
  // * @see  Administrator#isActiveTicketAdmin
  // * @see  Action
  // * @see  Package
  // * @see  TechnologyName
  // * @see  Ticket
  // * @see  TicketPriority
  // * @see  TicketType
  // */
  //public int addTicket(
  //  String accounting,
  //  String language,
  //  String category,
  //  String ticketType,
  //  String summary,
  //  String details,
  //  String clientPriority,
  //  String contactEmails,
  //  String contactPhoneNumbers
  //) throws IllegalArgumentException, IOException, SQLException {
  //  return connector.getTickets().addTicket(
  //    (accounting == null || accounting.length() == 0) ? null : getAccount(accounting),
  //    getLanguage(language),
  //    (category == null || category.length() == 0) ? null : getTicketCategory(category),
  //    getTicketType(ticketType),
  //    summary,
  //    (details == null || details.length() == 0) ? null : details,
  //    getTicketPriority(clientPriority),
  //    contactEmails,
  //    contactPhoneNumbers
  //  );
  //}

  ///**
  // * Adds a work entry to a {@link Ticket} when a {@link Ticket} is worked on,
  // * but not completed.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of their work
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket} or
  // *                                       {@link Administrator}
  // *
  // * @see  Ticket#actWorkEntry
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void addTicketWork(
  //  int ticket_id,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  Administrator pe=connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actWorkEntry(pe, comments);
  //}

  /**
   * Adds a new {@link Transaction} to a {@link Account}.
   *
   * @param  accountName  the accounting code of the {@link Account}
   * @param  sourceAccountName  the accounting code of the originating {@link Account}
   * @param  administrator  the username of the {@link Administrator} making
   *                        this {@link Transaction}
   * @param  type  the type as found in {@link PackageDefinitionLimit}
   * @param  description  the description
   * @param  quantity  the quantity in thousandths
   * @param  rate  the rate in hundredths
   * @param  paymentConfirmed  the confirmation status of the transaction
   *
   * @return  the transid of the new {@link Transaction}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                       {@link Account}, {@link Administrator}, {@link PackageDefinitionLimit},
   *                                       {@link PaymentType}, or <code>payment_confirmed</code>
   *
   * @see  Account#addTransaction
   * @see  Transaction
   * @see  #addAccount
   * @see  Account
   * @see  #addAdministrator
   * @see  Administrator
   * @see  TransactionType
   */
  public int addTransaction(
      int timeType,
      Timestamp time,
      Account.Name accountName,
      Account.Name sourceAccountName,
      com.aoindustries.aoserv.client.account.User.Name administrator,
      String type,
      String description,
      int quantity,
      Money rate,
      String paymentType,
      String paymentInfo,
      String processor,
      byte paymentConfirmed
  ) throws IllegalArgumentException, IOException, SQLException {
    final Account account = getAccount(accountName);
    final Account sourceAccount = getAccount(sourceAccountName);
    Administrator pe = connector.getAccount().getAdministrator().get(administrator);
    if (pe == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
    }
    TransactionType tt = connector.getBilling().getTransactionType().get(type);
    if (tt == null) {
      throw new IllegalArgumentException("Unable to find TransactionType: " + type);
    }
    PaymentType pt;
    if (paymentType == null || paymentType.length() == 0) {
      pt = null;
    } else {
      pt = connector.getPayment().getPaymentType().get(paymentType);
      if (pt == null) {
        throw new IllegalArgumentException("Unable to find PaymentType: " + paymentType);
      }
    }
    if (paymentInfo != null && paymentInfo.length() == 0) {
      paymentInfo = null;
    }
    Processor ccProcessor;
    if (processor == null || processor.length() == 0) {
      ccProcessor = null;
    } else {
      ccProcessor = connector.getPayment().getProcessor().get(processor);
      if (ccProcessor == null) {
        throw new IllegalArgumentException("Unable to find CreditCardProcessor: " + processor);
      }
    }
    return connector.getBilling().getTransaction().add(
        timeType,
        time,
        account,
        sourceAccount,
        pe,
        tt,
        description,
        quantity,
        rate,
        pt,
        paymentInfo,
        ccProcessor,
        paymentConfirmed
    );
  }

  /**
   * Adds a new {@link User} to a {@link Package}.  A username is unique to the
   * system, regardless of which service(s) it is used for.  For example, if a username is
   * allocated for use as a MySQL user for business A, business B may not use the username as
   * a PostgreSQL user.
   *
   * @param  packageName  the name of the {@link Package} that owns the {@link User}
   * @param  username  the username to add
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the username is not a valid username or
   *                                       unable to find the {@link Package}
   *
   * @see  Package#addUsername
   * @see  User
   * @see  #addPackage
   * @see  Package
   */
  public void addUsername(
      Account.Name packageName,
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getPackage(packageName).addUsername(username);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.linux.User} currently has passwords set.
   *
   * @param  username  the username of the account
   *
   * @return  an {@code int} containing {@link PasswordProtected#NONE},
   *          {@link PasswordProtected#SOME}, or {@link PasswordProtected#ALL}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.linux.User} is not found
   *
   * @see  com.aoindustries.aoserv.client.linux.User#arePasswordsSet
   * @see  #setLinuxAccountPassword
   * @see  User
   * @see  PasswordProtected
   */
  public int areLinuxAccountPasswordsSet(
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxAccount(username).arePasswordsSet();
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.mysql.User} currently has passwords set.
   *
   * @param  username  the username of the user
   *
   * @return  an {@code int} containing {@link PasswordProtected#NONE},
   *          {@link PasswordProtected#SOME}, or {@link PasswordProtected#ALL}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.mysql.User} is not found
   *
   * @see  com.aoindustries.aoserv.client.mysql.User#arePasswordsSet
   * @see  #setMysqlUserPassword
   * @see  User
   * @see  PasswordProtected
   */
  public int areMysqlUserPasswordsSet(
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    return getMysqlUser(username).arePasswordsSet();
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.postgresql.User} currently has passwords set.
   *
   * @param  username  the username of the user
   *
   * @return  an {@code int} containing {@link PasswordProtected#NONE},
   *          {@link PasswordProtected#SOME}, or {@link PasswordProtected#ALL}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.postgresql.User} is not found
   *
   * @see  com.aoindustries.aoserv.client.postgresql.User#arePasswordsSet
   * @see  #setPostgresUserPassword
   * @see  User
   * @see  PasswordProtected
   */
  public int arePostgresUserPasswordsSet(
      com.aoindustries.aoserv.client.postgresql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    return getPostgresUser(username).arePasswordsSet();
  }

  /**
   * Determines if a {@link User} currently has passwords set.
   *
   * @param  username  the username
   *
   * @return  an {@code int} containing {@link PasswordProtected#NONE},
   *          {@link PasswordProtected#SOME}, or {@link PasswordProtected#ALL}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link User} is not found
   *
   * @see  User#arePasswordsSet
   * @see  #setUsernamePassword
   * @see  User
   * @see  PasswordProtected
   */
  public int areUsernamePasswordsSet(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    return getUsername(username).arePasswordsSet();
  }

  ///**
  // * Bounces a {@link Ticket}.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the bounce
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket} or
  // *                                       {@link Administrator}
  // *
  // * @see  Ticket#actBounceTicket
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void bounceTicket(
  //  int ticket_id,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actBounceTicket(pe, comments);
  //}

  /**
   * Cancels an {@link Account}.  The {@link Account} must already be disabled.
   *
   * @param  accounting  the accounting code of the business
   * @param  reason  the reason the account is being canceled
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Account}
   *
   * @see  Account#cancel
   */
  public void cancelAccount(
      Account.Name accounting,
      String reason
  ) throws IllegalArgumentException, IOException, SQLException {
    getAccount(accounting).cancel(reason);
  }

  ///**
  // * Changes the administrative priority of a {@link Ticket}.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  priority  the new {@link Priority}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link Administrator}, or {@link Priority}
  // *
  // * @see  Ticket#actChangeAdminPriority
  // * @see  #addTicket
  // * @see  TicketPriority
  // * @see  Action
  // */
  //public void changeTicketAdminPriority(
  //  int ticket_id,
  //  String priority,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  TicketPriority pr;
  //  if (priority == null || priority.length() == 0) {
  //    pr=null;
  //  } else {
  //    pr=connector.getTicketPriorities().get(priority);
  //    if (pr == null) {
  //      throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
  //    }
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actChangeAdminPriority(pr, pe, comments);
  //}

  ///**
  // * Changes the client's priority of a {@link Ticket}.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  priority  the new {@link Priority}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link Administrator}, or {@link Priority}
  // *
  // * @see  Ticket#actChangeClientPriority
  // * @see  #addTicket
  // * @see  TicketPriority
  // * @see  Action
  // */
  //public void changeTicketClientPriority(
  //  int ticket_id,
  //  String priority,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  TicketPriority pr=connector.getTicketPriorities().get(priority);
  //  if (pr == null) {
  //    throw new IllegalArgumentException("Unable to find TicketPriority: "+priority);
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actChangeClientPriority(pr, pe, comments);
  //}

  ///**
  // * Changes the {@link TicketType} of a {@link Ticket}.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  type  the name of the new {@link TicketType}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link TicketType}, or {@link Administrator}
  // *
  // * @see  Ticket#actChangeTicketType
  // * @see  TicketType
  // * @see  #addTicket
  // * @see  TicketPriority
  // * @see  Action
  // */
  //public void changeTicketType(
  //  int ticket_id,
  //  String type,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  TicketType tt=connector.getTicketTypes().get(type);
  //  if (tt == null) {
  //    throw new IllegalArgumentException("Unable to find TicketType: "+type);
  //  }
  //  Administrator pe=connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actChangeTicketType(tt, pe, comments);
  //}

  /**
   * Checks the strength of a password that will be used for
   * a {@link Administrator}.
   *
   * @param  username  the username of the {@link Administrator} whose
   *                   password will be set
   * @param  password  the new password
   *
   * @return  a description of why the password is weak or {@code null}
   *          if all checks succeed
   *
   * @see  #setAdministratorPassword(com.aoindustries.aoserv.client.account.User.Name, java.lang.String)
   * @see  Administrator#checkPassword
   */
  public static List<PasswordChecker.Result> checkAdministratorPassword(
      com.aoindustries.aoserv.client.account.User.Name username,
      String password
  ) throws IOException {
    return Administrator.checkPassword(username, password);
  }

  /**
   * Checks the format of a {@link Zone}.
   *
   * @param  zone  the new DNS zone name, some examples include <code>aoindustries.com.</code>
   *               and <code>netspade.co.uk.</code>
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the format is not valid
   *
   * @see  ZoneTable#checkDnsZone(java.lang.String)
   * @see  Zone
   */
  public void checkDnsZone(
      String zone
  ) throws IllegalArgumentException, IOException, SQLException {
    if (!connector.getDns().getZone().checkDnsZone(zone)) {
      throw new IllegalArgumentException("Invalid DNS zone: " + zone);
    }
  }

  /**
   * Checks the format of an email list path.
   *
   * @param  aoServer  the hostname of the server the list would be hosted on
   * @param  path  the path of the list
   *
   * @exception  IllegalArgumentException  if the name is not in a valid format
   *
   * @see  com.aoindustries.aoserv.client.email.List#isValidRegularPath
   */
  public void checkEmailListPath(
      String aoServer,
      PosixPath path
  ) throws IllegalArgumentException, IOException, SQLException {
    Server ao = getLinuxServer(aoServer);
    if (
        !com.aoindustries.aoserv.client.email.List.isValidRegularPath(
            path,
            ao.getHost().getOperatingSystemVersion_id()
        )
    ) {
      throw new IllegalArgumentException("Invalid EmailList path: " + path + " on " + ao);
    }
  }

  /**
   * Checks the strength of a password that will be used for a {@link com.aoindustries.aoserv.client.linux.User} or
   * {@link UserServer}.
   *
   * @param  username  the username of the account that will have its password set
   * @param  password  the new password for the account
   *
   * @return  a {@link String} describing why the password is not secure or {@code null}
   *          if the password is strong
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#checkPassword
   * @see  #setLinuxAccountPassword
   * @see  #setLinuxServerAccountPassword
   * @see  PasswordChecker
   */
  public List<PasswordChecker.Result> checkLinuxAccountPassword(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxAccount(username).checkPassword(password);
  }

  /**
   * Checks the strength of a password that will be used for
   * a {@link com.aoindustries.aoserv.client.mysql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.UserServer} whos
   *                   password will be set
   * @param  password  the new password
   *
   * @return  a description of why the password is weak or {@code null}
   *          if all checks succeed
   *
   * @exception  IOException  if unable to load the dictionary resource
   *
   * @see  #setMysqlUserPassword
   * @see  #setMysqlServerUserPassword
   * @see  com.aoindustries.aoserv.client.mysql.User#checkPassword
   */
  public static List<PasswordChecker.Result> checkMysqlPassword(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      String password
  ) throws IOException {
    return com.aoindustries.aoserv.client.mysql.User.checkPassword(username, password);
  }

  /**
   * Checks the strength of a password that will be used for
   * a {@link com.aoindustries.aoserv.client.postgresql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.UserServer} whos
   *                   password will be set
   * @param  password  the new password
   *
   * @return  a description of why the password is weak or {@code null}
   *          if all checks succeed
   *
   * @exception  IOException  if unable to load the dictionary resource
   *
   * @see  #setPostgresUserPassword
   * @see  #setPostgresServerUserPassword
   * @see  com.aoindustries.aoserv.client.postgresql.User#checkPassword
   */
  public static List<PasswordChecker.Result> checkPostgresPassword(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      String password
  ) throws IOException {
    return com.aoindustries.aoserv.client.postgresql.User.checkPassword(username, password);
  }

  /**
   * Checks the format of a Majordomo list name.
   *
   * @exception  IllegalArgumentException  if the domain is not in a valid format
   *
   * @see  MajordomoList#isValidListName
   * @see  #addMajordomoList
   */
  public static void checkMajordomoListName(
      String listName
  ) throws IllegalArgumentException {
    if (!MajordomoList.isValidListName(listName)) {
      throw new IllegalArgumentException("Invalid Majordomo list name: " + listName);
    }
  }

  /**
   * Checks the format of an {@link SharedTomcat} name.
   *
   * @param  tomcatName  the name of the {@link SharedTomcat}
   *
   * @exception  IllegalArgumentException  if the name is not in a valid format
   *
   * @see  SharedTomcat#isValidSharedTomcatName
   * @see  #addHttpdSharedTomcat
   * @see  #addHttpdTomcatSharedSite
   */
  public static void checkSharedTomcatName(
      String tomcatName
  ) throws IllegalArgumentException {
    if (!SharedTomcat.isValidSharedTomcatName(tomcatName)) {
      throw new IllegalArgumentException("Invalid shared Tomcat name: " + tomcatName);
    }
  }

  /**
   * Checks the format of an {@link Site} name.
   *
   * @param  siteName  the name of the {@link Site}
   *
   * @exception  IllegalArgumentException  if the name is not in a valid format
   *
   * @see  Site#isValidSiteName
   * @see  #addHttpdTomcatStdSite
   */
  public static void checkSiteName(
      String siteName
  ) throws IllegalArgumentException {
    if (!Site.isValidSiteName(siteName)) {
      throw new IllegalArgumentException("Invalid site name: " + siteName);
    }
  }

  /**
   * Checks the strength of a password that will be used for
   * a {@link User}.  The strength requirement is based on
   * which services use the {@link User}.
   *
   * @param  username  the username whos password will be set
   * @param  password  the new password
   *
   * @return  a description of why the password is weak or {@code null}
   *          if all checks succeed
   *
   * @exception  IOException  if unable to load the dictionary resource or unable to access the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  #setUsernamePassword
   * @see  User#checkPassword
   */
  public List<PasswordChecker.Result> checkUsernamePassword(
      com.aoindustries.aoserv.client.account.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    return getUsername(username).checkPassword(password);
  }

  /**
   * Checks if a password matches a {@link UserServer}.
   *
   * @param  username  the username of the account
   * @param  aoServer  the hostname of the server to check
   * @param  password  the password to compare against
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.linux.User}, {@link Host},
   *                                  {@link Server}, or {@link UserServer} is not found
   *
   * @see  UserServer#passwordMatches
   * @see  #addLinuxServerAccount
   */
  public boolean compareLinuxServerAccountPassword(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).passwordMatches(password);
  }

  ///**
  // * Completes a {@link Ticket}.  Once a {@link Ticket} is completed, no more
  // * modifications or actions may be applied to the {@link Ticket}.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link TicketType}, or {@link Administrator}
  // *
  // * @see  Ticket#actCompleteTicket
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void completeTicket(
  //  int ticket_id,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actCompleteTicket(pe, comments);
  //}

  /**
   * Copies the contents of user's home directory from one server to another.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  fromAoServer  the server to get the data from
   * @param  toAoServer  the server to put the data on
   *
   * @return  the number of bytes transferred
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the source {@link UserServer}
   *                                       or destination {@link Server}
   *
   * @see  UserServer#copyHomeDirectory
   * @see  #addLinuxServerAccount
   * @see  #removeLinuxServerAccount
   */
  public long copyHomeDirectory(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String fromAoServer,
      String toAoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(fromAoServer, username).copyHomeDirectory(getLinuxServer(toAoServer));
  }

  /**
   * Copies the password from one {@link UserServer} to another.
   *
   * @param  fromUsername  the username to copy from
   * @param  fromAoServer  the server to get the data from
   * @param  toUsername  the username to copy to
   * @param  toAoServer  the server to put the data on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find a {@link UserServer}
   *
   * @see  UserServer#copyPassword
   * @see  #addLinuxServerAccount
   * @see  #removeLinuxServerAccount
   */
  public void copyLinuxServerAccountPassword(
      com.aoindustries.aoserv.client.linux.User.Name fromUsername,
      String fromAoServer,
      com.aoindustries.aoserv.client.linux.User.Name toUsername,
      String toAoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(fromAoServer, fromUsername).copyPassword(getLinuxServerAccount(toAoServer, toUsername));
  }

  /**
   * Encrypts a password using a pure Java implementation of the standard Unix <code>crypt</code>
   * function.
   *
   * @param  password  the password that is to be encrypted
   * @param  salt  the two character salt for the encryption process, if {@code null},
   *               a random salt will be used
   *
   * @deprecated  Please use {@link HashedPassword} instead.
   * @see  HashedPassword
   */
  @Deprecated
  public static String crypt(String password, String salt) {
    if (password == null || password.isEmpty()) {
      return HashedPassword.NO_PASSWORD_VALUE;
    }
    return salt == null || salt.isEmpty() ? com.aoapps.security.UnixCrypt.crypt(password) : com.aoapps.security.UnixCrypt.crypt(password, salt);
  }

  /**
   * Disables a {@link CreditCard}.  When a {@link Transaction} using a
   * {@link CreditCard} fails, the {@link CreditCard} is disabled.
   *
   * @param  pkey  the unique identifier of the {@link CreditCard}
   * @param  reason  the reason the card is being disabled
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link CreditCard}
   *
   * @see  CreditCard#declined
   * @see  Transaction
   * @see  CreditCard
   */
  public void declineCreditCard(
      int pkey,
      String reason
  ) throws IllegalArgumentException, IOException, SQLException {
    CreditCard card = connector.getPayment().getCreditCard().get(pkey);
    if (card == null) {
      throw new IllegalArgumentException("Unable to find CreditCard: " + pkey);
    }
    card.declined(reason);
  }

  /**
   * Disables a business, recursively disabling all of its enabled child components.
   *
   * @param  accounting  the accounting code to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the necessary {@link AoservObject}s
   */
  public int disableAccount(Account.Name accounting, String disableReason) throws IllegalArgumentException, IOException, SQLException {
    Account bu = getAccount(accounting);
    DisableLog dl = connector.getAccount().getDisableLog().get(bu.addDisableLog(disableReason));
    for (Package pk : bu.getPackages()) {
      if (!pk.isDisabled()) {
        disablePackage(dl, pk);
      }
    }
    bu.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a package, recursively disabling all of its enabled child components.
   *
   * @param  name  the name of the package
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the necessary {@link AoservObject}s
   */
  public int disablePackage(Account.Name name, String disableReason) throws IllegalArgumentException, SQLException, IOException {
    Package pk = getPackage(name);
    DisableLog dl = connector.getAccount().getDisableLog().get(pk.getAccount().addDisableLog(disableReason));
    disablePackage(dl, pk);
    return dl.getPkey();
  }

  private void disablePackage(DisableLog dl, Package pk) throws IOException, SQLException {
    /*
     * Email stuff
     */
    for (com.aoindustries.aoserv.client.email.List el : pk.getEmailLists()) {
      if (!el.isDisabled()) {
        el.disable(dl);
      }
    }
    for (Pipe ep : pk.getEmailPipes()) {
      if (!ep.isDisabled()) {
        ep.disable(dl);
      }
    }
    for (SmtpRelay ssr : pk.getEmailSmtpRelays()) {
      if (!ssr.isDisabled()) {
        ssr.disable(dl);
      }
    }

    /*
     * HTTP stuff
     */
    List<Server> httpdServers = new SortedArrayList<>();
    for (SharedTomcat hst : pk.getHttpdSharedTomcats()) {
      if (!hst.isDisabled()) {
        hst.disable(dl);
        Server ao = hst.getLinuxServer();
        if (!httpdServers.contains(ao)) {
          httpdServers.add(ao);
        }
      }
    }
    for (Site hs : pk.getHttpdSites()) {
      if (!hs.isDisabled()) {
        disableHttpdSite(dl, hs);
        Server ao = hs.getLinuxServer();
        if (!httpdServers.contains(ao)) {
          httpdServers.add(ao);
        }
      }
    }

    // Wait for httpd site rebuilds to complete, which shuts down all the appropriate processes
    for (Server httpdServer : httpdServers) {
      httpdServer.waitForHttpdSiteRebuild();
    }

    // Disable the user accounts once the JVMs have been shut down
    for (com.aoindustries.aoserv.client.account.User un : pk.getUsernames()) {
      if (!un.isDisabled()) {
        disableUsername(dl, un);
      }
    }

    pk.disable(dl);
  }

  /**
   * Disables a {@link SharedTomcat}.
   *
   * @param  name  the name of the tomcat JVM
   * @param  aoServer  the server that hosts the JVM
   * @param  disableReason  the reason the JVM is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, or {@link SharedTomcat}
   */
  public int disableHttpdSharedTomcat(
      String name,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, IOException, SQLException {
    SharedTomcat hst = getHttpdSharedTomcat(aoServer, name);
    DisableLog dl = connector.getAccount().getDisableLog().get(hst.getLinuxServerGroup().getLinuxGroup().getPackage().getAccount().addDisableLog(disableReason));
    hst.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables an {@link Pipe}.
   *
   * @param  pkey  the pkey of the pipe
   * @param  disableReason  the reason the pipe is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Pipe}
   */
  public int disableEmailPipe(
      int pkey,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    Pipe ep = connector.getEmail().getPipe().get(pkey);
    if (ep == null) {
      throw new IllegalArgumentException("Unable to find EmailPipe: " + pkey);
    }
    DisableLog dl = connector.getAccount().getDisableLog().get(ep.getPackage().getAccount().addDisableLog(disableReason));
    ep.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link Site}.
   *
   * @param  name  the name of the site
   * @param  aoServer  the server that hosts the site
   * @param  disableReason  the reason the site is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, or {@link Site}
   */
  public int disableHttpdSite(
      String name,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    Site hs = getHttpdSite(aoServer, name);
    DisableLog dl = connector.getAccount().getDisableLog().get(hs.getPackage().getAccount().addDisableLog(disableReason));
    disableHttpdSite(dl, hs);
    return dl.getPkey();
  }

  private void disableHttpdSite(DisableLog dl, Site hs) throws IOException, SQLException {
    for (VirtualHost hsb : hs.getHttpdSiteBinds()) {
      if (!hsb.isDisabled()) {
        hsb.disable(dl);
      }
    }
    hs.disable(dl);
  }

  /**
   * Disables a {@link VirtualHost}.
   *
   * @param  pkey  the pkey of the bind
   * @param  disableReason  the reason the bind is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHost}
   */
  public int disableHttpdSiteBind(
      int pkey,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    VirtualHost hsb = connector.getWeb().getVirtualHost().get(pkey);
    if (hsb == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteBind: " + pkey);
    }
    DisableLog dl = connector.getAccount().getDisableLog().get(hsb.getHttpdSite().getPackage().getAccount().addDisableLog(disableReason));
    hsb.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables an {@link com.aoindustries.aoserv.client.email.List}.
   *
   * @param  path  the path of the list
   * @param  aoServer  the server the list is part of
   * @param  disableReason  the reason the bind is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.email.List}
   */
  public int disableEmailList(
      PosixPath path,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.email.List el = getEmailList(aoServer, path);
    DisableLog dl = connector.getAccount().getDisableLog().get(el.getLinuxServerGroup().getLinuxGroup().getPackage().getAccount().addDisableLog(disableReason));
    el.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link SmtpRelay}.
   *
   * @param  pkey  the pkey of the relay
   * @param  disableReason  the reason the bind is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link SmtpRelay}
   */
  public int disableEmailSmtpRelay(
      int pkey,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    SmtpRelay ssr = connector.getEmail().getSmtpRelay().get(pkey);
    if (ssr == null) {
      throw new IllegalArgumentException("Unable to find EmailSmtpRelay: " + pkey);
    }
    DisableLog dl = connector.getAccount().getDisableLog().get(ssr.getPackage().getAccount().addDisableLog(disableReason));
    ssr.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link User} and all uses of the username.
   *
   * @param  username  the username to disable
   * @param  disableReason  the reason the bind is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   */
  public int disableUsername(
      com.aoindustries.aoserv.client.account.User.Name username,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    DisableLog dl = connector.getAccount().getDisableLog().get(un.getPackage().getAccount().addDisableLog(disableReason));
    disableUsername(dl, un);
    return dl.getPkey();
  }

  private void disableUsername(DisableLog dl, com.aoindustries.aoserv.client.account.User un) throws IOException, SQLException {
    com.aoindustries.aoserv.client.linux.User la = un.getLinuxAccount();
    if (la != null && !la.isDisabled()) {
      disableLinuxAccount(dl, la);
    }

    com.aoindustries.aoserv.client.mysql.User mu = un.getMysqlUser();
    if (mu != null && !mu.isDisabled()) {
      disableMysqlUser(dl, mu);
    }

    com.aoindustries.aoserv.client.postgresql.User pu = un.getPostgresUser();
    if (pu != null && !pu.isDisabled()) {
      disablePostgresUser(dl, pu);
    }

    un.disable(dl);
  }

  /**
   * Disables a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.linux.User}
   */
  public int disableLinuxAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount(username);
    DisableLog dl = connector.getAccount().getDisableLog().get(la.getUsername().getPackage().getAccount().addDisableLog(disableReason));
    disableLinuxAccount(dl, la);
    return dl.getPkey();
  }

  private void disableLinuxAccount(DisableLog dl, com.aoindustries.aoserv.client.linux.User la) throws IOException, SQLException {
    for (UserServer lsa : la.getLinuxServerAccounts()) {
      if (!lsa.isDisabled()) {
        disableLinuxServerAccount(dl, lsa);
      }
    }

    la.disable(dl);
  }

  /**
   * Disables a {@link UserServer}.
   *
   * @param  username  the username to disable
   * @param  aoServer  the server the account is on
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User},
   *                                  {@link com.aoindustries.aoserv.client.linux.User}, or {@link UserServer}
   */
  public int disableLinuxServerAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    UserServer lsa = getLinuxServerAccount(aoServer, username);
    DisableLog dl = connector.getAccount().getDisableLog().get(lsa.getLinuxAccount().getUsername().getPackage().getAccount().addDisableLog(disableReason));
    disableLinuxServerAccount(dl, lsa);
    return dl.getPkey();
  }

  private void disableLinuxServerAccount(DisableLog dl, UserServer lsa) throws IOException, SQLException {
    for (CvsRepository cr : lsa.getCvsRepositories()) {
      if (!cr.isDisabled()) {
        cr.disable(dl);
      }
    }
    lsa.disable(dl);
  }

  /**
   * Disables a {@link CvsRepository}.
   *
   * @param  pkey  the pkey of the repository to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link CvsRepository}
   */
  public int disableCvsRepository(
      int pkey,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    CvsRepository cr = connector.getScm().getCvsRepository().get(pkey);
    if (cr == null) {
      throw new IllegalArgumentException("Unable to find CvsRepository: " + pkey);
    }
    DisableLog dl = connector.getAccount().getDisableLog()
        .get(
            cr
                .getLinuxServerAccount()
                .getLinuxAccount()
                .getUsername()
                .getPackage()
                .getAccount()
                .addDisableLog(disableReason)
        );
    cr.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link com.aoindustries.aoserv.client.mysql.User}.
   *
   * @param  username  the username to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.mysql.User}
   */
  public int disableMysqlUser(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser(username);
    DisableLog dl = connector.getAccount().getDisableLog().get(mu.getUsername().getPackage().getAccount().addDisableLog(disableReason));
    disableMysqlUser(dl, mu);
    return dl.getPkey();
  }

  private void disableMysqlUser(DisableLog dl, com.aoindustries.aoserv.client.mysql.User mu) throws IOException, SQLException {
    for (com.aoindustries.aoserv.client.mysql.UserServer msu : mu.getMysqlServerUsers()) {
      if (!msu.isDisabled()) {
        msu.disable(dl);
      }
    }
    mu.disable(dl);
  }

  /**
   * Disables a {@link com.aoindustries.aoserv.client.mysql.UserServer}.
   *
   * @param  username  the username to disable
   * @param  aoServer  the server the account is on
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link com.aoindustries.aoserv.client.mysql.User}
   */
  public int disableMysqlServerUser(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.mysql.UserServer msu = getMysqlServerUser(aoServer, mysqlServer, username);
    DisableLog dl = connector.getAccount().getDisableLog().get(msu.getMysqlUser().getUsername().getPackage().getAccount().addDisableLog(disableReason));
    msu.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link com.aoindustries.aoserv.client.postgresql.User}.
   *
   * @param  username  the username to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.postgresql.User}
   */
  public int disablePostgresUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser(username);
    DisableLog dl = connector.getAccount().getDisableLog().get(pu.getUsername().getPackage().getAccount().addDisableLog(disableReason));
    disablePostgresUser(dl, pu);
    return dl.getPkey();
  }

  private void disablePostgresUser(DisableLog dl, com.aoindustries.aoserv.client.postgresql.User pu) throws IOException, SQLException {
    for (com.aoindustries.aoserv.client.postgresql.UserServer psu : pu.getPostgresServerUsers()) {
      if (!psu.isDisabled()) {
        psu.disable(dl);
      }
    }
    pu.disable(dl);
  }

  /**
   * Disables a {@link com.aoindustries.aoserv.client.postgresql.UserServer}.
   *
   * @param  username  the username to disable
   * @param  postgresServer  the name of the PostgresServer
   * @param  aoServer  the server the account is on
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link com.aoindustries.aoserv.client.postgresql.User}
   */
  public int disablePostgresServerUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.postgresql.UserServer psu = getPostgresServerUser(aoServer, postgresServer, username);
    DisableLog dl = connector.getAccount().getDisableLog()
        .get(
            psu
                .getPostgresUser()
                .getUsername()
                .getPackage()
                .getAccount()
                .addDisableLog(disableReason)
        );
    psu.disable(dl);
    return dl.getPkey();
  }

  /**
   * Disables a {@link Administrator}.
   *
   * @param  username  the username to disable
   * @param  disableReason  the reason the account is being disabled
   *
   * @return  the pkey of the new {@link DisableLog}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link Administrator}
   */
  public int disableAdministrator(
      com.aoindustries.aoserv.client.account.User.Name username,
      String disableReason
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    Administrator administrator = un.getAdministrator();
    if (administrator == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    DisableLog dl = connector.getAccount().getDisableLog().get(un.getPackage().getAccount().addDisableLog(disableReason));
    administrator.disable(dl);
    return dl.getPkey();
  }

  /**
   * Enables a business, recursively enabling all of its disabled child components.
   *
   * @param  accounting  the accounting code to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the necessary {@link Account accounts}
   */
  public void enableAccount(Account.Name accounting) throws IllegalArgumentException, IOException, SQLException {
    Account bu = getAccount(accounting);
    DisableLog dl = bu.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("Account not disabled: " + accounting);
    }
    bu.enable();
    for (Package pk : bu.getPackages()) {
      if (dl.equals(pk.getDisableLog())) {
        enablePackage(dl, pk);
      }
    }
  }

  /**
   * Enables a package, recursively enabling all of its disabled child components.
   *
   * @param  name  the name of the package
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the necessary {@link AoservObject}s
   */
  public void enablePackage(Account.Name name) throws IllegalArgumentException, SQLException, IOException {
    Package pk = getPackage(name);
    DisableLog dl = pk.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("Package not disabled: " + name);
    }
    enablePackage(dl, pk);
  }

  private void enablePackage(DisableLog dl, Package pk) throws IOException, SQLException {
    pk.enable();

    /*
     * Email stuff
     */
    for (com.aoindustries.aoserv.client.email.List el : pk.getEmailLists()) {
      if (dl.equals(el.getDisableLog())) {
        el.enable();
      }
    }
    for (Pipe ep : pk.getEmailPipes()) {
      if (dl.equals(ep.getDisableLog())) {
        ep.enable();
      }
    }
    for (SmtpRelay ssr : pk.getEmailSmtpRelays()) {
      if (dl.equals(ssr.getDisableLog())) {
        ssr.enable();
      }
    }

    // Various accounts
    List<Server> linuxAccountServers = new SortedArrayList<>();
    List<Server> mysqlServers = new SortedArrayList<>();
    List<Server> postgresServers = new SortedArrayList<>();
    for (com.aoindustries.aoserv.client.account.User un : pk.getUsernames()) {
      if (dl.equals(un.getDisableLog())) {
        enableUsername(
            dl,
            un,
            linuxAccountServers,
            mysqlServers,
            postgresServers
        );
      }
    }

    // Wait for rebuilds
    for (Server linuxAccountServer : linuxAccountServers) {
      linuxAccountServer.waitForLinuxAccountRebuild();
    }
    for (Server mysqlServer : mysqlServers) {
      mysqlServer.waitForMysqlUserRebuild();
    }
    for (Server postgresServer : postgresServers) {
      postgresServer.waitForPostgresUserRebuild();
    }

    // Start up the web sites
    for (SharedTomcat hst : pk.getHttpdSharedTomcats()) {
      if (dl.equals(hst.getDisableLog())) {
        hst.enable();
      }
    }

    for (Site hs : pk.getHttpdSites()) {
      if (hs.getDisableLog_pkey() != null && hs.getDisableLog_pkey() == dl.getPkey()) {
        enableHttpdSite(dl, hs);
      }
    }
  }

  /**
   * Enables a {@link SharedTomcat}.
   *
   * @param  name  the name of the tomcat JVM
   * @param  aoServer  the server that hosts the JVM
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, or {@link SharedTomcat}
   */
  public void enableHttpdSharedTomcat(
      String name,
      String aoServer
  ) throws IllegalArgumentException, SQLException, IOException {
    SharedTomcat hst = getHttpdSharedTomcat(aoServer, name);
    DisableLog dl = hst.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("HttpdSharedTomcat not disabled: " + name + " on " + aoServer);
    }
    hst.enable();
  }

  /**
   * Enables an {@link Pipe}.
   *
   * @param  pkey  the pkey of the pipe
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Pipe}
   */
  public void enableEmailPipe(
      int pkey
  ) throws IllegalArgumentException, SQLException, IOException {
    Pipe ep = connector.getEmail().getPipe().get(pkey);
    if (ep == null) {
      throw new IllegalArgumentException("Unable to find EmailPipe: " + pkey);
    }
    DisableLog dl = ep.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("EmailPipe not disabled: " + pkey);
    }
    ep.enable();
  }

  /**
   * Enables a {@link Site}.
   *
   * @param  name  the name of the site
   * @param  aoServer  the server that hosts the site
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, or {@link Site}
   */
  public void enableHttpdSite(
      String name,
      String aoServer
  ) throws IllegalArgumentException, SQLException, IOException {
    Site hs = getHttpdSite(aoServer, name);
    DisableLog dl = hs.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("HttpdSite not disabled: " + name + " on " + aoServer);
    }
    enableHttpdSite(dl, hs);
  }

  private void enableHttpdSite(DisableLog dl, Site hs) throws IOException, SQLException {
    hs.enable();
    for (VirtualHost hsb : hs.getHttpdSiteBinds()) {
      if (dl.equals(hsb.getDisableLog())) {
        hsb.enable();
      }
    }
  }

  /**
   * Enables a {@link VirtualHost}.
   *
   * @param  pkey  the pkey of the bind
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHost}
   */
  public void enableHttpdSiteBind(
      int pkey
  ) throws IllegalArgumentException, SQLException, IOException {
    VirtualHost hsb = connector.getWeb().getVirtualHost().get(pkey);
    if (hsb == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteBind: " + pkey);
    }
    DisableLog dl = hsb.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("HttpdSiteBind not disabled: " + pkey);
    }
    hsb.enable();
  }

  /**
   * Enables an {@link com.aoindustries.aoserv.client.email.List}.
   *
   * @param  path  the path of the list
   * @param  aoServer  the server the list is part of
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.email.List}
   */
  public void enableEmailList(
      PosixPath path,
      String aoServer
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.email.List el = getEmailList(aoServer, path);
    DisableLog dl = el.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("EmailList not disabled: " + path + " on " + aoServer);
    }
    el.enable();
  }

  /**
   * Enables a {@link SmtpRelay}.
   *
   * @param  pkey  the pkey of the relay
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link SmtpRelay}
   */
  public void enableEmailSmtpRelay(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    SmtpRelay ssr = connector.getEmail().getSmtpRelay().get(pkey);
    if (ssr == null) {
      throw new IllegalArgumentException("Unable to find EmailSmtpRelay: " + pkey);
    }
    DisableLog dl = ssr.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("EmailSmtpRelay not disabled: " + pkey);
    }
    ssr.enable();
  }

  /**
   * Enables a {@link User} and all uses of the username.
   *
   * @param  username  the username to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   */
  public void enableUsername(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    DisableLog dl = un.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("Username not disabled: " + username);
    }
    enableUsername(dl, un, null, null, null);
  }

  private void enableUsername(
      DisableLog dl,
      com.aoindustries.aoserv.client.account.User un,
      List<Server> linuxAccountServers,
      List<Server> mysqlServers,
      List<Server> postgresServers
  ) throws IOException, SQLException {
    un.enable();

    Administrator ba = un.getAdministrator();
    if (ba != null && dl.equals(ba.getDisableLog())) {
      ba.enable();
    }

    com.aoindustries.aoserv.client.linux.User la = un.getLinuxAccount();
    if (la != null && dl.equals(la.getDisableLog())) {
      enableLinuxAccount(dl, la, linuxAccountServers);
    }

    com.aoindustries.aoserv.client.mysql.User mu = un.getMysqlUser();
    if (mu != null && dl.equals(mu.getDisableLog())) {
      enableMysqlUser(dl, mu, mysqlServers);
    }

    com.aoindustries.aoserv.client.postgresql.User pu = un.getPostgresUser();
    if (pu != null && dl.equals(pu.getDisableLog())) {
      enablePostgresUser(dl, pu, postgresServers);
    }
  }

  /**
   * Enables a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.linux.User}
   */
  public void enableLinuxAccount(
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount(username);
    DisableLog dl = la.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("LinuxAccount not disabled: " + username);
    }
    enableLinuxAccount(dl, la, null);
  }

  private void enableLinuxAccount(DisableLog dl, com.aoindustries.aoserv.client.linux.User la, List<Server> linuxAccountServers) throws SQLException, IOException {
    la.enable();

    for (UserServer lsa : la.getLinuxServerAccounts()) {
      if (dl.equals(lsa.getDisableLog())) {
        enableLinuxServerAccount(dl, lsa);
        if (linuxAccountServers != null) {
          Server ao = lsa.getServer();
          if (!linuxAccountServers.contains(ao)) {
            linuxAccountServers.add(ao);
          }
        }
      }
    }
  }

  /**
   * Enables a {@link UserServer}.
   *
   * @param  username  the username to enable
   * @param  aoServer  the server the account is on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User},
   *                                  {@link com.aoindustries.aoserv.client.linux.User}, or {@link UserServer}
   */
  public void enableLinuxServerAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, SQLException, IOException {
    UserServer lsa = getLinuxServerAccount(aoServer, username);
    DisableLog dl = lsa.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("LinuxServerAccount not disabled: " + username + " on " + aoServer);
    }
    enableLinuxServerAccount(dl, lsa);
  }

  private void enableLinuxServerAccount(DisableLog dl, UserServer lsa) throws IOException, SQLException {
    lsa.enable();
    for (CvsRepository cr : lsa.getCvsRepositories()) {
      if (dl.equals(cr.getDisableLog())) {
        cr.enable();
      }
    }
  }

  /**
   * Enables a {@link CvsRepository}.
   *
   * @param  pkey  the pkey of the repository to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link CvsRepository}
   */
  public void enableCvsRepository(
      int pkey
  ) throws IllegalArgumentException, SQLException, IOException {
    CvsRepository cr = connector.getScm().getCvsRepository().get(pkey);
    if (cr == null) {
      throw new IllegalArgumentException("Unable to find CvsRepository: " + pkey);
    }
    DisableLog dl = cr.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("CvsRepository not disabled: " + pkey);
    }
    cr.enable();
  }

  /**
   * Enables a {@link com.aoindustries.aoserv.client.mysql.User}.
   *
   * @param  username  the username to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.mysql.User}
   */
  public void enableMysqlUser(
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.mysql.User mu = getMysqlUser(username);
    DisableLog dl = mu.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("MysqlUser not disabled: " + username);
    }
    enableMysqlUser(dl, mu, null);
  }

  private void enableMysqlUser(DisableLog dl, com.aoindustries.aoserv.client.mysql.User mu, List<Server> mysqlServers) throws IOException, SQLException {
    mu.enable();
    for (com.aoindustries.aoserv.client.mysql.UserServer msu : mu.getMysqlServerUsers()) {
      if (dl.equals(msu.getDisableLog())) {
        msu.enable();
        if (mysqlServers != null) {
          Server ao = msu.getMysqlServer().getLinuxServer();
          if (!mysqlServers.contains(ao)) {
            mysqlServers.add(ao);
          }
        }
      }
    }
  }

  /**
   * Enables a {@link com.aoindustries.aoserv.client.mysql.UserServer}.
   *
   * @param  username  the username to enable
   * @param  aoServer  the server the account is on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link com.aoindustries.aoserv.client.mysql.User}
   */
  public void enableMysqlServerUser(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.mysql.UserServer msu = getMysqlServerUser(aoServer, mysqlServer, username);
    DisableLog dl = msu.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("MysqlServerUser not disabled: " + username + " on " + mysqlServer + " on " + aoServer);
    }
    msu.enable();
  }

  /**
   * Enables a {@link com.aoindustries.aoserv.client.postgresql.User}.
   *
   * @param  username  the username to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link com.aoindustries.aoserv.client.postgresql.User}
   */
  public void enablePostgresUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.postgresql.User pu = getPostgresUser(username);
    DisableLog dl = pu.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("PostgresUser not disabled: " + username);
    }
    enablePostgresUser(dl, pu, null);
  }

  private void enablePostgresUser(DisableLog dl, com.aoindustries.aoserv.client.postgresql.User pu, List<Server> postgresServers) throws IOException, SQLException {
    pu.enable();

    for (com.aoindustries.aoserv.client.postgresql.UserServer psu : pu.getPostgresServerUsers()) {
      if (dl.equals(psu.getDisableLog())) {
        psu.enable();
        if (postgresServers != null) {
          Server ao = psu.getPostgresServer().getLinuxServer();
          if (!postgresServers.contains(ao)) {
            postgresServers.add(ao);
          }
        }
      }
    }
  }

  /**
   * Enables a {@link com.aoindustries.aoserv.client.postgresql.UserServer}.
   *
   * @param  username  the username to enable
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the server the account is on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link com.aoindustries.aoserv.client.postgresql.User}
   */
  public void enablePostgresServerUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.postgresql.UserServer psu = getPostgresServerUser(aoServer, postgresServer, username);
    DisableLog dl = psu.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("PostgresServerUser not disabled: " + username + " on " + aoServer);
    }
    psu.enable();
  }

  /**
   * Enables an {@link Administrator}.
   *
   * @param  username  the username to enable
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or {@link Administrator}
   */
  public void enableAdministrator(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, SQLException, IOException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    Administrator ba = un.getAdministrator();
    if (ba == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    DisableLog dl = ba.getDisableLog();
    if (dl == null) {
      throw new IllegalArgumentException("Administrator not disabled: " + username);
    }
    ba.enable();
  }

  /**
   * Dumps the contents of a {@link com.aoindustries.aoserv.client.mysql.Database} to a {@link Writer}.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.mysql.Database}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  out  the {@link Writer} to dump to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.mysql.Database}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Database#dump
   * @see  com.aoindustries.aoserv.client.mysql.Database
   */
  public void dumpMysqlDatabase(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      Writer out
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlDatabase(aoServer, mysqlServer, name).dump(out);
  }

  /**
   * Dumps the contents of a {@link com.aoindustries.aoserv.client.mysql.Database} to an {@link OutputStream}, optionally gzipped.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.mysql.Database}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  gzip  the gzip flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.mysql.Database}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Database#dump
   * @see  com.aoindustries.aoserv.client.mysql.Database
   */
  public void dumpMysqlDatabase(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      boolean gzip,
      StreamHandler streamHandler
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlDatabase(aoServer, mysqlServer, name).dump(gzip, streamHandler);
  }

  /**
   * Dumps the contents of a {@link com.aoindustries.aoserv.client.postgresql.Database} to a {@link Writer}.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.postgresql.Database}
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   * @param  out  the {@link Writer} to dump to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.postgresql.Database}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Database#dump
   * @see  com.aoindustries.aoserv.client.postgresql.Database
   */
  public void dumpPostgresDatabase(
      com.aoindustries.aoserv.client.postgresql.Database.Name name,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer,
      Writer out
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresDatabase(aoServer, postgresServer, name).dump(out);
  }

  /**
   * Dumps the contents of a {@link com.aoindustries.aoserv.client.postgresql.Database} to an {@link OutputStream}, optionally gzipped.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.postgresql.Database}
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   * @param  gzip  the gzip flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.postgresql.Database}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Database#dump
   * @see  com.aoindustries.aoserv.client.postgresql.Database
   */
  public void dumpPostgresDatabase(
      com.aoindustries.aoserv.client.postgresql.Database.Name name,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer,
      boolean gzip,
      StreamHandler streamHandler
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresDatabase(aoServer, postgresServer, name).dump(gzip, streamHandler);
  }

  /**
   * Generates a unique accounting code that may be used to create a new {@link Account}.
   *
   * @param  accountingTemplate  the beginning part of the accounting code, such as <code>"AO_"</code>
   *
   * @return  the available accounting code
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  AccountTable#generateAccountingCode(com.aoindustries.aoserv.client.account.Account.Name)
   * @see  #addAccount
   * @see  Account
   */
  public Account.Name generateAccountingCode(
      Account.Name accountingTemplate
  ) throws IOException, SQLException {
    return connector.getAccount().getAccount().generateAccountingCode(accountingTemplate);
  }

  /**
   * Generates a unique MySQL database name.
   *
   * @param  templateBase  the beginning part of the template, such as <code>"AO"</code>
   * @param  templateAdded  the part of the template added between the <code>template_base</code> and
   *                         the generated number, such as <code>"_"</code>
   *
   * @return  the available database name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  com.aoindustries.aoserv.client.mysql.DatabaseTable#generateMysqlDatabaseName
   * @see  #addMysqlDatabase
   * @see  com.aoindustries.aoserv.client.mysql.Database
   */
  public com.aoindustries.aoserv.client.mysql.Database.Name generateMysqlDatabaseName(
      String templateBase,
      String templateAdded
  ) throws IOException, SQLException {
    return connector.getMysql().getDatabase().generateMysqlDatabaseName(templateBase, templateAdded);
  }

  /**
   * Generates a unique {@link Package} name.
   *
   * @param  template  the beginning part of the template, such as <code>"AO_"</code>
   *
   * @return  the available {@link Package} name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  PackageTable#generatePackageName(com.aoindustries.aoserv.client.account.Account.Name)
   * @see  #addPackage
   * @see  Package
   */
  public Account.Name generatePackageName(
      Account.Name template
  ) throws IOException, SQLException {
    return connector.getBilling().getPackage().generatePackageName(template);
  }

  /**
   * Generates a random, valid password.
   *
   * @return  the password
   *
   * @exception  IOException  if unable to contact the server
   *
   */
  public String generatePassword() throws IOException {
    return PasswordGenerator.generatePassword();
  }

  /**
   * Generates a unique PostgreSQL database name.
   *
   * @param  templateBase  the beginning part of the template, such as <code>"AO"</code>
   * @param  templateAdded  the part of the template added between the <code>template_base</code> and
   *                         the generated number, such as <code>"_"</code>
   *
   * @return  the available database name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  com.aoindustries.aoserv.client.postgresql.DatabaseTable#generatePostgresDatabaseName
   * @see  #addPostgresDatabase
   * @see  com.aoindustries.aoserv.client.postgresql.Database
   */
  public com.aoindustries.aoserv.client.postgresql.Database.Name generatePostgresDatabaseName(
      String templateBase,
      String templateAdded
  ) throws IOException, SQLException {
    return connector.getPostgresql().getDatabase().generatePostgresDatabaseName(templateBase, templateAdded);
  }

  /**
   * Generates a unique {@link SharedTomcat} name.
   *
   * @param  template  the beginning part of the template, such as <code>"ao"</code>
   *
   * @return  the available {@link SharedTomcat} name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  SharedTomcatTable#generateSharedTomcatName(java.lang.String)
   * @see  #addHttpdSharedTomcat
   * @see  #addHttpdTomcatSharedSite
   * @see  Site
   */
  public String generateSharedTomcatName(
      String template
  ) throws IOException, SQLException {
    return connector.getWeb_tomcat().getSharedTomcat().generateSharedTomcatName(template);
  }

  /**
   * Generates a unique {@link Site} name.
   *
   * @param  template  the beginning part of the template, such as <code>"ao"</code>
   *
   * @return  the available {@link Site} name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  SiteTable#generateSiteName(java.lang.String)
   * @see  #addHttpdTomcatStdSite
   * @see  Site
   */
  public String generateSiteName(
      String template
  ) throws IOException, SQLException {
    return connector.getWeb().getSite().generateSiteName(template);
  }

  /**
   * Gets the autoresponder content.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the server to get the data from
   *
   * @return  the autoresponder content
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the source {@link UserServer}
   *
   * @see  UserServer#getAutoresponderContent
   * @see  #setAutoresponder
   */
  public String getAutoresponderContent(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).getAutoresponderContent();
  }

  /**
   * Gets the {@link AoservConnector} used for communication with the server.
   */
  public AoservConnector getConnector() {
    return connector;
  }

  /**
   * Gets a user's cron table on one server.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  aoServer  the server to get the data from
   *
   * @return  the cron table
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the source {@link UserServer}
   *
   * @see  UserServer#getCronTable
   * @see  #setCronTable
   * @see  #addLinuxServerAccount
   * @see  #removeLinuxServerAccount
   */
  public String getCronTable(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).getCronTable();
  }

  /**
   * Gets the list of email addresses that an {@link com.aoindustries.aoserv.client.email.List} will be forwarded to.
   *
   * @param  path  the path of the list
   * @param  aoServer  the server this list is part of
   *
   * @return  the list of addresses, one address per line separated by <code>'\n'</code>
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.email.List}
   *
   * @see  com.aoindustries.aoserv.client.email.List#getAddressList
   * @see  #addEmailList
   * @see  #setEmailListAddressList
   * @see  List
   */
  public String getEmailListAddressList(
      PosixPath path,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getEmailList(aoServer, path).getAddressList();
  }

  /**
   * Gets the total size of a {@link BackupPartition}.
   *
   * @param  aoServer  the hostname of the server
   * @param  path  the path of the {@link BackupPartition}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link BackupPartition}
   *
   * @see  BackupPartition#getDiskTotalSize
   */
  public long getBackupPartitionTotalSize(
      String aoServer,
      String path
  ) throws IllegalArgumentException, IOException, SQLException {
    BackupPartition bp = getLinuxServer(aoServer).getBackupPartitionForPath(path);
    if (bp == null) {
      throw new IllegalArgumentException("Unable to find BackupPartition: " + path + " on " + aoServer);
    }
    return bp.getDiskTotalSize();
  }

  /**
   * Gets the used size of a {@link BackupPartition}.
   *
   * @param  aoServer  the hostname of the server
   * @param  path  the path of the {@link BackupPartition}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link BackupPartition}
   *
   * @see  BackupPartition#getDiskUsedSize
   */
  public long getBackupPartitionUsedSize(
      String aoServer,
      String path
  ) throws IllegalArgumentException, IOException, SQLException {
    BackupPartition bp = getLinuxServer(aoServer).getBackupPartitionForPath(path);
    if (bp == null) {
      throw new IllegalArgumentException("Unable to find BackupPartition: " + path + " on " + aoServer);
    }
    return bp.getDiskUsedSize();
  }

  /**
   * Gets the last reported activity for a {@link FileReplication}.
   *
   * @param  fromServer  the server that is being backed-up
   * @param  toServer  the hostname of the server the stores the backups
   * @param  path  the path of the {@link BackupPartition}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host}, {@link Server}, {@link BackupPartition}, or {@link FileReplication}
   *
   * @see  FileReplication#getActivity()
   */
  public FileReplication.Activity getFailoverFileReplicationActivity(
      String fromServer,
      String toServer,
      String path
  ) throws IllegalArgumentException, IOException, SQLException {
    return getFailoverFileReplication(fromServer, toServer, path).getActivity();
  }

  /**
   * @see  HttpdServer#getConcurrency()
   *
   * @param  aoServer  the server hosting the account
   * @param  name      the name of the instance of {@code null} for the default instance
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @throws IllegalArgumentException if unable to find the {@link Host}, {@link Server}, or {@link HttpdServer}
   */
  public int getHttpdServerConcurrency(
      String aoServer,
      String name
  ) throws IllegalArgumentException, IOException, SQLException {
    return getHttpdServer(aoServer, name).getConcurrency();
  }

  /**
   * Gets the attributes of an inbox.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer    the server hosting the account
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link UserServer}
   *
   * @see  UserServer#getInboxAttributes
   */
  public InboxAttributes getInboxAttributes(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).getInboxAttributes();
  }

  /**
   * Gets the IMAP folder sizes for an  inbox.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer    the server hosting the account
   * @param  folderNames  the folder names
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link UserServer}
   *
   * @see  UserServer#getImapFolderSizes
   */
  public long[] getImapFolderSizes(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String[] folderNames
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).getImapFolderSizes(folderNames);
  }

  /**
   * Gets the info file for a {@link MajordomoList}.
   *
   * @param  domain  the domain of the {@link MajordomoServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  listName  the name of the new list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the name is not valid or unable to find the
   *                                  {@link Server}, {@link Domain},
   *                                  {@link MajordomoServer}, or {@link MajordomoList}
   *
   * @see  MajordomoList#getInfoFile
   * @see  #addMajordomoList
   * @see  #removeEmailList
   */
  public String getMajordomoInfoFile(
      DomainName domain,
      String aoServer,
      String listName
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoServer ms = ed.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    MajordomoList ml = ms.getMajordomoList(listName);
    if (ml == null) {
      throw new IllegalArgumentException("Unable to find MajordomoList: " + listName + '@' + domain + " on " + aoServer);
    }
    return ml.getInfoFile();
  }

  /**
   * Gets the intro file for a {@link MajordomoList}.
   *
   * @param  domain  the domain of the {@link MajordomoServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  listName  the name of the new list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the name is not valid or unable to find the
   *                                  {@link Server}, {@link Domain},
   *                                  {@link MajordomoServer}, or {@link MajordomoList}
   *
   * @see  MajordomoList#getIntroFile
   * @see  #addMajordomoList
   * @see  #removeEmailList
   */
  public String getMajordomoIntroFile(
      DomainName domain,
      String aoServer,
      String listName
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoServer ms = ed.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    MajordomoList ml = ms.getMajordomoList(listName);
    if (ml == null) {
      throw new IllegalArgumentException("Unable to find MajordomoList: " + listName + '@' + domain + " on " + aoServer);
    }
    return ml.getIntroFile();
  }

  /**
   * Gets the contents of a MRTG file.
   *
   * @param  aoServer  the hostname of the server to get the file from
   * @param  filename  the filename on the server
   * @param  out  the {@link OutputStream} to write the file to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#getMrtgFile
   */
  public void getMrtgFile(
      String aoServer,
      String filename,
      OutputStream out
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).getMrtgFile(filename, out);
  }

  /**
   * Gets the current status of the UPS.
   *
   * @param  aoServer  the hostname of the server to get the file from
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#getMrtgFile
   */
  public String getUpsStatus(
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServer(aoServer).getUpsStatus();
  }

  /**
   * Gets the contents of an AWStats file.
   *
   * @param  siteName  the site name
   * @param  aoServer  the hostname of the server to get the file from
   * @param  path  the filename on the server
   * @param  queryString  the query string for the request
   * @param  out  the {@link OutputStream} to write the file to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host}, {@link Server}, or {@link Site}
   *
   * @see  Site#getAwstatsFile
   */
  public void getAwstatsFile(
      String siteName,
      String aoServer,
      String path,
      String queryString,
      OutputStream out
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).getAwstatsFile(path, queryString, out);
  }

  /**
   * Gets the name of the root {@link Account} in the tree of {@link Account accounts}.
   *
   * @return  the accounting code of the root {@link Account}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  AccountTable#getRootAccount_name()
   */
  public Account.Name getRootAccount() throws IOException, SQLException {
    return connector.getAccount().getAccount().getRootAccount_name();
  }

  ///**
  // * Places a {@link Ticket} in the hold state.  When in a hold state, a {@link Ticket}
  // * is not being worked on because the support personnel are waiting for something out of their
  // * immediate control.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link TicketType}, or {@link Administrator}
  // *
  // * @see  Ticket#actHoldTicket
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void holdTicket(
  //  int ticket_id,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  ti.actHoldTicket(comments);
  //}

  /**
   * Initializes the password files for an {@link Site}.  These files are
   * typically contained in <code>/var/www/<i>sitename</i>/conf/passwd</code> and
   * <code>/var/www/<i>sitename</i>/conf/group</code>.
   *
   * @param  siteName  the name of the site to initialize
   * @param  aoServer  the hostname of the {@link Server}
   * @param  username  the username granted access to the site
   * @param  password  the password for that username
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link Site}
   *
   * @see  Site#initializePasswdFile
   * @see  #addHttpdTomcatStdSite
   */
  /*
  public void initializeHttpdSitePasswdFile(
    String siteName,
    String aoServer,
    String username,
    String password
  ) {
    getHttpdSite(aoServer, siteName).initializePasswdFile(username, password);
  }
   */

  private static final int numTables = Table.TableId.values().length;

  /**
   * Invalidates a table, causing all caches of the table to be removed and all configurations
   * based on the table to be reevaluated.
   *
   * @param  tableId  the ID of the {@link AoservTable} to invalidate
   * @param  server  the server that should be invalidated or <code>null or ""</code> for none, accepts ao_servers.hostname, servers.package || '/' || servers.name, or servers.pkey
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the table ID is invalid
   *
   * @see  AoservConnector#invalidateTable
   * @see  Administrator#isActiveTableInvalidator
   */
  public void invalidate(
      int tableId,
      String server
  ) throws IllegalArgumentException, SQLException, IOException {
    if (tableId < 0 || tableId >= numTables) {
      throw new IllegalArgumentException("Invalid table ID: " + tableId);
    }
    Host se;
    if (server != null && server.length() == 0) {
      server = null;
    }
    if (server == null) {
      se = null;
    } else {
      se = connector.getNet().getHost().get(server);
      if (se == null) {
        throw new IllegalArgumentException("Unable to find Host: " + server);
      }
    }
    connector.invalidateTable(tableId, se == null ? -1 : se.pkey);
  }

  /**
   * Determines if an accounting code is available.
   *
   * @param  accounting  the accounting code
   *
   * @return  {@code true} if the accounting code is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  AccountTable#isAccountingAvailable
   * @see  #addAccount
   * @see  #generateAccountingCode
   * @see  Account
   */
  public boolean isAccountingAvailable(
      Account.Name accounting
  ) throws SQLException, IOException {
    return connector.getAccount().getAccount().isAccountingAvailable(accounting);
  }

  /**
   * Determines if a {@link Administrator} currently has a password set.
   *
   * @param  username  the username of the administrator
   *
   * @return  if the {@link Administrator} has a password set
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link Administrator} is not found
   *
   * @see  Administrator#arePasswordsSet
   * @see  #setAdministratorPassword
   * @see  Administrator
   */
  public boolean isAdministratorPasswordSet(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    Administrator ba = connector.getAccount().getAdministrator().get(username);
    if (ba == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    return ba.arePasswordsSet() == PasswordProtected.ALL;
  }

  /**
   * Determines if a {@link Zone} is available.
   *
   * @param  zone  the zone in <code>domain.tld.</code> format
   *
   * @return  {@code true} if the {@link Zone} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  ZoneTable#isDnsZoneAvailable(java.lang.String)
   * @see  #addDnsZone
   * @see  Zone
   */
  public boolean isDnsZoneAvailable(
      String zone
  ) throws IOException, SQLException {
    return connector.getDns().getZone().isDnsZoneAvailable(zone);
  }

  /**
   * Determines if an {@link IpAddress} is currently being used.
   *
   * @param  ipAddress  the IP address
   *
   * @return  {@code true} if the {@link IpAddress} is in use
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link IpAddress}
   *
   * @see  IpAddress#isUsed
   * @see  #setIpAddressPackage
   */
  public boolean isIpAddressUsed(
      InetAddress ipAddress,
      String server,
      String netDevice
  ) throws IllegalArgumentException, IOException, SQLException {
    return getIpAddress(server, netDevice, ipAddress).isUsed();
  }

  /**
   * Determines if a groupname is available.
   *
   * @param  groupname  the groupname
   *
   * @return  {@code true} if the groupname is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  GroupTable#isLinuxGroupNameAvailable
   * @see  #addLinuxGroup
   * @see  Group
   */
  public boolean isLinuxGroupNameAvailable(
      Group.Name groupname
  ) throws IOException, SQLException {
    return connector.getLinux().getGroup().isLinuxGroupNameAvailable(groupname);
  }

  /**
   * Determines if a {@link UserServer} currently has a password set.
   *
   * @param  username  the username of the account
   * @param  aoServer  the server the account is hosted on
   *
   * @return  if the {@link UserServer} has a password set
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link UserServer} is not found
   *
   * @see  UserServer#arePasswordsSet
   * @see  #setLinuxServerAccountPassword
   * @see  UserServer
   */
  public boolean isLinuxServerAccountPasswordSet(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).arePasswordsSet() == PasswordProtected.ALL;
  }

  /**
   * Determines if a {@link UserServer} is currently in manual procmail mode.  Manual
   * procmail mode is initiated when the header comment in the .procmailrc file is altered or removed.
   *
   * @param  username  the username of the account
   * @param  aoServer  the server the account is hosted on
   *
   * @return  if the {@link UserServer} is in manual mode
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link UserServer} is not found
   *
   * @see  UserServer#isProcmailManual
   * @see  UserServer
   */
  public int isLinuxServerAccountProcmailManual(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServerAccount(aoServer, username).isProcmailManual();
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.mysql.Database} name is available on the specified
   * {@link Server}.
   *
   * @param  name  the name of the database
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @return  {@code true} if the {@link com.aoindustries.aoserv.client.mysql.Database} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the database name is invalid or unable
   *                                       to find the {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Server#isMysqlDatabaseNameAvailable
   */
  public boolean isMysqlDatabaseNameAvailable(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getMysqlServer(aoServer, mysqlServer).isMysqlDatabaseNameAvailable(name);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.mysql.Server} name is available on the specified
   * {@link Server}.
   *
   * @param  name  the name of the MySQL server
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @return  {@code true} if the {@link com.aoindustries.aoserv.client.mysql.Server} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the server name is invalid or unable
   *                                       to find the {@link Server}
   *
   * @see  Server#isMysqlServerNameAvailable
   */
  public boolean isMysqlServerNameAvailable(
      com.aoindustries.aoserv.client.mysql.Server.Name name,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServer(aoServer).isMysqlServerNameAvailable(name);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.mysql.UserServer} currently has a password set.
   *
   * @param  username  the username of the account
   * @param  aoServer  the server the account is hosted on
   *
   * @return  if the {@link com.aoindustries.aoserv.client.mysql.UserServer} has a password set
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.mysql.UserServer} is not found
   *
   * @see  com.aoindustries.aoserv.client.mysql.UserServer#arePasswordsSet
   * @see  #setMysqlServerUserPassword
   * @see  UserServer
   */
  public boolean isMysqlServerUserPasswordSet(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getMysqlServerUser(aoServer, mysqlServer, username).arePasswordsSet() == PasswordProtected.ALL;
  }

  /**
   * Determines if a {@link Package} name is available.
   *
   * @param  packageName  the name of the {@link Package}
   *
   * @return  {@code true} if the {@link Package} name is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  PackageTable#isPackageNameAvailable
   * @see  #generatePackageName
   * @see  #addPackage
   * @see  Package
   */
  public boolean isPackageNameAvailable(
      Account.Name packageName
  ) throws IOException, SQLException {
    return connector.getBilling().getPackage().isPackageNameAvailable(packageName);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.postgresql.Database} name is available on the specified
   * {@link Server}.
   *
   * @param  name  the name of the database
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @return  {@code true} if the {@link com.aoindustries.aoserv.client.postgresql.Database} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the database name is invalid or unable
   *                                       to find the {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Server#isPostgresDatabaseNameAvailable
   */
  public boolean isPostgresDatabaseNameAvailable(
      com.aoindustries.aoserv.client.postgresql.Database.Name name,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getPostgresServer(aoServer, postgresServer).isPostgresDatabaseNameAvailable(name);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.postgresql.Server} name is available on the specified
   * {@link Server}.
   *
   * @param  name  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @return  {@code true} if the {@link com.aoindustries.aoserv.client.postgresql.Server} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the server name is invalid or unable
   *                                       to find the {@link Server}
   *
   * @see  Server#isPostgresServerNameAvailable
   */
  public boolean isPostgresServerNameAvailable(
      com.aoindustries.aoserv.client.postgresql.Server.Name name,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServer(aoServer).isPostgresServerNameAvailable(name);
  }

  /**
   * Determines if a {@link com.aoindustries.aoserv.client.postgresql.UserServer} currently has a password set.
   *
   * @param  username  the username of the account
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the server the account is hosted on
   *
   * @return  if the {@link com.aoindustries.aoserv.client.postgresql.UserServer} has a password set
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link com.aoindustries.aoserv.client.postgresql.UserServer} is not found
   *
   * @see  com.aoindustries.aoserv.client.postgresql.UserServer#arePasswordsSet
   * @see  #setPostgresServerUserPassword
   * @see  UserServer
   */
  public boolean isPostgresServerUserPasswordSet(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getPostgresServerUser(aoServer, postgresServer, username).arePasswordsSet() == PasswordProtected.ALL;
  }

  /**
   * Determines if a {@link Domain} is available.
   *
   * @param  domain  the domain
   * @param  aoServer  the hostname of the server
   *
   * @return  {@code true} if the {@link Domain} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the {@link Domain} is invalid
   *
   * @see  Server#isEmailDomainAvailable
   * @see  #addEmailDomain
   * @see  Domain
   */
  public boolean isEmailDomainAvailable(
      DomainName domain,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getLinuxServer(aoServer).isEmailDomainAvailable(domain);
  }

  /**
   * Determines if a name is available for use as a {@link SharedTomcat}.
   *
   * @param  name  the name
   *
   * @return  {@code true} if the name is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  SharedTomcatTable#isSharedTomcatNameAvailable
   * @see  #generateSharedTomcatName
   * @see  SharedTomcat
   */
  public boolean isSharedTomcatNameAvailable(
      String name
  ) throws IOException, SQLException {
    return connector.getWeb_tomcat().getSharedTomcat().isSharedTomcatNameAvailable(name);
  }

  /**
   * Determines if a site name is available.
   *
   * @param  siteName  the site name
   *
   * @return  {@code true} if the site name is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if the site name is invalid
   *
   * @see  SiteTable#isSiteNameAvailable
   */
  public boolean isSiteNameAvailable(
      String siteName
  ) throws IllegalArgumentException, IOException, SQLException {
    checkSiteName(siteName);
    return connector.getWeb().getSite().isSiteNameAvailable(siteName);
  }

  /**
   * Determines if a {@link User} is available.
   *
   * @param  username  the username
   *
   * @return  {@code true} if the {@link User} is available
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   *
   * @see  UserTable#isUsernameAvailable(com.aoindustries.aoserv.client.account.User.Name)
   * @see  #addUsername
   * @see  User
   */
  public boolean isUsernameAvailable(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IOException, SQLException {
    return connector.getAccount().getUser().isUsernameAvailable(username);
  }

  ///**
  // * Kills a {@link Ticket}.  Once killed, a {@link Ticket} may not be modified in
  // * any way.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link TicketType}, or {@link Administrator}
  // *
  // * @see  Ticket#actKillTicket
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void killTicket(
  //  int ticket_id,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actKillTicket(pe, comments);
  //}

  /**
   * Moves all resources for one {@link Account} from one {@link Server}
   * to another {@link Server}.
   *
   * @param  business  the accounting code of the {@link Account}
   * @param  from  the hostname of the {@link Server} to get all the resources from
   * @param  to  the hostname of the {@link Server} to place all the resources on
   * @param  out  an optional {@link TerminalWriter} to send diagnostic output to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Account} or either
   *                                       of the {@link Server servers}
   *
   * @see  Account#move
   */
  public void moveAccount(
      Account.Name business,
      String from,
      String to,
      TerminalWriter out
  ) throws IllegalArgumentException, IOException, SQLException {
    getAccount(business).move(getLinuxServer(from), getLinuxServer(to), out);
  }

  /**
   * Moves an {@link IpAddress} from one {@link Host} to another.
   *
   * @param  ipAddress  the IP address to move
   * @param  toServer  the destination server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link IpAddress} or
   *                                       the {@link Host}
   *
   * @see  IpAddress#moveTo
   */
  public void moveIpAddress(
      InetAddress ipAddress,
      String fromServer,
      String fromNetDevice,
      String toServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getIpAddress(fromServer, fromNetDevice, ipAddress).moveTo(getHost(toServer));
  }

  /**
   * Times the latency of the communication with the server.
   *
   * @return  the latency of the communication in milliseconds
   *
   * @see  AoservConnector#ping
   */
  public int ping() throws IOException, SQLException {
    return connector.ping();
  }

  /**
   * Prints the contents of a {@link Zone} as used by the <code>named</code> process.
   *
   * @param  zone  the name of the {@link Zone}
   * @param  out  the {@link PrintWriter} to write to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Zone}
   *
   * @see  Zone#printZoneFile(java.io.PrintWriter)
   * @see  #addDnsZone
   */
  public void printZoneFile(
      String zone,
      PrintWriter out
  ) throws IllegalArgumentException, SQLException, IOException {
    getZone(zone).printZoneFile(out);
  }

  ///**
  // * Reactivates a {@link Ticket} that is in the hold state.
  // *
  // * @param  ticket_id  the pkey of the {@link Ticket}
  // * @param  administrator  the username of the {@link Administrator}
  // *                        making the change
  // * @param  comments  the details of the change
  // *
  // * @exception  IOException  if unable to contact the server
  // * @exception  SQLException  if unable to access the database or a data integrity
  // *                           violation occurs
  // * @exception  IllegalArgumentException  if unable to find the {@link Ticket},
  // *                                       {@link TicketType}, or {@link Administrator}
  // *
  // * @see  Ticket#actReactivateTicket
  // * @see  #addTicket
  // * @see  Action
  // */
  //public void reactivateTicket(
  //  int ticket_id,
  //  String administrator,
  //  String comments
  //) throws IllegalArgumentException, IOException, SQLException {
  //  Ticket ti=connector.getTickets().get(ticket_id);
  //  if (ti == null) {
  //    throw new IllegalArgumentException("Unable to find Ticket: "+ticket_id);
  //  }
  //  Administrator pe = connector.getAdministrators().get(administrator);
  //  if (pe == null) {
  //    throw new IllegalArgumentException("Unable to find Administrator: " + administrator);
  //  }
  //  ti.actReactivateTicket(pe, comments);
  //}

  /**
   * Refreshes the time window for SMTP server access by resetting the expiration to 24 hours from the current time.
   *
   * @param  pkey  the {@code id} of the {@link SmtpRelay}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link SmtpRelay}
   *
   * @see  SmtpRelay#refresh
   * @see  #addEmailSmtpRelay
   * @see  SmtpRelay
   */
  public void refreshEmailSmtpRelay(
      int pkey,
      long minDuration
  ) throws IllegalArgumentException, IOException, SQLException {
    SmtpRelay sr = connector.getEmail().getSmtpRelay().get(pkey);
    if (sr == null) {
      throw new IllegalArgumentException("Unable to find EmailSmtpRelay: " + pkey);
    }
    sr.refresh(minDuration);
  }

  /**
   * Removes a {@link BlackholeAddress} from the system.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain},
   *                                       {@link Address}, or {@link BlackholeAddress}
   *
   * @see  BlackholeAddress#remove()
   */
  public void removeBlackholeEmailAddress(
      String address,
      DomainName domain,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    Address addr = getEmailAddress(aoServer, domain, address);
    BlackholeAddress bea = addr.getBlackholeEmailAddress();
    if (bea == null) {
      throw new IllegalArgumentException("Unable to find BlackholeEmailAddress: " + address + '@' + domain + " on " + aoServer);
    }
    bea.remove();
    if (addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) {
      addr.remove();
    }
  }

  /**
   * Removes an {@link Administrator} from the system.
   *
   * @param  username  the username of the {@link Administrator}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User} or
   *                                  {@link Administrator}
   *
   * @see  Administrator#remove()
   * @see  #addAdministrator
   */
  public void removeAdministrator(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.account.User un = getUsername(username);
    Administrator ba = un.getAdministrator();
    if (ba == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    ba.remove();
  }

  /**
   * Revokes an {@link Account account's} access to a {@link Host}.  The server
   * must not have any resources allocated for the business, and the server must not
   * be the default server for the business.
   *
   * @param  accounting  the accounting code of the business
   * @param  server  the hostname of the server
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the business or server
   *
   * @see  AccountHost
   * @see  AccountHost#remove()
   * @see  #addAccountHost(com.aoindustries.aoserv.client.account.Account.Name, java.lang.String)
   * @see  #setDefaultAccountHost
   */
  public void removeAccountHost(
      Account.Name accounting,
      String server
  ) throws IllegalArgumentException, IOException, SQLException {
    Account bu = getAccount(accounting);
    Host se = getHost(server);
    AccountHost bs = bu.getAccountHost(se);
    if (bs == null) {
      throw new IllegalArgumentException("Unable to find AccountHost: accounting=" + accounting + " and server=" + server);
    }
    bs.remove();
  }

  /**
   * Removes a {@link CreditCard}.
   *
   * @param  pkey  the {@code id} of the {@link CreditCard} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link CreditCard}
   *
   * @see  CreditCard#remove
   */
  public void removeCreditCard(
      int pkey
  ) throws IllegalArgumentException, SQLException, IOException {
    CreditCard cc = connector.getPayment().getCreditCard().get(pkey);
    if (cc == null) {
      throw new IllegalArgumentException("Unable to find CreditCard: " + pkey);
    }
    cc.remove();
  }

  /**
   * Removes a {@link CvsRepository}.
   *
   * @param  aoServer  the hostname of the {@link Server}
   * @param  path  the path of the repository
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                  {@link CvsRepository}
   *
   * @see  CvsRepository#remove
   * @see  #addCvsRepository
   * @see  CvsRepository
   */
  public void removeCvsRepository(
      String aoServer,
      PosixPath path
  ) throws IllegalArgumentException, IOException, SQLException {
    Server ao = getLinuxServer(aoServer);
    CvsRepository cr = ao.getCvsRepository(path);
    if (cr == null) {
      throw new IllegalArgumentException("Unable to find CvsRepository: " + path + " on " + aoServer);
    }
    cr.remove();
  }

  /**
   * Removes one record from a {@link Zone}.
   *
   * @param  pkey  the {@code id} of the {@link Record} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Record}
   *
   * @see  Record#remove()
   * @see  #addDnsRecord
   * @see  Record
   */
  public void removeDnsRecord(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    Record nr = connector.getDns().getRecord().get(pkey);
    if (nr == null) {
      throw new IllegalArgumentException("Unable to find DNSRecord: " + pkey);
    }
    nr.remove();
  }

  public void removeDnsRecord(
      String zone,
      String domain,
      String type,
      String tag,
      String destination
  ) throws IllegalArgumentException, IOException, SQLException {
    Zone nz = getZone(zone);

    // Must be a valid type
    RecordType nt = connector.getDns().getRecordType().get(type);
    if (nt == null) {
      throw new IllegalArgumentException("Unable to find RecordType: " + type);
    }
    // Must have a valid destination type
    nt.checkDestination(tag, destination);

    // Find the record matching all four fields, should be one and *only* one
    Record found = null;
    for (Record rec : nz.getRecords(domain, nt)) {
      if (
          Objects.equals(rec.getTag(), tag)
              && rec.getDestination().equals(destination)
      ) {
        if (found != null) {
          throw new AssertionError("Duplicate DNSRecord: (" + zone + ", " + domain + ", " + type + ", " + tag + ", " + destination + ")");
        }
        found = rec;
      }
    }
    if (found == null) {
      throw new AssertionError("Unable to find DNSRecord: (" + zone + ", " + domain + ", " + type + ", " + tag + ", " + destination + ")");
    }
    found.remove();
  }

  /**
   * Completely removes a {@link Zone} from the servers.
   *
   * @param  zone  the name of the {@link Zone} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Zone}
   *
   * @see  Zone#remove()
   * @see  #addDnsZone
   * @see  Zone
   */
  public void removeDnsZone(
      String zone
  ) throws IllegalArgumentException, IOException, SQLException {
    getZone(zone).remove();
  }

  /**
   * Completely removes a {@link Zone} from the servers.
   *
   * @param  zone  the name of the {@link Zone} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Zone}
   *
   * @see  Zone#remove()
   * @see  #addDnsZone
   * @see  Zone
   */
  public void setDnsZoneTtl(
      String zone,
      int ttl
  ) throws IllegalArgumentException, IOException, SQLException {
    getZone(zone).setTtl(ttl);
  }

  /**
   * Removes an {@link Address} from the system.  If the {@link Address} is used
   * by other resources, such as {@link ListAddress}, those resources are also removed.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain} or
   *                                       {@link Address}
   *
   * @see  Address#remove()
   * @see  #addEmailForwarding(java.lang.String, com.aoapps.net.DomainName, java.lang.String, com.aoapps.net.Email)
   * @see  #addEmailListAddress(java.lang.String, com.aoapps.net.DomainName, com.aoindustries.aoserv.client.linux.PosixPath, java.lang.String)
   * @see  #addEmailPipeAddress(java.lang.String, com.aoapps.net.DomainName, int)
   * @see  #addLinuxAccAddress(java.lang.String, com.aoapps.net.DomainName, java.lang.String, com.aoindustries.aoserv.client.linux.User.Name)
   */
  public void removeEmailAddress(
      String address,
      DomainName domain,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getEmailAddress(aoServer, domain, address).remove();
  }

  /**
   * Removes an {@link Forwarding} from the system.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  aoServer  the hostname of the server that hosts this domain
   * @param  destination  the destination of the email forwarding
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain},
   *                                       {@link Address}, or {@link Forwarding}
   *
   * @see  Forwarding#remove
   * @see  #addEmailForwarding
   */
  public void removeEmailForwarding(
      String address,
      DomainName domain,
      String aoServer,
      Email destination
  ) throws IllegalArgumentException, IOException, SQLException {
    Address addr = getEmailAddress(aoServer, domain, address);
    Forwarding ef = addr.getEmailForwarding(destination);
    if (ef == null) {
      throw new IllegalArgumentException("Unable to find EmailForwarding: " + address + '@' + domain + "->" + destination + " on " + aoServer);
    }
    ef.remove();
    if (addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) {
      addr.remove();
    }
  }

  /**
   * Removes an {@link com.aoindustries.aoserv.client.email.List} from the system.  All {@link Address}es that are directed
   * to the list are also removed.  The file that stores the list contents is removed from the file system.
   *
   * @param  path  the path of the {@link com.aoindustries.aoserv.client.email.List} to remove
   * @param  aoServer  the server that hosts this list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.email.List}
   *
   * @see  com.aoindustries.aoserv.client.email.List#remove
   * @see  #addEmailList
   */
  public void removeEmailList(
      PosixPath path,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getEmailList(aoServer, path).remove();
  }

  /**
   * Removes an {@link ListAddress} from the system.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  path  the list the emails are sent to
   * @param  aoServer  the hostname of the server hosting the list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain},
   *                                       {@link Address}, {@link com.aoindustries.aoserv.client.email.List}, or
   *                                       {@link ListAddress}
   *
   * @see  ListAddress#remove()
   * @see  #addEmailListAddress(java.lang.String, com.aoapps.net.DomainName, com.aoindustries.aoserv.client.linux.PosixPath, java.lang.String)
   */
  public void removeEmailListAddress(
      String address,
      DomainName domain,
      PosixPath path,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    Address addr = getEmailAddress(aoServer, domain, address);
    com.aoindustries.aoserv.client.email.List el = getEmailList(aoServer, path);
    ListAddress ela = addr.getEmailListAddress(el);
    if (ela == null) {
      throw new IllegalArgumentException("Unable to find EmailListAddress: " + address + '@' + domain + "->" + path + " on " + aoServer);
    }
    ela.remove();
    if (addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) {
      addr.remove();
    }
  }

  /**
   * Removes an {@link Pipe} from the system.  All {@link Address}es that are directed
   * to the pipe are also removed.
   *
   * @param  pkey  the {@code id} of the {@link Pipe} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Pipe}
   *
   * @see  Pipe#remove
   * @see  #addEmailPipe
   */
  public void removeEmailPipe(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    Pipe ep = connector.getEmail().getPipe().get(pkey);
    if (ep == null) {
      throw new IllegalArgumentException("Unable to find EmailPipe: " + pkey);
    }
    ep.remove();
  }

  /**
   * Removes an {@link PipeAddress} from the system.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  pipe  the pkey of the email pipe
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain},
   *                                       {@link Address}, {@link Pipe}, or
   *                                       {@link PipeAddress}
   *
   * @see  PipeAddress#remove()
   * @see  #addEmailPipeAddress(java.lang.String, com.aoapps.net.DomainName, int)
   */
  public void removeEmailPipeAddress(
      String address,
      DomainName domain,
      int pipe
  ) throws IllegalArgumentException, IOException, SQLException {
    Pipe ep = connector.getEmail().getPipe().get(pipe);
    if (ep == null) {
      throw new IllegalArgumentException("Unable to find EmailPipe: " + pipe);
    }
    Server ao = ep.getLinuxServer();
    Domain sd = ao.getEmailDomain(domain);
    if (sd == null) {
      throw new IllegalArgumentException("Unable to find EmailDomain: " + domain + " on " + ao.getHostname());
    }
    Address addr = connector.getEmail().getAddress().getEmailAddress(address, sd);
    if (addr == null) {
      throw new IllegalArgumentException("Unable to find EmailAddress: " + address + "@" + domain + " on " + ao.getHostname());
    }
    PipeAddress epa = addr.getEmailPipeAddress(ep);
    if (epa == null) {
      throw new IllegalArgumentException("Unable to find EmailPipeAddress: " + address + "@" + domain + "->" + ep);
    }
    epa.remove();
    if (addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) {
      addr.remove();
    }
  }

  /**
   * Removes the {@link GuestUser} flag from a {@link com.aoindustries.aoserv.client.linux.User}, allowing access
   * to the server root directory.
   *
   * @param  username  the username of the {@link GuestUser}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link GuestUser}
   *
   * @see  GuestUser#remove()
   * @see  #addFtpGuestUser(com.aoindustries.aoserv.client.linux.User.Name)
   */
  public void removeFtpGuestUser(
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    GuestUser ftpUser = connector.getFtp().getGuestUser().get(username);
    if (ftpUser == null) {
      throw new IllegalArgumentException("Unable to find FtpGuestUser: " + username);
    }
    ftpUser.remove();
  }

  /**
   * Completely removes a {@link SharedTomcat} from the servers.
   *
   * @param  name  the name of the site
   * @param  aoServer  the server the site runs on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link SharedTomcat}
   *
   * @see  SharedTomcat#remove
   * @see  SharedTomcat
   */
  public void removeHttpdSharedTomcat(
      String name,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).remove();
  }

  /**
   * Completely removes a {@link Site} from the servers.
   *
   * @param  name  the name of the site
   * @param  aoServer  the server the site runs on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Site}
   *
   * @see  Site#remove
   * @see  Site
   */
  public void removeHttpdSite(
      String name,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, name).remove();
  }

  /**
   * Removes a {@link VirtualHostName} from the servers.
   *
   * @param  pkey  the pkey of the site URL
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHostName}
   *
   * @see  VirtualHostName#remove
   */
  public void removeVirtualHostName(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    VirtualHostName hsu = connector.getWeb().getVirtualHostName().get(pkey);
    if (hsu == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteURL: " + pkey);
    }
    hsu.remove();
  }

  /**
   * Removes a {@link Context} from the servers.
   *
   * @param  pkey  the pkey of the context
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Context}
   *
   * @see  Context#remove
   */
  public void removeHttpdTomcatContext(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    Context htc = connector.getWeb_tomcat().getContext().get(pkey);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + pkey);
    }
    htc.remove();
  }

  /**
   * Removes a {@link ContextDataSource} from a {@link Context}.
   *
   * @param  pkey  the pkey of the data source
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link ContextDataSource}
   *
   * @see  Context#remove
   */
  public void removeHttpdTomcatDataSource(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    ContextDataSource htds = connector.getWeb_tomcat().getContextDataSource().get(pkey);
    if (htds == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: " + pkey);
    }
    htds.remove();
  }

  /**
   * Removes a {@link ContextParameter} from a {@link Context}.
   *
   * @param  pkey  the pkey of the parameter
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link ContextParameter}
   *
   * @see  Context#remove
   */
  public void removeHttpdTomcatParameter(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    ContextParameter htp = connector.getWeb_tomcat().getContextParameter().get(pkey);
    if (htp == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: " + pkey);
    }
    htp.remove();
  }

  /**
   * Removes a {@link InboxAddress} from the system.
   *
   * @param  address  the part of the email address before the <code>@</code>
   * @param  domain  the part of the email address after the <code>@</code>
   * @param  username  the account the emails are sent to
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain},
   *                                       {@link Address}, {@link User},
   *                                       {@link com.aoindustries.aoserv.client.linux.User}, or {@link InboxAddress}
   *
   * @see  InboxAddress#remove()
   * @see  #addLinuxAccAddress(java.lang.String, com.aoapps.net.DomainName, java.lang.String, com.aoindustries.aoserv.client.linux.User.Name)
   */
  public void removeLinuxAccAddress(
      String address,
      DomainName domain,
      String aoServer,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    Address addr = getEmailAddress(aoServer, domain, address);
    UserServer lsa = getLinuxServerAccount(aoServer, username);
    InboxAddress laa = addr.getLinuxAccAddress(lsa);
    if (laa == null) {
      throw new IllegalArgumentException("Unable to find LinuxAccAddress: " + address + '@' + domain + "->" + username + " on " + aoServer);
    }
    laa.remove();
    if (addr.getCannotRemoveReasons().isEmpty() && !addr.isUsed()) {
      addr.remove();
    }
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.linux.User} and all related data from the system.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#remove
   * @see  #addLinuxAccount
   */
  public void removeLinuxAccount(
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).remove();
  }

  /**
   * Removes a {@link Group} and all related data from the system.
   *
   * @param  name  the name of the {@link Group} to remove
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Group}
   *
   * @see  Group#remove
   * @see  #addLinuxGroup
   */
  public void removeLinuxGroup(
      Group.Name name
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxGroup(name).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.linux.User}'s access to a {@link Group}.
   *
   * @param  group  the name of the {@link Group} to remove access to
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User} to remove access from
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Group},
   *                                       {@link com.aoindustries.aoserv.client.linux.User}, or {@link GroupUser}
   *
   * @see  GroupUser#remove
   * @see  #addLinuxGroupAccount
   * @see  #addLinuxGroup
   * @see  #addLinuxAccount
   */
  public void removeLinuxGroupAccount(
      Group.Name group,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    Group lg = getLinuxGroup(group);
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount(username);
    List<GroupUser> lgas = connector.getLinux().getGroupUser().getLinuxGroupAccounts(group, username);
    if (lgas.isEmpty()) {
      throw new IllegalArgumentException(username + " is not part of the " + group + " group");
    }
    for (GroupUser lga : lgas) {
      lga.remove();
    }
  }

  /**
   * Removes a {@link UserServer} from a {@link Server}.
   *
   * @param  username  the username of the {@link UserServer} to remove
   * @param  aoServer  the hostname of the {@link Server} to remove the account from
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User},
   *                                       {@link Server}, or {@link UserServer}
   *
   * @see  UserServer#remove
   * @see  #addLinuxServerAccount
   */
  public void removeLinuxServerAccount(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).remove();
  }

  /**
   * Removes a {@link GroupServer} from a {@link Server}.
   *
   * @param  group  the name of the {@link GroupServer} to remove
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Group},
   *                                       {@link Server}, or {@link GroupServer}
   *
   * @see  GroupServer#remove()
   * @see  #addLinuxServerGroup(com.aoindustries.aoserv.client.linux.Group.Name, java.lang.String)
   */
  public void removeLinuxServerGroup(
      Group.Name group,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerGroup(aoServer, group).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.mysql.Database} from the system.  All related
   * {@link com.aoindustries.aoserv.client.mysql.DatabaseUser}s are also removed, and all data is removed
   * from the MySQL server.  The data is not dumped or backed-up during
   * the removal, if a backup is desired, use {@link #dumpMysqlDatabase(com.aoindustries.aoserv.client.mysql.Database.Name, com.aoindustries.aoserv.client.mysql.Server.Name, java.lang.String, java.io.Writer)}.
   *
   * @param  name  the name of the database
   * @param  aoServer  the server the database is hosted on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.mysql.Database}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Database#remove
   * @see  #addMysqlDatabase
   * @see  #dumpMysqlDatabase
   */
  public void removeMysqlDatabase(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlDatabase(aoServer, mysqlServer, name).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.mysql.DatabaseUser} from the system.  The {@link com.aoindustries.aoserv.client.mysql.User} is
   * no longer allowed to access the {@link com.aoindustries.aoserv.client.mysql.Database}.
   *
   * @param  name  the name of the {@link com.aoindustries.aoserv.client.mysql.Database}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                       {@link com.aoindustries.aoserv.client.mysql.Database},
   *                                       {@link com.aoindustries.aoserv.client.mysql.UserServer}, or
   *                                       {@link com.aoindustries.aoserv.client.mysql.DatabaseUser}
   *
   * @see  com.aoindustries.aoserv.client.mysql.DatabaseUser#remove()
   * @see  #addMysqlDbUser
   */
  public void removeMysqlDbUser(
      com.aoindustries.aoserv.client.mysql.Database.Name name,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.mysql.Database md = getMysqlDatabase(aoServer, mysqlServer, name);
    com.aoindustries.aoserv.client.mysql.UserServer msu = getMysqlServerUser(aoServer, mysqlServer, username);
    com.aoindustries.aoserv.client.mysql.DatabaseUser mdu = md.getMysqlDbUser(msu);
    if (mdu == null) {
      throw new IllegalArgumentException("Unable to find MysqlDbUser on MysqlServer " + mysqlServer + " on Server "
          + aoServer + " for MysqlDatabase named " + name + " and MysqlServerUser named " + username);
    }
    mdu.remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.mysql.UserServer} from a the system..  The {@link com.aoindustries.aoserv.client.mysql.User} is
   * no longer allowed to access the {@link Server}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.mysql.UserServer}
   *
   * @see  com.aoindustries.aoserv.client.mysql.UserServer#remove
   * @see  #addMysqlServerUser
   */
  public void removeMysqlServerUser(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlServerUser(aoServer, mysqlServer, username).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.mysql.User} from a the system.  All of the associated
   * {@link com.aoindustries.aoserv.client.mysql.UserServer}s and {@link com.aoindustries.aoserv.client.mysql.DatabaseUser}s are also removed.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.mysql.User}
   *
   * @see  com.aoindustries.aoserv.client.mysql.User#remove
   * @see  #addMysqlUser
   * @see  #removeMysqlServerUser
   */
  public void removeMysqlUser(
      com.aoindustries.aoserv.client.mysql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlUser(username).remove();
  }

  /**
   * Removes a {@link Bind} from a the system.
   *
   * @param  pkey  the primary key of the {@link Bind}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Bind}
   *
   * @see  Bind#remove
   */
  public void removeNetBind(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    getNetBind(pkey).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.postgresql.Database} from the system.  All data is removed
   * from the PostgreSQL server.  The data is not dumped or backed-up during
   * the removal, if a backup is desired, use {@link #dumpPostgresDatabase(com.aoindustries.aoserv.client.postgresql.Database.Name, com.aoindustries.aoserv.client.postgresql.Server.Name, java.lang.String, java.io.Writer)}.
   *
   * @param  name  the name of the database
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the server the database is hosted on
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.postgresql.Database}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Database#remove()
   * @see  #addPostgresDatabase
   * @see  #dumpPostgresDatabase
   */
  public void removePostgresDatabase(
      com.aoindustries.aoserv.client.postgresql.Database.Name name,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresDatabase(aoServer, postgresServer, name).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.postgresql.UserServer} from a the system..  The {@link com.aoindustries.aoserv.client.postgresql.User} is
   * no longer allowed to access the {@link Server}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.UserServer}
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or
   *                                       {@link com.aoindustries.aoserv.client.postgresql.UserServer}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.UserServer#remove
   */
  public void removePostgresServerUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresServerUser(aoServer, postgresServer, username).remove();
  }

  /**
   * Removes a {@link com.aoindustries.aoserv.client.postgresql.User} from a the system..  All of the associated
   * {@link com.aoindustries.aoserv.client.postgresql.UserServer}s are also removed.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.postgresql.User}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.User#remove
   * @see  #addPostgresUser
   * @see  #removePostgresServerUser
   */
  public void removePostgresUser(
      com.aoindustries.aoserv.client.postgresql.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresUser(username).remove();
  }

  /**
   * Removes an {@link Domain} and all of its {@link Address}es.
   *
   * @param  domain  the name of the {@link Domain}
   * @param  aoServer  the server hosting this domain
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Domain}
   *
   * @see  Domain#remove
   * @see  #addEmailDomain
   * @see  #removeEmailAddress
   */
  public void removeEmailDomain(
      DomainName domain,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    getEmailDomain(aoServer, domain).remove();
  }

  /**
   * Removes an {@link SmtpRelay} from the system, revoking access to the SMTP
   * server from one IP address.
   *
   * @param  pkey  the {@code id} of the {@link Domain}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link SmtpRelay}
   *
   * @see  SmtpRelay#remove
   * @see  #addEmailSmtpRelay
   * @see  #refreshEmailSmtpRelay
   */
  public void removeEmailSmtpRelay(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    SmtpRelay sr = connector.getEmail().getSmtpRelay().get(pkey);
    if (sr == null) {
      throw new IllegalArgumentException("Unable to find EmailSmtpRelay: " + pkey);
    }
    sr.remove();
  }

  /**
   * Removes a {@link FileReplicationSetting} from the system.
   *
   * @param  replication  the pkey of the {@link FileReplication}
   * @param  path  the path of the setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link FileReplication} or {@link FileReplicationSetting}
   *
   * @see  FileReplicationSetting#remove()
   * @see  #addFileBackupSetting
   */
  public void removeFileBackupSetting(
      int replication,
      String path
  ) throws IllegalArgumentException, IOException, SQLException {
    FileReplication ffr = getConnector().getBackup().getFileReplication().get(replication);
    if (ffr == null) {
      throw new IllegalArgumentException("Unable to find FailoverFileReplication: " + replication);
    }
    FileReplicationSetting fbs = ffr.getFileBackupSetting(path);
    if (fbs == null) {
      throw new IllegalArgumentException("Unable to find FileBackupSetting: " + path + " on " + replication);
    }
    fbs.remove();
  }

  /**
   * Removes a {@link MajordomoServer} and all of its {@link MajordomoList}s.
   *
   * @param  domain  the name of the {@link MajordomoServer}
   * @param  aoServer  the server hosting the list
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                  {@link Domain} or {@link MajordomoServer}
   *
   * @see  MajordomoServer#remove
   * @see  #addMajordomoServer
   */
  public void removeMajordomoServer(
      DomainName domain,
      String aoServer
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain sd = getEmailDomain(aoServer, domain);
    MajordomoServer ms = sd.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    ms.remove();
  }

  /**
   * Removes a {@link User} from the system.
   *
   * @param  username  the username of the {@link User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  User#remove
   * @see  #addUsername
   */
  public void removeUsername(
      com.aoindustries.aoserv.client.account.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getUsername(username).remove();
  }

  /**
   * Restarts the Apache web server.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#restartApache
   */
  public void restartApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).restartApache();
  }

  /**
   * Restarts the cron doggie.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#restartCron
   */
  public void restartCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).restartCron();
  }

  /**
   * Restarts the MySQL database server.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Server#restartMysql
   */
  public void restartMysql(com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getMysqlServer(aoServer, mysqlServer).restartMysql();
  }

  /**
   * Restarts the PostgreSQL database server.
   *
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Server#restartPostgresql
   */
  public void restartPostgresql(com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getPostgresServer(aoServer, postgresServer).restartPostgresql();
  }

  /**
   * Restarts the X Font Server.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#restartXfs
   */
  public void restartXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).restartXfs();
  }

  /**
   * Restarts the X Virtual Frame Buffer.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#restartXvfb
   */
  public void restartXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).restartXvfb();
  }

  /**
   * Sets the autoresponder behavior for a Linux server account.
   *
   * @param  username  the username of the account
   * @param  aoServer  the server the account is on
   * @param  address  the address part of the email address
   * @param  domain  the domain of the email address
   * @param  subject  the subject of the email
   * @param  content  the content of the email
   * @param  enabled  if the autoresponder is enabled or not
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Address} or
   *                                  the {@link UserServer}
   *
   * @see  UserServer#setAutoresponder
   */
  public void setAutoresponder(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String address,
      DomainName domain,
      String subject,
      String content,
      boolean enabled
  ) throws IllegalArgumentException, IOException, SQLException {
    final UserServer lsa = getLinuxServerAccount(aoServer, username);
    if (address == null) {
      address = "";
    }
    Address ea;
    if (domain == null) {
      if (address.length() > 0) {
        throw new IllegalArgumentException("Cannot have an address without a domain: " + address);
      }
      ea = null;
    } else {
      Domain sd = getEmailDomain(aoServer, domain);
      ea = sd.getEmailAddress(address);
      if (ea == null) {
        throw new IllegalArgumentException("Unable to find EmailAddress: " + address + '@' + domain + " on " + aoServer);
      }
    }
    if (subject != null && subject.length() == 0) {
      subject = null;
    }
    if (content != null && content.length() == 0) {
      content = null;
    }
    InboxAddress laa = ea == null ? null : ea.getLinuxAccAddress(lsa);
    if (laa == null) {
      throw new IllegalArgumentException("Unable to find LinuxAccAddress: " + address + " on " + aoServer);
    }
    lsa.setAutoresponder(laa, subject, content, enabled);
  }

  /**
   * Sets the accounting code for the business.  The accounting code is the value that uniquely
   * identifies an account within the system.
   *
   * @param  oldAccounting  the old accounting code
   * @param  newAccounting  the new accounting code
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Account} or
   *                                  the requested accounting code is not valid
   *
   * @see  Account#setName(com.aoindustries.aoserv.client.account.Account.Name)
   */
  public void setAccountAccounting(
      Account.Name oldAccounting,
      Account.Name newAccounting
  ) throws IllegalArgumentException, IOException, SQLException {
    getAccount(oldAccounting).setName(newAccounting);
  }

  /**
   * Sets the password for an {@link Administrator}.  This password must pass the security
   * checks provided by {@link #checkAdministratorPassword(com.aoindustries.aoserv.client.account.User.Name, java.lang.String)}.
   *
   * @param  username  the username of the {@link Administrator}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Administrator}
   *
   * @see  Administrator#setPassword(java.lang.String)
   * @see  #addAdministrator
   */
  public void setAdministratorPassword(
      com.aoindustries.aoserv.client.account.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    Administrator pe = connector.getAccount().getAdministrator().get(username);
    if (pe == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    pe.setPassword(password);
  }

  /**
   * Sets the profile of an {@link Administrator}, which is all of their contact
   * information and other details.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Administrator}
   *
   * @see  Administrator#setProfile(java.lang.String, java.lang.String, java.sql.Date, boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.aoapps.net.Email, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   * @see  #addAdministrator
   */
  public void setAdministratorProfile(
      com.aoindustries.aoserv.client.account.User.Name username,
      String name,
      String title,
      Date birthday,
      boolean isPrivate,
      String workPhone,
      String homePhone,
      String cellPhone,
      String fax,
      Email email,
      String address1,
      String address2,
      String city,
      String state,
      String country,
      String zip
  ) throws IllegalArgumentException, IOException, SQLException {
    Administrator administrator = connector.getAccount().getAdministrator().get(username);
    if (administrator == null) {
      throw new IllegalArgumentException("Unable to find Administrator: " + username);
    }
    administrator.setProfile(
        name,
        title,
        birthday,
        isPrivate,
        workPhone,
        homePhone,
        cellPhone,
        fax,
        email,
        address1,
        address2,
        city,
        state,
        country,
        zip
    );
  }

  /**
   * Sets a user's cron table on one server.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  aoServer  the server to get the data from
   * @param  cronTable  the new cron table
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the source {@link UserServer}
   *
   * @see  UserServer#setCronTable
   * @see  #getCronTable
   * @see  #addLinuxServerAccount
   * @see  #removeLinuxServerAccount
   */
  public void setCronTable(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String cronTable
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setCronTable(cronTable);
  }

  /**
   * Sets the permissions for a CVS repository directory.
   *
   * @param  aoServer  the server the repository exists on
   * @param  path  the path of the server
   * @param  mode  the permission bits
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the source {@link CvsRepository}
   *
   * @see  CvsRepository#setMode
   * @see  #addCvsRepository
   * @see  #removeCvsRepository
   */
  public void setCvsRepositoryMode(
      String aoServer,
      PosixPath path,
      long mode
  ) throws IllegalArgumentException, IOException, SQLException {
    Server ao = getLinuxServer(aoServer);
    CvsRepository cr = ao.getCvsRepository(path);
    if (cr == null) {
      throw new IllegalArgumentException("Unable to find CvsRepository: " + path + " on " + aoServer);
    }
    cr.setMode(mode);
  }

  /**
   * Sets the default {@link Host} for an {@link Account}.
   *
   * @param  accounting  the accounting code of the business
   * @param  server  the hostname of the server
   *
   * @exception  IOException  if unable to communicate with the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the business or server
   *
   * @see  AccountHost
   * @see  AccountHost#setAsDefault
   * @see  #addAccountHost
   * @see  #removeAccountHost
   */
  public void setDefaultAccountHost(
      Account.Name accounting,
      String server
  ) throws IllegalArgumentException, SQLException, IOException {
    Account bu = getAccount(accounting);
    Host se = getHost(server);
    AccountHost bs = bu.getAccountHost(se);
    if (bs == null) {
      throw new IllegalArgumentException("Unable to find AccountHost: accounting=" + accounting + " and server=" + server);
    }
    bs.setAsDefault();
  }

  /**
   * Sets the list of addresses that an {@link com.aoindustries.aoserv.client.email.List} will forward messages
   * to.
   *
   * @param  path  the path of the {@link com.aoindustries.aoserv.client.email.List}
   * @param  aoServer  the server hosting the list
   * @param  addresses  the list of addresses, one address per line
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.email.List}
   *
   * @see  com.aoindustries.aoserv.client.email.List#setAddressList
   * @see  #getEmailListAddressList
   * @see  #addEmailList
   */
  public void setEmailListAddressList(
      PosixPath path,
      String aoServer,
      String addresses
  ) throws IllegalArgumentException, IOException, SQLException {
    getEmailList(aoServer, path).setAddressList(addresses);
  }

  /**
   * Sets the settings contained by one {@link FileReplicationSetting}.
   *
   * @param  replication  the hostname of the {@link FileReplication}
   * @param  path  the path of the setting
   * @param  backupEnabled  the enabled flag for the prefix
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link FileReplication} or {@link FileReplicationSetting}
   *
   * @see  FileReplicationSetting#setSettings(java.lang.String, boolean, boolean)
   * @see  #addFileBackupSetting
   */
  public void setFileBackupSetting(
      int replication,
      String path,
      boolean backupEnabled,
      boolean required
  ) throws IllegalArgumentException, IOException, SQLException {
    FileReplication ffr = getConnector().getBackup().getFileReplication().get(replication);
    if (ffr == null) {
      throw new IllegalArgumentException("Unable to find FailoverFileReplication: " + replication);
    }
    FileReplicationSetting fbs = ffr.getFileBackupSetting(path);
    if (fbs == null) {
      throw new IllegalArgumentException("Unable to find FileBackupSetting: " + path + " on " + replication);
    }
    fbs.setSettings(
        path,
        backupEnabled,
        required
    );
  }

  /**
   * Sets the <code>is_manual</code> flag for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  isManual  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link SharedTomcat}
   *
   * @see  SharedTomcat#setIsManual
   */
  public void setHttpdSharedTomcatIsManual(
      String name,
      String aoServer,
      boolean isManual
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).setIsManual(isManual);
  }

  /**
   * Sets the <code>maxPostSize</code> for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  maxPostSize  the new maximum POST size, in bytes, {@code -1} for none.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link SharedTomcat}
   *
   * @see  SharedTomcat#setMaxPostSize(int)
   */
  public void setHttpdSharedTomcatMaxPostSize(
      String name,
      String aoServer,
      int maxPostSize
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).setMaxPostSize(maxPostSize);
  }

  /**
   * Sets the <code>unpackWars</code> setting for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  unpackWars  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link SharedTomcat}
   *
   * @see  SharedTomcat#setUnpackWars(boolean)
   */
  public void setHttpdSharedTomcatUnpackWars(
      String name,
      String aoServer,
      boolean unpackWars
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).setUnpackWars(unpackWars);
  }

  /**
   * Sets the <code>autoDeploy</code> setting for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  autoDeploy  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link SharedTomcat}
   *
   * @see  SharedTomcat#setAutoDeploy(boolean)
   */
  public void setHttpdSharedTomcatAutoDeploy(
      String name,
      String aoServer,
      boolean autoDeploy
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).setAutoDeploy(autoDeploy);
  }

  /**
   * Sets the <code>tomcatAuthentication</code> setting for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  autoDeploy  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link SharedTomcat}
   *
   * @see  SharedTomcat#setTomcatAuthentication(boolean)
   */
  public void setHttpdSharedTomcatTomcatAuthentication(
      String name,
      String aoServer,
      boolean autoDeploy
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSharedTomcat(aoServer, name).setTomcatAuthentication(autoDeploy);
  }

  /**
   * Sets the Tomcat version for a {@link SharedTomcat}.
   *
   * @param  name  the name of the JVM
   * @param  aoServer  the hostname of the {@link Server}
   * @param  version  the new version
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @throws IllegalArgumentException if unable to find the {@link Server}, {@link SharedTomcat}, or {@link com.aoindustries.aoserv.client.web.tomcat.Version}.
   *
   * @see  SharedTomcat#setHttpdTomcatVersion(com.aoindustries.aoserv.client.web.tomcat.Version)
   */
  public void setHttpdSharedTomcatVersion(
      String name,
      String aoServer,
      String version
  ) throws IllegalArgumentException, IOException, SQLException {
    SharedTomcat hst = getHttpdSharedTomcat(aoServer, name);
    hst.setHttpdTomcatVersion(
        findTomcatVersion(hst.getLinuxServer(), version)
    );
  }

  /**
   * Sets the <code>is_manual</code> flag for a {@link VirtualHost}.
   *
   * @param  pkey  the primary key of the {@link VirtualHost}
   * @param  isManual  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHost}
   *
   * @see  VirtualHost#setIsManual(boolean)
   */
  public void setHttpdSiteBindIsManual(
      int pkey,
      boolean isManual
  ) throws IllegalArgumentException, IOException, SQLException {
    VirtualHost hsb = connector.getWeb().getVirtualHost().get(pkey);
    if (hsb == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteBind: " + pkey);
    }
    hsb.setIsManual(isManual);
  }

  /**
   * Sets the <code>redirect_to_primary_hostname</code> flag for a {@link VirtualHost}.
   *
   * @param  pkey  the primary key of the {@link VirtualHost}
   * @param  redirectToPrimaryHostname  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHost}
   *
   * @see  VirtualHost#setRedirectToPrimaryHostname
   */
  public void setHttpdSiteBindRedirectToPrimaryHostname(
      int pkey,
      boolean redirectToPrimaryHostname
  ) throws IllegalArgumentException, IOException, SQLException {
    VirtualHost hsb = connector.getWeb().getVirtualHost().get(pkey);
    if (hsb == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteBind: " + pkey);
    }
    hsb.setRedirectToPrimaryHostname(redirectToPrimaryHostname);
  }

  /**
   * Sets the <code>is_manual</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  isManual  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setIsManual
   */
  public void setHttpdSiteIsManual(
      String siteName,
      String aoServer,
      boolean isManual
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setIsManual(isManual);
  }

  /**
   * Sets the administrative email address for a {@link Site}.
   *
   * @param  siteName  the name of the {@link Site}
   * @param  aoServer  the hostname of the server that hosts the site
   * @param  emailAddress  the new adminstrative email address
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site},
   *                                  or the email address is not in a valid format
   *
   * @see  Site#setServerAdmin
   */
  public void setHttpdSiteServerAdmin(
      String siteName,
      String aoServer,
      Email emailAddress
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setServerAdmin(emailAddress);
  }

  /**
   * Sets the PHP version for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  phpVersion  the new version
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or PHP version.
   *
   * @see  Site#setPhpVersion(com.aoindustries.aoserv.client.distribution.SoftwareVersion)
   */
  public void setHttpdSitePhpVersion(
      String siteName,
      String aoServer,
      String phpVersion
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    hs.setPhpVersion(
        findPhpVersion(hs.getLinuxServer(), phpVersion)
    );
  }

  /**
   * Sets the <code>enable_cgi</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableCgi  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableCgi(boolean)
   */
  public void setHttpdSiteEnableCgi(
      String siteName,
      String aoServer,
      boolean enableCgi
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableCgi(enableCgi);
  }

  /**
   * Sets the <code>enable_ssi</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableSsi  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableSsi(boolean)
   */
  public void setHttpdSiteEnableSsi(
      String siteName,
      String aoServer,
      boolean enableSsi
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableSsi(enableSsi);
  }

  /**
   * Sets the <code>enable_htaccess</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableHtaccess  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableHtaccess(boolean)
   */
  public void setHttpdSiteEnableHtaccess(
      String siteName,
      String aoServer,
      boolean enableHtaccess
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableHtaccess(enableHtaccess);
  }

  /**
   * Sets the <code>enable_indexes</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableIndexes  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableIndexes(boolean)
   */
  public void setHttpdSiteEnableIndexes(
      String siteName,
      String aoServer,
      boolean enableIndexes
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableIndexes(enableIndexes);
  }

  /**
   * Sets the <code>enable_follow_symlinks</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableFollowSymlinks  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableFollowSymlinks(boolean)
   */
  public void setHttpdSiteEnableFollowSymlinks(
      String siteName,
      String aoServer,
      boolean enableFollowSymlinks
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableFollowSymlinks(enableFollowSymlinks);
  }

  /**
   * Sets the <code>enable_anonymous_ftp</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  enableAnonymousFtp  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setEnableAnonymousFtp(boolean)
   */
  public void setHttpdSiteEnableAnonymousFtp(
      String siteName,
      String aoServer,
      boolean enableAnonymousFtp
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setEnableAnonymousFtp(enableAnonymousFtp);
  }

  /**
   * Sets the <code>block_trace_track</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  blockTraceTrack  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setBlockTraceTrack(boolean)
   */
  public void setHttpdSiteBlockTraceTrack(
      String siteName,
      String aoServer,
      boolean blockTraceTrack
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setBlockTraceTrack(blockTraceTrack);
  }

  /**
   * Sets the <code>block_scm</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  blockScm  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setBlockScm(boolean)
   */
  public void setHttpdSiteBlockScm(
      String siteName,
      String aoServer,
      boolean blockScm
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setBlockScm(blockScm);
  }

  /**
   * Sets the <code>block_core_dumps</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  blockCoreDumps  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setBlockCoreDumps(boolean)
   */
  public void setHttpdSiteBlockCoreDumps(
      String siteName,
      String aoServer,
      boolean blockCoreDumps
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setBlockCoreDumps(blockCoreDumps);
  }

  /**
   * Sets the <code>block_editor_backups</code> flag for a {@link Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  blockEditorBackups  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link Site}
   *
   * @see  Site#setBlockEditorBackups(boolean)
   */
  public void setHttpdSiteBlockEditorBackups(
      String siteName,
      String aoServer,
      boolean blockEditorBackups
  ) throws IllegalArgumentException, IOException, SQLException {
    getHttpdSite(aoServer, siteName).setBlockEditorBackups(blockEditorBackups);
  }

  /**
   * Sets the attributes for a {@link Context}.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   */
  public void setHttpdTomcatContextAttributes(
      String siteName,
      String aoServer,
      String oldPath,
      String className,
      boolean cookies,
      boolean crossContext,
      PosixPath docBase,
      boolean override,
      String newPath,
      boolean privileged,
      boolean reloadable,
      boolean useNaming,
      String wrapperClass,
      int debug,
      PosixPath workDir,
      boolean serverXmlConfigured
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    Context htc = hts.getHttpdTomcatContext(oldPath);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + siteName + " on " + aoServer + " path='" + oldPath + '\'');
    }
    htc.setAttributes(
        className,
        cookies,
        crossContext,
        docBase,
        override,
        newPath,
        privileged,
        reloadable,
        useNaming,
        wrapperClass,
        debug,
        workDir,
        serverXmlConfigured
    );
  }

  /**
   * Sets the <code>block_webinf</code> flag for a {@link com.aoindustries.aoserv.client.web.tomcat.Site}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  blockWebinf  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or {@link com.aoindustries.aoserv.client.web.tomcat.Site}.
   *
   * @see  com.aoindustries.aoserv.client.web.tomcat.Site#setBlockWebinf(boolean)
   */
  public void setHttpdTomcatSiteBlockWebinf(
      String siteName,
      String aoServer,
      boolean blockWebinf
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    hts.setBlockWebinf(blockWebinf);
  }

  /**
   * Sets the <code>maxPostSize</code> for a {@link PrivateTomcatSite}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  maxPostSize  the new maximum POST size, in bytes, {@code -1} for none.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or {@link PrivateTomcatSite}
   *
   * @see  PrivateTomcatSite#setMaxPostSize(int)
   */
  public void setHttpdTomcatStdSiteMaxPostSize(
      String siteName,
      String aoServer,
      int maxPostSize
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
    if (htss == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
    }
    htss.setMaxPostSize(maxPostSize);
  }

  /**
   * Sets the <code>unpackWars</code> setting for a {@link PrivateTomcatSite}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  unpackWars  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or {@link PrivateTomcatSite}
   *
   * @see  PrivateTomcatSite#setUnpackWars(boolean)
   */
  public void setHttpdTomcatStdSiteUnpackWars(
      String siteName,
      String aoServer,
      boolean unpackWars
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
    if (htss == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
    }
    htss.setUnpackWars(unpackWars);
  }

  /**
   * Sets the <code>autoDeploy</code> setting for a {@link PrivateTomcatSite}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  autoDeploy  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or {@link PrivateTomcatSite}
   *
   * @see  PrivateTomcatSite#setAutoDeploy(boolean)
   */
  public void setHttpdTomcatStdSiteAutoDeploy(
      String siteName,
      String aoServer,
      boolean autoDeploy
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
    if (htss == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
    }
    htss.setAutoDeploy(autoDeploy);
  }

  /**
   * Sets the <code>tomcatAuthentication</code> setting for a {@link PrivateTomcatSite}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  tomcatAuthentication  the new setting
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site}, or {@link PrivateTomcatSite}
   *
   * @see  PrivateTomcatSite#setTomcatAuthentication(boolean)
   */
  public void setHttpdTomcatStdSiteTomcatAuthentication(
      String siteName,
      String aoServer,
      boolean tomcatAuthentication
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
    if (htss == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
    }
    htss.setTomcatAuthentication(tomcatAuthentication);
  }

  /**
   * Sets the Tomcat version for a {@link PrivateTomcatSite}.
   *
   * @param  siteName  the name of the site
   * @param  aoServer  the hostname of the {@link Server}
   * @param  version  the new version
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @throws IllegalArgumentException if unable to find the {@link Server}, {@link Site}, {@link PrivateTomcatSite}, or {@link com.aoindustries.aoserv.client.web.tomcat.Version}.
   *
   * @see  PrivateTomcatSite#setHttpdTomcatVersion(com.aoindustries.aoserv.client.web.tomcat.Version)
   */
  public void setHttpdTomcatStdSiteVersion(
      String siteName,
      String aoServer,
      String version
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    PrivateTomcatSite htss = hts.getHttpdTomcatStdSite();
    if (htss == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatStdSite: " + siteName + " on " + aoServer);
    }
    htss.setHttpdTomcatVersion(
        findTomcatVersion(hs.getLinuxServer(), version)
    );
  }

  /**
   * Sets the IP address of a DHCP-enabled {@link IpAddress}.
   *
   * @param  ipAddress  the pkey of the {@link IpAddress}
   * @param  dhcpAddress  the new IP address
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link IpAddress} or
   *                                       DHCP address is not valid format
   *
   * @see  IpAddress#setDhcpAddress
   */
  public void setIpAddressDhcpAddress(
      int ipAddress,
      InetAddress dhcpAddress
  ) throws IllegalArgumentException, IOException, SQLException {
    IpAddress ia = connector.getNet().getIpAddress().get(ipAddress);
    if (ia == null) {
      throw new IllegalArgumentException("Unable to find IpAddress: " + ipAddress);
    }
    ia.setDhcpAddress(dhcpAddress);
  }

  /**
   * Sets the hostname of an {@link IpAddress}.
   *
   * @param  ipAddress  the {@link IpAddress} being modified
   * @param  hostname  the new hostname
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link IpAddress} or
   *                                       hostname is not valid format
   *
   * @see  IpAddress#setHostname
   */
  public void setIpAddressHostname(
      InetAddress ipAddress,
      String server,
      String netDevice,
      DomainName hostname
  ) throws IllegalArgumentException, IOException, SQLException {
    getIpAddress(server, netDevice, ipAddress).setHostname(hostname);
  }

  /**
   * Sets the monitoring status of an {@link IpAddress}.
   *
   * @param  ipAddress  the {@link IpAddress} being modified
   * @param  enabled  the new monitoring state
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @throws IllegalArgumentException if unable to find the {@link IpAddress} or
          {@link Package}
   *
   * @see  IpAddress#setPackage
   * @see  #addPackage
   */
  public void setIpAddressMonitoringEnabled(
      InetAddress ipAddress,
      String server,
      String netDevice,
      boolean enabled
  ) throws IllegalArgumentException, IOException, SQLException {
    getIpAddress(server, netDevice, ipAddress).getMonitoring().setEnabled(enabled);
  }

  /**
   * Sets the ownership of an {@link IpAddress}.  The {@link Package} may only be set
   * if the {@link IpAddress} is not being used by any resources.
   *
   * @param  ipAddress  the {@link IpAddress} being modified
   * @param  newPackage  the name of the {@link Package}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   *                           violation occurs
   * @throws IllegalArgumentException if unable to find the {@link IpAddress} or
          {@link Package}
   *
   * @see  IpAddress#setPackage
   * @see  #addPackage
   */
  public void setIpAddressPackage(
      InetAddress ipAddress,
      String server,
      String netDevice,
      Account.Name newPackage
  ) throws IllegalArgumentException, IOException, SQLException {
    getIpAddress(server, netDevice, ipAddress).setPackage(getPackage(newPackage));
  }

  /**
   * Sets the home phone number associated with a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  phone  the new office phone
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setHomePhone
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountHomePhone(
      com.aoindustries.aoserv.client.linux.User.Name username,
      Gecos phone
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setHomePhone(phone);
  }

  /**
   * Sets the full name associated with a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  name  the new full name for the account
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
   *                                       find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setName
   * @see  com.aoindustries.aoserv.client.linux.User.Gecos#validate(java.lang.String)
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountName(
      com.aoindustries.aoserv.client.linux.User.Name username,
      Gecos name
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setName(name);
  }

  /**
   * Sets the office location associated with a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  location  the new office location
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setOfficeLocation
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountOfficeLocation(
      com.aoindustries.aoserv.client.linux.User.Name username,
      Gecos location
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setOfficeLocation(location);
  }

  /**
   * Sets the office phone number associated with a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  phone  the new office phone
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setOfficePhone
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountOfficePhone(
      com.aoindustries.aoserv.client.linux.User.Name username,
      Gecos phone
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setOfficePhone(phone);
  }

  /**
   * Sets the password for a {@link com.aoindustries.aoserv.client.linux.User} by setting the password
   * for each one of its {@link UserServer}s.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setPassword
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountPassword(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setPassword(password);
  }

  /**
   * Sets the shell used by a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   * @param  path  the full path of the shell
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.linux.User} or {@link Shell}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setShell
   * @see  #addLinuxAccount
   */
  public void setLinuxAccountShell(
      com.aoindustries.aoserv.client.linux.User.Name username,
      PosixPath path
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.linux.User la = getLinuxAccount(username);
    Shell sh = connector.getLinux().getShell().get(path);
    if (sh == null) {
      throw new IllegalArgumentException("Unable to find Shell: " + path);
    }
    la.setShell(sh);
  }

  /**
   * Sets the password for a {@link UserServer}.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Host}, {@link Server} or
   *                                       {@link UserServer}
   *
   * @see  UserServer#setPassword
   * @see  #addLinuxServerAccount
   */
  public void setLinuxServerAccountPassword(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setPassword(password);
  }

  /**
   * Sets the number of days junk email is kept.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  days  the new number of days, {@code -1} causes the junk to not be automatically removed
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link UserServer}
   *
   * @see  UserServer#setJunkEmailRetention
   * @see  #addLinuxServerAccount
   */
  public void setLinuxServerAccountJunkEmailRetention(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      int days
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setJunkEmailRetention(days);
  }

  /**
   * Sets the SpamAssassin integration mode for an email account.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  mode      the new mode
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link UserServer}, or {@link SpamAssassinMode}
   *
   * @see  UserServer#setEmailSpamAssassinIntegrationMode
   * @see  #addLinuxServerAccount
   * @see  SpamAssassinMode
   */
  public void setLinuxServerAccountSpamAssassinIntegrationMode(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      String mode
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setEmailSpamAssassinIntegrationMode(getEmailSpamAssassinIntegrationMode(mode));
  }

  /**
   * Sets the SpamAssassin required score for an email account.
   *
   * @param  username        the username of the {@link UserServer}
   * @param  aoServer        the hostname of the {@link Server}
   * @param  requiredScore  the new required score
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link UserServer}
   *
   * @see  UserServer#setSpamAssassinRequiredScore
   * @see  #addLinuxServerAccount
   */
  public void setLinuxServerAccountSpamAssassinRequiredScore(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      float requiredScore
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setSpamAssassinRequiredScore(requiredScore);
  }

  /**
   * Sets the number of days trash email is kept.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  days  the new number of days, {@code -1} causes the trash to not be automatically removed
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link UserServer}
   *
   * @see  UserServer#setTrashEmailRetention
   * @see  #addLinuxServerAccount
   */
  public void setLinuxServerAccountTrashEmailRetention(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      int days
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setTrashEmailRetention(days);
  }

  /**
   * Sets the <code>use_inbox</code> flag on a {@link UserServer}.
   *
   * @param  username  the username of the {@link UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  useInbox  the new flag
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Server} or {@link UserServer}
   *
   * @see  UserServer#setUseInbox(boolean)
   * @see  UserServer#useInbox()
   * @see  #addLinuxServerAccount
   */
  public void setLinuxServerAccountUseInbox(
      com.aoindustries.aoserv.client.linux.User.Name username,
      String aoServer,
      boolean useInbox
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServerAccount(aoServer, username).setUseInbox(useInbox);
  }

  /**
   * Sets the info file for a {@link MajordomoList}.
   *
   * @param  domain  the domain of the {@link MajordomoServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  listName  the name of the new list
   * @param  file  the new file contents
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the name is not valid or unable to find the
   *                                  {@link Server}, {@link Domain},
   *                                  {@link MajordomoServer}, or {@link MajordomoList}
   *
   * @see  MajordomoList#setInfoFile
   * @see  #addMajordomoList
   * @see  #removeEmailList
   */
  public void setMajordomoInfoFile(
      DomainName domain,
      String aoServer,
      String listName,
      String file
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoServer ms = ed.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    MajordomoList ml = ms.getMajordomoList(listName);
    if (ml == null) {
      throw new IllegalArgumentException("Unable to find MajordomoList: " + listName + '@' + domain + " on " + aoServer);
    }
    ml.setInfoFile(file);
  }

  /**
   * Sets the intro file for a {@link MajordomoList}.
   *
   * @param  domain  the domain of the {@link MajordomoServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  listName  the name of the new list
   * @param  file  the new file contents
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data
   *                           integrity violation occurs
   * @exception  IllegalArgumentException  if the name is not valid or unable to find the
   *                                  {@link Server}, {@link Domain},
   *                                  {@link MajordomoServer}, or {@link MajordomoList}
   *
   * @see  MajordomoList#setIntroFile
   * @see  #addMajordomoList
   * @see  #removeEmailList
   */
  public void setMajordomoIntroFile(
      DomainName domain,
      String aoServer,
      String listName,
      String file
  ) throws IllegalArgumentException, IOException, SQLException {
    Domain ed = getEmailDomain(aoServer, domain);
    MajordomoServer ms = ed.getMajordomoServer();
    if (ms == null) {
      throw new IllegalArgumentException("Unable to find MajordomoServer: " + domain + " on " + aoServer);
    }
    MajordomoList ml = ms.getMajordomoList(listName);
    if (ml == null) {
      throw new IllegalArgumentException("Unable to find MajordomoList: " + listName + '@' + domain + " on " + aoServer);
    }
    ml.setIntroFile(file);
  }

  /**
   * Sets the password for a {@link com.aoindustries.aoserv.client.mysql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.UserServer}
   * @param  aoServer  the hostname of the {@link Server}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.mysql.User},
   *                                       {@link Server}, or {@link com.aoindustries.aoserv.client.mysql.UserServer}
   *
   * @see  com.aoindustries.aoserv.client.mysql.UserServer#setPassword
   */
  public void setMysqlServerUserPassword(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer,
      String aoServer,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlServerUser(aoServer, mysqlServer, username).setPassword(password == null || password.length() == 0 ? null : password);
  }

  /**
   * Sets the password for a {@link com.aoindustries.aoserv.client.mysql.User} by settings the password for
   * all of its {@link com.aoindustries.aoserv.client.mysql.UserServer}s.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.mysql.User}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.mysql.User}
   *
   * @see  com.aoindustries.aoserv.client.mysql.User#setPassword
   */
  public void setMysqlUserPassword(
      com.aoindustries.aoserv.client.mysql.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getMysqlUser(username).setPassword(password == null || password.length() == 0 ? null : password);
  }

  /**
   * Sets the firewalld zones enable for a {@link Bind}.
   *
   * @param  pkey  the pkey of the {@link Bind}
   * @param  firewalldZones  the set of enabled zones
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Bind}
   *
   * @see  Bind#setFirewalldZones(java.util.Set)
   */
  public void setNetBindFirewalldZones(
      int pkey,
      Set<FirewallZone.Name> firewalldZones
  ) throws IllegalArgumentException, IOException, SQLException {
    getNetBind(pkey).setFirewalldZones(firewalldZones);
  }

  /**
   * Sets the monitoring status for a {@link Bind}.
   *
   * @param  pkey  the pkey of the {@link Bind}
   * @param  enabled  the new monitoring state
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link Bind}
   *
   * @see  Bind#setMonitoringEnabled
   */
  public void setNetBindMonitoringEnabled(
      int pkey,
      boolean enabled
  ) throws IllegalArgumentException, IOException, SQLException {
    getNetBind(pkey).setMonitoringEnabled(enabled);
  }

  /**
   * Sets the password for a {@link com.aoindustries.aoserv.client.postgresql.UserServer}.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.UserServer}
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the hostname of the {@link Server}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.postgresql.User},
   *                                       {@link Server}, or {@link com.aoindustries.aoserv.client.postgresql.UserServer}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.UserServer#setPassword
   */
  public void setPostgresServerUserPassword(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer,
      String aoServer,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresServerUser(aoServer, postgresServer, username).setPassword(password == null || password.length() == 0 ? null : password);
  }

  /**
   * Sets the password for a {@link com.aoindustries.aoserv.client.postgresql.User} by settings the password for
   * all of its {@link com.aoindustries.aoserv.client.postgresql.UserServer}s.
   *
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.postgresql.User}
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.postgresql.User}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.User#setPassword
   */
  public void setPostgresUserPassword(
      com.aoindustries.aoserv.client.postgresql.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getPostgresUser(username).setPassword(password == null || password.length() == 0 ? null : password);
  }

  /**
   * Sets the primary URL for a {@link VirtualHost}.
   *
   * @param  pkey  the pkey of the {@link VirtualHostName}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualHostName}
   *
   * @see  VirtualHostName#setAsPrimary()
   */
  public void setPrimaryVirtualHostName(
      int pkey
  ) throws IllegalArgumentException, IOException, SQLException {
    VirtualHostName hsu = connector.getWeb().getVirtualHostName().get(pkey);
    if (hsu == null) {
      throw new IllegalArgumentException("Unable to find HttpdSiteURL: " + pkey);
    }
    hsu.setAsPrimary();
  }

  /**
   * Sets the primary group for a {@link com.aoindustries.aoserv.client.linux.User}.
   *
   * @param  groupName  the name of the {@link Group}
   * @param  username  the username of the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if the name is not in a valid format or unable to
   *                                       find the {@link com.aoindustries.aoserv.client.linux.User}
   *
   * @see  com.aoindustries.aoserv.client.linux.User#setPrimaryLinuxGroup
   */
  public void setPrimaryLinuxGroupAccount(
      Group.Name groupName,
      com.aoindustries.aoserv.client.linux.User.Name username
  ) throws IllegalArgumentException, IOException, SQLException {
    getLinuxAccount(username).setPrimaryLinuxGroup(getLinuxGroup(groupName));
  }

  /**
   * Sets the password for a {@link User}.  This password must pass the security
   * checks provided by {@link #checkUsernamePassword(com.aoindustries.aoserv.client.account.User.Name, java.lang.String)}.
   *
   * @param  username  the username
   * @param  password  the new password
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link User}
   *
   * @see  User#setPassword
   * @see  #checkUsernamePassword
   * @see  #addUsername
   */
  public void setUsernamePassword(
      com.aoindustries.aoserv.client.account.User.Name username,
      String password
  ) throws IllegalArgumentException, IOException, SQLException {
    getUsername(username).setPassword(password);
  }

  /**
   * Starts the Apache web server if it is not already running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#startApache
   */
  public void startApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).startApache();
  }

  /**
   * Starts the cron process if it is not already running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#startCron
   */
  public void startCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).startCron();
  }

  /**
   * Starts the distribution on a server and/or changes the setting of the user file scanning.
   *
   * @param  aoServer     the public hostname of the {@link Server} to start the scan on
   * @param  includeUser  the flag indicating whether to include user files
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}
   *
   * @see  Server#startDistro
   */
  public void startDistro(String aoServer, boolean includeUser) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).startDistro(includeUser);
  }

  /**
   * Starts and/or restarts the Tomcat or JBoss Java VM for the provided site.
   *
   * @param  siteName  the name of the site, which is the directory name under <code>/var/www/</code>
   * @param  aoServer    the public hostname of the {@link Server} the site is hosted on
   *
   * @return  an error message if the Java VM cannot currently be restarted or
   *          {@code null} on success
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                       {@link Site}, or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   *
   * @see  com.aoindustries.aoserv.client.web.tomcat.Site#startJvm
   * @see  #addHttpdTomcatStdSite
   */
  public String startJvm(String siteName, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    Site site = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site tomcatSite = site.getHttpdTomcatSite();
    if (tomcatSite == null) {
      throw new IllegalArgumentException("HttpdSite " + siteName + " on " + aoServer + " is not a HttpdTomcatSite");
    }
    return tomcatSite.startJvm();
  }

  /**
   * Starts the MySQL database server if it is not already running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Server#startMysql
   */
  public void startMysql(com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getMysqlServer(aoServer, mysqlServer).startMysql();
  }

  /**
   * Starts the PostgreSQL database server if it is not already running.
   *
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Server#startPostgresql
   */
  public void startPostgresql(com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getPostgresServer(aoServer, postgresServer).startPostgresql();
  }

  /**
   * Starts the X Font Server if it is not already running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#startXfs
   */
  public void startXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).startXfs();
  }

  /**
   * Starts the X Virtual Frame Buffer if it is not already running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#startXvfb
   */
  public void startXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).startXvfb();
  }

  /**
   * Stops the Apache web server if it is running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#stopApache
   */
  public void stopApache(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).stopApache();
  }

  /**
   * Stops the cron daemon if it is running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#stopCron
   */
  public void stopCron(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).stopCron();
  }

  /**
   * Stops the Tomcat or JBoss Java VM for the provided site.
   *
   * @param  siteName  the name of the site, which is the directory name under <code>/var/www/</code>
   * @param  aoServer    the public hostname of the {@link Server} the site is hosted on
   *
   * @return  an error message if the Java VM cannot currently be stopped
   *          {@code null} on success
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server},
   *                                       {@link Site}, or {@link com.aoindustries.aoserv.client.web.tomcat.Site}
   *
   * @see  com.aoindustries.aoserv.client.web.tomcat.Site#stopJvm
   * @see  #addHttpdTomcatStdSite
   */
  public String stopJvm(String siteName, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    Site site = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site tomcatSite = site.getHttpdTomcatSite();
    if (tomcatSite == null) {
      throw new IllegalArgumentException("HttpdSite " + siteName + " on " + aoServer + " is not a HttpdTomcatSite");
    }
    return tomcatSite.stopJvm();
  }

  /**
   * Stops the MySQL database server if it is running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.mysql.Server#stopMysql
   */
  public void stopMysql(com.aoindustries.aoserv.client.mysql.Server.Name mysqlServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getMysqlServer(aoServer, mysqlServer).stopMysql();
  }

  /**
   * Stops the PostgreSQL database server if it is running.
   *
   * @param  postgresServer  the name of the PostgreSQL server
   * @param  aoServer  the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  com.aoindustries.aoserv.client.postgresql.Server#stopPostgresql
   */
  public void stopPostgresql(com.aoindustries.aoserv.client.postgresql.Server.Name postgresServer, String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getPostgresServer(aoServer, postgresServer).stopPostgresql();
  }

  /**
   * Stops the X Font Server if it is running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#stopXfs
   */
  public void stopXfs(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).stopXfs();
  }

  /**
   * Stops the X Virtual Frame Buffer if it is running.
   *
   * @param  aoServer       the public hostname of the {@link Server}
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#stopXvfb
   */
  public void stopXvfb(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).stopXvfb();
  }

  /**
   * Updates a {@link Context} data source.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  {@link com.aoindustries.aoserv.client.web.tomcat.Site} or {@link Context}.
   */
  public void updateHttpdTomcatDataSource(
      String siteName,
      String aoServer,
      String path,
      String oldName,
      String newName,
      String driverClassName,
      String url,
      String username,
      String password,
      int maxActive,
      int maxIdle,
      int maxWait,
      String validationQuery
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    Context htc = hts.getHttpdTomcatContext(path);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + siteName + " on " + aoServer + " path='" + path + '\'');
    }
    ContextDataSource htds = htc.getHttpdTomcatDataSource(oldName);
    if (htds == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatDataSource: " + siteName + " on " + aoServer + " path='" + path + "' name='" + oldName + '\'');
    }
    htds.update(
        newName,
        driverClassName,
        url,
        username,
        password,
        maxActive,
        maxIdle,
        maxWait,
        validationQuery
    );
  }

  /**
   * Updates a {@link Context} parameter.
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Server}, {@link Site},
   *                                  {@link com.aoindustries.aoserv.client.web.tomcat.Site} or {@link Context}.
   */
  public void updateHttpdTomcatParameter(
      String siteName,
      String aoServer,
      String path,
      String oldName,
      String newName,
      String value,
      boolean override,
      String description
  ) throws IllegalArgumentException, IOException, SQLException {
    Site hs = getHttpdSite(aoServer, siteName);
    com.aoindustries.aoserv.client.web.tomcat.Site hts = hs.getHttpdTomcatSite();
    if (hts == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatSite: " + siteName + " on " + aoServer);
    }
    Context htc = hts.getHttpdTomcatContext(path);
    if (htc == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatContext: " + siteName + " on " + aoServer + " path='" + path + '\'');
    }
    ContextParameter htp = htc.getHttpdTomcatParameter(oldName);
    if (htp == null) {
      throw new IllegalArgumentException("Unable to find HttpdTomcatParameter: " + siteName + " on " + aoServer + " path='" + path + "' name='" + oldName + '\'');
    }
    htp.update(
        newName,
        value,
        override,
        description
    );
  }

  /**
   * Waits for any processing or pending updates of the Apache configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForHttpdSiteRebuild
   * @see  #addHttpdTomcatStdSite
   */
  public void waitForHttpdSiteRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForHttpdSiteRebuild();
  }

  /**
   * Waits for any processing or pending updates of the Linux account configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForLinuxAccountRebuild
   */
  public void waitForLinuxAccountRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForLinuxAccountRebuild();
  }

  /**
   * Waits for any processing or pending updates of the MySQL configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForMysqlDatabaseRebuild
   */
  public void waitForMysqlDatabaseRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForMysqlDatabaseRebuild();
  }

  /**
   * Waits for any processing or pending updates of the MySQL configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForMysqlDbUserRebuild
   */
  public void waitForMysqlDbUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForMysqlDbUserRebuild();
  }

  /**
   * Waits for any processing or pending updates of the MySQL server configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForMysqlServerRebuild
   */
  public void waitForMysqlServerRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForMysqlServerRebuild();
  }

  /**
   * Waits for any processing or pending updates of the MySQL configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForMysqlUserRebuild
   */
  public void waitForMysqlUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForMysqlUserRebuild();
  }

  /**
   * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForPostgresDatabaseRebuild
   */
  public void waitForPostgresDatabaseRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForPostgresDatabaseRebuild();
  }

  /**
   * Waits for any processing or pending updates of the PostgreSQL server configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForPostgresServerRebuild
   */
  public void waitForPostgresServerRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForPostgresServerRebuild();
  }

  /**
   * Waits for any processing or pending updates of the PostgreSQL configurations to complete.
   *
   * @param  aoServer  the hostname of the {@link Server} to wait for
   *
   * @exception  IOException  if not able to communicate with the server
   * @exception  SQLException  if not able to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link Server}
   *
   * @see  Server#waitForPostgresUserRebuild
   */
  public void waitForPostgresUserRebuild(String aoServer) throws IllegalArgumentException, IOException, SQLException {
    getLinuxServer(aoServer).waitForPostgresUserRebuild();
  }

  /**
   * @see  Certificate#check(boolean)
   *
   * @param  aoServer  the hostname of the server
   * @param  keyFileOrCertbotName  Either the full path for keyFile or the per-server unique certbot name
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @throws IllegalArgumentException if unable to find the {@link Server} or {@link Certificate}
   */
  public List<Certificate.Check> checkSslCertificate(
      String aoServer,
      String keyFileOrCertbotName,
      boolean allowCached
  ) throws IllegalArgumentException, IOException, SQLException {
    return getSslCertificate(aoServer, keyFileOrCertbotName).check(allowCached);
  }

  /**
   * @see  VirtualServer#create()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String createVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).create();
  }

  /**
   * @see  VirtualServer#reboot()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String rebootVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).reboot();
  }

  /**
   * @see  VirtualServer#shutdown()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String shutdownVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).shutdown();
  }

  /**
   * @see  VirtualServer#destroy()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String destroyVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).destroy();
  }

  /**
   * @see  VirtualServer#pause()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String pauseVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).pause();
  }

  /**
   * @see  VirtualServer#unpause()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String unpauseVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).unpause();
  }

  /**
   * @see  VirtualServer#getStatus()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public int getVirtualServerStatus(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualServer(virtualServer).getStatus();
  }

  /**
   * @see  com.aoindustries.aoserv.client.net.reputation.Set#addReputation(int, com.aoindustries.aoserv.client.net.reputation.Set.ConfidenceType, com.aoindustries.aoserv.client.net.reputation.Set.ReputationType, short)
   *
   * @param  identifier      the unique identifier of the set
   * @param  host            the dotted-quad (A.B.C.D) format IPv4 address
   * @param  confidence      either "uncertain" or "definite"
   * @param  reputationType  either "good" or "bad"
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link com.aoindustries.aoserv.client.net.reputation.Set} or unable to parse parameters
   */
  public void addIpReputation(
      String identifier,
      String host,
      String confidence,
      String reputationType,
      short score
  ) throws IllegalArgumentException, IOException, SQLException {
    com.aoindustries.aoserv.client.net.reputation.Set set = getIpReputationSet(identifier);
    int hostIp = IpAddress.getIntForIpAddress(host);
    set.addReputation(hostIp,
        com.aoindustries.aoserv.client.net.reputation.Set.ConfidenceType.valueOf(confidence.toUpperCase(Locale.ROOT)),
        com.aoindustries.aoserv.client.net.reputation.Set.ReputationType.valueOf(reputationType.toUpperCase(Locale.ROOT)),
        score
    );
  }

  /**
   * Begins a verification of the redundancy of the virtual disk.
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   * @param  device  the device identifier (xvda, xvdb, ...)
   *
   * @return  The time the verification began, which may be in the past if a verification was already in progress
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database or a data integrity
   *                           violation occurs
   * @exception  IllegalArgumentException  if unable to find the {@link VirtualServer} or
   *                                  {@link VirtualDisk}
   *
   * @see  VirtualDisk#verify()
   */
  public long verifyVirtualDisk(
      String virtualServer,
      String device
  ) throws IllegalArgumentException, IOException, SQLException {
    return getVirtualDisk(virtualServer, device).verify();
  }

  /**
   * @see  VirtualServer#getPrimaryPhysicalServer()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String getPrimaryVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return
        getVirtualServer(virtualServer)
            .getPrimaryPhysicalServer()
            .toString();
  }

  /**
   * @see  VirtualServer#getSecondaryPhysicalServer()
   *
   * @param  virtualServer  the pkey, package/name, or hostname of the virtual server
   *
   * @exception  IOException  if unable to contact the server
   * @exception  SQLException  if unable to access the database
   * @exception  IllegalArgumentException  if unable to find the {@link Host} or {@link VirtualServer}
   */
  public String getSecondaryVirtualServer(
      String virtualServer
  ) throws IllegalArgumentException, IOException, SQLException {
    return
        getVirtualServer(virtualServer)
            .getSecondaryPhysicalServer()
            .toString();
  }
}
