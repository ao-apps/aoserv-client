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

package com.aoindustries.aoserv.client.billing;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.backup.BackupReport;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.dns.Zone;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.SendmailServer;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.email.SmtpRelayType;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupType;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>Package</code> is a set of resources and its associated price.
 * An {@link Account} may multiple <code>Package</code>s, each with
 * their own monthly fee and sets of resources such as web sites, databases
 * and users.  Security is not maintained between <code>Package</code>s,
 * only between {@link Account accounts}.  If intra-account security is
 * required, please use child {@link Account accounts}.
 *
 * @see  Account
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class Package extends CachedObjectIntegerKey<Package> implements Disablable, Comparable<Package> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_NAME = 1;
  static final int COLUMN_ACCOUNTING = 2;
  static final int COLUMN_PACKAGE_DEFINITION = 3;
  public static final String COLUMN_NAME_name = "name";

  /**
   * The default inbound email burst before rate limiting.
   */
  public static final int DEFAULT_EMAIL_IN_BURST = 1000;

  /**
   * The default sustained inbound email rate in emails/second.
   */
  public static final float DEFAULT_EMAIL_IN_RATE = 10f;

  /**
   * The default outbound email burst before rate limiting.
   */
  public static final int DEFAULT_EMAIL_OUT_BURST = 200;

  /**
   * The default sustained outbound email rate in emails/second.
   */
  public static final float DEFAULT_EMAIL_OUT_RATE = .2f;

  /**
   * The default relay email burst before rate limiting.
   */
  public static final int DEFAULT_EMAIL_RELAY_BURST = 100;

  /**
   * The default sustained relay email rate in emails/second.
   */
  public static final float DEFAULT_EMAIL_RELAY_RATE = .1f;

  private Account.Name name;
  private Account.Name account;
  private int packageDefinition;
  private UnmodifiableTimestamp created;
  private com.aoindustries.aoserv.client.account.User.Name createdBy;
  private int disableLog;
  private int emailInBurst;
  private float emailInRate;
  private int emailOutBurst;
  private float emailOutRate;
  private int emailRelayBurst;
  private float emailRelayRate;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Package() {
    // Do nothing
  }

  @Override
  public String toStringImpl() {
    return name.toString();
  }

  public void addDnsZone(String zone, InetAddress ip, int ttl) throws IOException, SQLException {
    table.getConnector().getDns().getZone().addDnsZone(this, zone, ip, ttl);
  }

  public int addEmailSmtpRelay(com.aoindustries.aoserv.client.linux.Server aoServer, HostAddress host, SmtpRelayType type, long duration) throws IOException, SQLException {
    return table.getConnector().getEmail().getSmtpRelay().addEmailSmtpRelay(this, aoServer, host, type, duration);
  }

  public void addLinuxGroup(Group.Name name, GroupType type) throws IOException, SQLException {
    addLinuxGroup(name, type.getName());
  }

  public void addLinuxGroup(Group.Name name, String type) throws IOException, SQLException {
    table.getConnector().getLinux().getGroup().addLinuxGroup(name, this, type);
  }

  public void addUsername(com.aoindustries.aoserv.client.account.User.Name username) throws IOException, SQLException {
    table.getConnector().getAccount().getUser().addUsername(this, username);
  }

  @Override
  public boolean canDisable() throws IOException, SQLException {
    // Is already disabled
    if (disableLog != -1) {
      return false;
    }

    // Can only disabled when all dependent objects are already disabled
    for (SharedTomcat hst : getHttpdSharedTomcats()) {
      if (!hst.isDisabled()) {
        return false;
      }
    }
    for (Pipe ep : getEmailPipes()) {
      if (!ep.isDisabled()) {
        return false;
      }
    }
    for (CvsRepository cr : getCvsRepositories()) {
      if (!cr.isDisabled()) {
        return false;
      }
    }
    for (User un : getUsernames()) {
      if (!un.isDisabled()) {
        return false;
      }
    }
    for (Site hs : getHttpdSites()) {
      if (!hs.isDisabled()) {
        return false;
      }
    }
    for (com.aoindustries.aoserv.client.email.List el : getEmailLists()) {
      if (!el.isDisabled()) {
        return false;
      }
    }
    for (SmtpRelay ssr : getEmailSmtpRelays()) {
      if (!ssr.isDisabled()) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getAccount().isDisabled();
    }
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.PACKAGES, dl.getPkey(), name);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.PACKAGES, name);
  }

  public List<BackupReport> getBackupReports() throws IOException, SQLException {
    return table.getConnector().getBackup().getBackupReport().getBackupReports(this);
  }

  public Account.Name getAccount_name() {
    return account;
  }

  public Account getAccount() throws SQLException, IOException {
    Account accountingObject = table.getConnector().getAccount().getAccount().get(account);
    if (accountingObject == null) {
      throw new SQLException("Unable to find Account: " + account);
    }
    return accountingObject;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_NAME:
        return name;
      case COLUMN_ACCOUNTING:
        return account;
      case COLUMN_PACKAGE_DEFINITION:
        return packageDefinition;
      case 4:
        return created;
      case 5:
        return createdBy;
      case 6:
        return getDisableLog_id();
      case 7:
        return emailInBurst == -1 ? null : emailInBurst;
      case 8:
        return Float.isNaN(emailInRate) ? null : emailInRate;
      case 9:
        return emailOutBurst == -1 ? null : emailOutBurst;
      case 10:
        return Float.isNaN(emailOutRate) ? null : emailOutRate;
      case 11:
        return emailRelayBurst == -1 ? null : emailRelayBurst;
      case 12:
        return Float.isNaN(emailRelayRate) ? null : emailRelayRate;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  public Administrator getCreatedBy() throws SQLException, IOException {
    Administrator obj = table.getConnector().getAccount().getUser().get(createdBy).getAdministrator();
    if (obj == null) {
      throw new SQLException("Unable to find Administrator: " + createdBy);
    }
    return obj;
  }

  public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
    return table.getConnector().getScm().getCvsRepository().getCvsRepositories(this);
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  public Integer getDisableLog_id() {
    return disableLog == -1 ? null : disableLog;
  }

  @Override
  public DisableLog getDisableLog() throws SQLException, IOException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  /**
   * Gets the inbound burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getEmailInBurst() {
    return emailInBurst;
  }

  /**
   * Gets the inbound sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getEmailInRate() {
    return emailInRate;
  }

  /**
   * Gets the outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getEmailOutBurst() {
    return emailOutBurst;
  }

  /**
   * Gets the outbound sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getEmailOutRate() {
    return emailOutRate;
  }

  /**
   * Gets the relay burst limit for emails, the number of emails that may be sent before limiting occurs.
   * A value of <code>-1</code> indicates unlimited.
   */
  public int getEmailRelayBurst() {
    return emailRelayBurst;
  }

  /**
   * Gets the relay sustained email rate in emails/second.
   * A value of <code>Float.NaN</code> indicates unlimited.
   */
  public float getEmailRelayRate() {
    return emailRelayRate;
  }

  public List<Zone> getDnsZones() throws IOException, SQLException {
    return table.getConnector().getDns().getZone().getDnsZones(this);
  }

  public List<com.aoindustries.aoserv.client.email.List> getEmailLists() throws IOException, SQLException {
    return table.getConnector().getEmail().getList().getEmailLists(this);
  }

  public List<Pipe> getEmailPipes() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipe().getEmailPipes(this);
  }

  public List<SharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcat().getHttpdSharedTomcats(this);
  }

  public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
    return table.getConnector().getWeb().getHttpdServer().getHttpdServers(this);
  }

  public List<Site> getHttpdSites() throws IOException, SQLException {
    return table.getConnector().getWeb().getSite().getHttpdSites(this);
  }

  public List<IpAddress> getIpAddresses() throws IOException, SQLException {
    return table.getConnector().getNet().getIpAddress().getIpAddresses(this);
  }

  public List<Group> getLinuxGroups() throws IOException, SQLException {
    return table.getConnector().getLinux().getGroup().getLinuxGroups(this);
  }

  public List<com.aoindustries.aoserv.client.mysql.Database> getMysqlDatabases() throws IOException, SQLException {
    return table.getConnector().getMysql().getDatabase().getMysqlDatabases(this);
  }

  public List<MysqlReplication> getFailoverMysqlReplications() throws IOException, SQLException {
    return table.getConnector().getBackup().getMysqlReplication().getFailoverMysqlReplications(this);
  }

  public List<com.aoindustries.aoserv.client.mysql.User> getMysqlUsers() throws IOException, SQLException {
    return table.getConnector().getMysql().getUser().getMysqlUsers(this);
  }

  public Account.Name getName() {
    return name;
  }

  public List<Bind> getNetBinds() throws IOException, SQLException {
    return table.getConnector().getNet().getBind().getNetBinds(this);
  }

  public List<Bind> getNetBinds(IpAddress ip) throws IOException, SQLException {
    return table.getConnector().getNet().getBind().getNetBinds(this, ip);
  }

  public int getPackageDefinition_id() {
    return packageDefinition;
  }

  public PackageDefinition getPackageDefinition() throws SQLException, IOException {
    PackageDefinition pd = table.getConnector().getBilling().getPackageDefinition().get(packageDefinition);
    if (pd == null) {
      throw new SQLException("Unable to find PackageDefinition: " + packageDefinition);
    }
    return pd;
  }

  public List<com.aoindustries.aoserv.client.postgresql.Database> getPostgresDatabases() throws IOException, SQLException {
    return table.getConnector().getPostgresql().getDatabase().getPostgresDatabases(this);
  }

  public List<com.aoindustries.aoserv.client.postgresql.User> getPostgresUsers() throws SQLException, IOException {
    return table.getConnector().getPostgresql().getUser().getPostgresUsers(this);
  }

  public List<SendmailServer> getSendmailServers() throws IOException, SQLException {
    return table.getConnector().getEmail().getSendmailServer().getSendmailServers(this);
  }

  public Host getHost(String name) throws IOException, SQLException {
    return table.getConnector().getNet().getHost().getHost(this, name);
  }

  public List<Host> getServers() throws IOException, SQLException {
    return table.getConnector().getNet().getHost().getServers(this);
  }

  public List<Domain> getEmailDomains() throws IOException, SQLException {
    return table.getConnector().getEmail().getDomain().getEmailDomains(this);
  }

  public List<SmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
    return table.getConnector().getEmail().getSmtpRelay().getEmailSmtpRelays(this);
  }

  public List<Certificate> getSslCertificates() throws IOException, SQLException {
    return table.getConnector().getPki().getCertificate().getSslCertificates(this);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGES;
  }

  public List<User> getUsernames() throws IOException, SQLException {
    return table.getConnector().getAccount().getUser().getUsernames(this);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      name = Account.Name.valueOf(result.getString(pos++));
      account = Account.Name.valueOf(result.getString(pos++));
      packageDefinition = result.getInt(pos++);
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      createdBy = com.aoindustries.aoserv.client.account.User.Name.valueOf(result.getString(pos++));
      disableLog = result.getInt(pos++);
      if (result.wasNull()) {
        disableLog = -1;
      }
      emailInBurst = result.getInt(pos++);
      if (result.wasNull()) {
        emailInBurst = -1;
      }
      emailInRate = result.getFloat(pos++);
      if (result.wasNull()) {
        emailInRate = Float.NaN;
      }
      emailOutBurst = result.getInt(pos++);
      if (result.wasNull()) {
        emailOutBurst = -1;
      }
      emailOutRate = result.getFloat(pos++);
      if (result.wasNull()) {
        emailOutRate = Float.NaN;
      }
      emailRelayBurst = result.getInt(pos++);
      if (result.wasNull()) {
        emailRelayBurst = -1;
      }
      emailRelayRate = result.getFloat(pos++);
      if (result.wasNull()) {
        emailRelayRate = Float.NaN;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      name = Account.Name.valueOf(in.readUTF()).intern();
      account = Account.Name.valueOf(in.readUTF()).intern();
      packageDefinition = in.readCompressedInt();
      created = SQLStreamables.readUnmodifiableTimestamp(in);
      createdBy = com.aoindustries.aoserv.client.account.User.Name.valueOf(in.readUTF()).intern();
      disableLog = in.readCompressedInt();
      emailInBurst = in.readCompressedInt();
      emailInRate = in.readFloat();
      emailOutBurst = in.readCompressedInt();
      emailOutRate = in.readFloat();
      emailRelayBurst = in.readCompressedInt();
      emailRelayRate = in.readFloat();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name.toString());
    out.writeUTF(account.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122) <= 0) {
      out.writeUTF("unknown");
      out.writeCompressedInt(0);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_123) >= 0) {
      out.writeCompressedInt(packageDefinition);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(created.getTime());
    } else {
      SQLStreamables.writeTimestamp(created, out);
    }
    out.writeUTF(createdBy.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122) <= 0) {
      out.writeCompressedInt(-1);
      out.writeCompressedInt(200);
      out.writeCompressedInt(-1);
      out.writeCompressedInt(100);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeCompressedInt(256);
      out.writeLong(64L * 1024 * 1024);
      out.writeCompressedInt(256);
      out.writeLong(64L * 1024 * 1024);
    }
    out.writeCompressedInt(disableLog);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_24) >= 0) {
      out.writeCompressedInt(emailInBurst);
      out.writeFloat(emailInRate);
      out.writeCompressedInt(emailOutBurst);
      out.writeFloat(emailOutRate);
      out.writeCompressedInt(emailRelayBurst);
      out.writeFloat(emailRelayRate);
    }
  }

  @Override
  public int compareTo(Package o) {
    return name.compareTo(o.name);
  }
}
