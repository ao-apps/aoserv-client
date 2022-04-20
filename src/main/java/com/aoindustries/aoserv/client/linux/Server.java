/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.linux;

import com.aoapps.collections.AoCollections;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Strings;
import com.aoapps.lang.dto.DtoFactory;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.BufferManager;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Email;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoapps.net.URIParameters;
import com.aoapps.security.HashedKey;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.backup.BackupPartition;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.email.Address;
import com.aoindustries.aoserv.client.email.BlackholeAddress;
import com.aoindustries.aoserv.client.email.CyrusImapdServer;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Forwarding;
import com.aoindustries.aoserv.client.email.InboxAddress;
import com.aoindustries.aoserv.client.email.ListAddress;
import com.aoindustries.aoserv.client.email.MajordomoServer;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.PipeAddress;
import com.aoindustries.aoserv.client.email.SendmailServer;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.email.SystemAlias;
import com.aoindustries.aoserv.client.ftp.GuestUser;
import com.aoindustries.aoserv.client.ftp.PrivateServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.DeviceId;
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
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A {@link Server} stores the details about a server that runs the AOServ distribution.
 *
 * @author  AO Industries, Inc.
 */
public final class Server
  extends CachedObjectIntegerKey<Server>
  implements DtoFactory<com.aoindustries.aoserv.client.dto.LinuxServer>
{

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Server.class);

  static final int COLUMN_SERVER = 0;
  static final int COLUMN_HOSTNAME = 1;
  public static final String COLUMN_HOSTNAME_name = "hostname";

  private DomainName hostname;
  private int daemon_bind;
  private int pool_size;
  private int distro_hour;
  private UnmodifiableTimestamp last_distro_time;
  private int failover_server;
  private String daemonDeviceId;
  private int daemon_connect_bind;
  private String time_zone;
  private int jilter_bind;
  private boolean restrict_outbound_email;
  private HostAddress daemon_connect_address;
  private int failover_batch_size;
  private float monitoring_load_low;
  private float monitoring_load_medium;
  private float monitoring_load_high;
  private float monitoring_load_critical;
  private LinuxId uidMin;
  private LinuxId gidMin;
  private LinuxId uidMax;
  private LinuxId gidMax;
  private LinuxId lastUid;
  private LinuxId lastGid;
  private long sftp_umask;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public Server() {
    // Do nothing
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_SERVER: return pkey;
      case COLUMN_HOSTNAME: return hostname;
      case 2: return daemon_bind == -1?null:daemon_bind;
      case 3: return pool_size;
      case 4: return distro_hour;
      case 5: return last_distro_time;
      case 6: return failover_server == -1?null:failover_server;
      case 7: return daemonDeviceId;
      case 8: return daemon_connect_bind == -1?null:daemon_connect_bind;
      case 9: return time_zone;
      case 10: return jilter_bind == -1 ? null : jilter_bind;
      case 11: return restrict_outbound_email;
      case 12: return daemon_connect_address;
      case 13: return failover_batch_size;
      case 14: return Float.isNaN(monitoring_load_low) ? null : monitoring_load_low;
      case 15: return Float.isNaN(monitoring_load_medium) ? null : monitoring_load_medium;
      case 16: return Float.isNaN(monitoring_load_high) ? null : monitoring_load_high;
      case 17: return Float.isNaN(monitoring_load_critical) ? null : monitoring_load_critical;
      case 18: return uidMin;
      case 19: return gidMin;
      case 20: return uidMax;
      case 21: return gidMax;
      case 22: return lastUid;
      case 23: return lastGid;
      case 24: return sftp_umask == -1 ? null : sftp_umask;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getServer_pkey() {
    return pkey;
  }

  public Host getHost() throws SQLException, IOException {
    Host se = table.getConnector().getNet().getHost().get(pkey);
    if (se == null) {
      throw new SQLException("Unable to find Host: " + pkey);
    }
    return se;
  }

  /**
   * Gets the unique, fully-qualified hostname for this server.  Should be resolvable in DNS to ease maintenance.
   */
  public DomainName getHostname() {
    return hostname;
  }

  public Integer getDaemonBind_id() {
    return daemon_bind == -1 ? null : daemon_bind;
  }

  /**
   * Gets the port information to bind to.
   */
  public Bind getDaemonBind() throws IOException, SQLException {
    if (daemon_bind == -1) {
      return null;
    }
    // May be filtered
    return table.getConnector().getNet().getBind().get(daemon_bind);
  }

  public int getPoolSize() {
    return pool_size;
  }

  public int getDistroHour() {
    return distro_hour;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getLastDistroTime() {
    return last_distro_time;
  }

  public Integer getFailoverServer_server_pkey() {
    return failover_server == -1 ? null : failover_server;
  }

  public Server getFailoverServer() throws SQLException, IOException {
    if (failover_server == -1) {
      return null;
    }
    Server se=table.getConnector().getLinux().getServer().get(failover_server);
    if (se == null) {
      throw new SQLException("Unable to find linux.Server: "+failover_server);
    }
    return se;
  }

  public String getDaemonDeviceId_name() {
    return daemonDeviceId;
  }

  public DeviceId getDaemonDeviceId() throws SQLException, IOException {
    DeviceId obj = table.getConnector().getNet().getDeviceId().get(daemonDeviceId);
    if (obj == null) {
      throw new SQLException("Unable to find NetDeviceID: " + daemonDeviceId);
    }
    return obj;
  }

  public Integer getDaemonConnectBind_id() {
    return daemon_connect_bind == -1 ? null : daemon_connect_bind;
  }

  /**
   * Gets the port information to connect to.
   */
  public Bind getDaemonConnectBind() throws IOException, SQLException {
    if (daemon_connect_bind == -1) {
      return null;
    }
    // May be filtered
    return table.getConnector().getNet().getBind().get(daemon_connect_bind);
  }

  public String getTimeZone_name() {
    return time_zone;
  }

  public TimeZone getTimeZone() throws SQLException, IOException {
    TimeZone obj = table.getConnector().getLinux().getTimeZone().get(time_zone);
    if (obj == null) {
      throw new SQLException("Unable to find TimeZone: " + time_zone);
    }
    return obj;
  }

  public Integer getJilterBind_id() {
    return jilter_bind == -1 ? null : jilter_bind;
  }

  public Bind getJilterBind() throws IOException, SQLException {
    if (jilter_bind == -1) {
      return null;
    }
    // May be filtered
    return table.getConnector().getNet().getBind().get(jilter_bind);
  }

  public boolean getRestrictOutboundEmail() {
    return restrict_outbound_email;
  }

  /**
   * Gets the address that should be connected to in order to reach this server.
   * This overrides both getDaemonConnectBind and getDaemonBind.
   *
   * @see  #getDaemonConnectBind
   * @see  #getDaemonBind
   */
  public HostAddress getDaemonConnectAddress() {
    return daemon_connect_address;
  }

  /**
   * Gets the number of filesystem entries sent per batch during failover replications.
   */
  public int getFailoverBatchSize() {
    return failover_batch_size;
  }

  /**
   * Gets the 5-minute load average that is considered a low-priority alert or
   * <code>NaN</code> if no alert allowed at this level.
   */
  public float getMonitoringLoadLow() {
    return monitoring_load_low;
  }

  /**
   * Gets the 5-minute load average that is considered a medium-priority alert or
   * <code>NaN</code> if no alert allowed at this level.
   */
  public float getMonitoringLoadMedium() {
    return monitoring_load_medium;
  }

  /**
   * Gets the 5-minute load average that is considered a high-priority alert or
   * <code>NaN</code> if no alert allowed at this level.
   */
  public float getMonitoringLoadHigh() {
    return monitoring_load_high;
  }

  /**
   * Gets the 5-minute load average that is considered a critical-priority alert or
   * <code>NaN</code> if no alert allowed at this level.  This is the level
   * that will alert people 24x7.
   */
  public float getMonitoringLoadCritical() {
    return monitoring_load_critical;
  }

  /**
   * Gets the min value for automatic uid selection in useradd.
   */
  public LinuxId getUidMin() {
    return uidMin;
  }

  /**
   * Gets the min value for automatic gid selection in groupadd.
   */
  public LinuxId getGidMin() {
    return gidMin;
  }

  /**
   * Gets the max value for automatic uid selection in useradd.
   */
  public LinuxId getUidMax() {
    return uidMax;
  }

  /**
   * Gets the max value for automatic gid selection in groupadd.
   */
  public LinuxId getGidMax() {
    return gidMax;
  }

  /**
   * Gets the last value for automatic uid selection in useradd, if any.
   */
  public LinuxId getLastUid() {
    return lastUid;
  }

  /**
   * Gets the last value for automatic gid selection in groupadd, if any.
   */
  public LinuxId getLastGid() {
    return lastGid;
  }

  /**
   * Gets the optional umask for the sftp-server or <code>-1</code> for none.
   */
  public long getSftpUmask() {
    return sftp_umask;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt("server");
      hostname = DomainName.valueOf(result.getString("hostname"));
      daemon_bind = result.getInt("daemon_bind");
      if (result.wasNull()) {
        daemon_bind = -1;
      }
      pool_size = result.getInt("pool_size");
      distro_hour = result.getInt("distro_hour");
      last_distro_time = UnmodifiableTimestamp.valueOf(result.getTimestamp("last_distro_time"));
      failover_server = result.getInt("failover_server");
      if (result.wasNull()) {
        failover_server = -1;
      }
      daemonDeviceId = result.getString("daemonDeviceId");
      daemon_connect_bind = result.getInt("daemon_connect_bind");
      time_zone = result.getString("time_zone");
      jilter_bind = result.getInt("jilter_bind");
      if (result.wasNull()) {
        jilter_bind = -1;
      }
      restrict_outbound_email = result.getBoolean("restrict_outbound_email");
      daemon_connect_address = HostAddress.valueOf(result.getString("daemon_connect_address"));
      failover_batch_size = result.getInt("failover_batch_size");
      monitoring_load_low = result.getFloat("monitoring_load_low");
      if (result.wasNull()) {
        monitoring_load_low = Float.NaN;
      }
      monitoring_load_medium = result.getFloat("monitoring_load_medium");
      if (result.wasNull()) {
        monitoring_load_medium = Float.NaN;
      }
      monitoring_load_high = result.getFloat("monitoring_load_high");
      if (result.wasNull()) {
        monitoring_load_high = Float.NaN;
      }
      monitoring_load_critical = result.getFloat("monitoring_load_critical");
      if (result.wasNull()) {
        monitoring_load_critical = Float.NaN;
      }
      uidMin = LinuxId.valueOf(result.getInt("uidMin"));
      gidMin = LinuxId.valueOf(result.getInt("gidMin"));
      uidMax = LinuxId.valueOf(result.getInt("uidMax"));
      gidMax = LinuxId.valueOf(result.getInt("gidMax"));
      int lastUidInt = result.getInt("lastUid");
      lastUid = result.wasNull() ? null : LinuxId.valueOf(lastUidInt);
      int lastGidInt = result.getInt("lastGid");
      lastGid = result.wasNull() ? null : LinuxId.valueOf(lastGidInt);
      sftp_umask = result.getLong("sftp_umask");
      if (result.wasNull()) {
        sftp_umask = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      hostname = DomainName.valueOf(in.readUTF());
      daemon_bind = in.readCompressedInt();
      pool_size = in.readCompressedInt();
      distro_hour = in.readCompressedInt();
      last_distro_time = SQLStreamables.readNullUnmodifiableTimestamp(in);
      failover_server = in.readCompressedInt();
      daemonDeviceId = InternUtils.intern(in.readNullUTF());
      daemon_connect_bind = in.readCompressedInt();
      time_zone = in.readUTF().intern();
      jilter_bind = in.readCompressedInt();
      restrict_outbound_email = in.readBoolean();
      daemon_connect_address = InternUtils.intern(HostAddress.valueOf(in.readNullUTF()));
      failover_batch_size = in.readCompressedInt();
      monitoring_load_low = in.readFloat();
      monitoring_load_medium = in.readFloat();
      monitoring_load_high = in.readFloat();
      monitoring_load_critical = in.readFloat();
      uidMin = LinuxId.valueOf(in.readCompressedInt());
      gidMin = LinuxId.valueOf(in.readCompressedInt());
      uidMax = LinuxId.valueOf(in.readCompressedInt());
      gidMax = LinuxId.valueOf(in.readCompressedInt());
      int lastUidInt = in.readCompressedInt();
      lastUid = lastUidInt == -1 ? null : LinuxId.valueOf(lastUidInt);
      int lastGidInt = in.readCompressedInt();
      lastGid = lastGidInt == -1 ? null : LinuxId.valueOf(lastGidInt);
      sftp_umask = in.readLong();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeCompressedInt(1);
      out.writeCompressedInt(2000);
      out.writeCompressedInt(1024);
      out.writeCompressedInt(2);
      out.writeCompressedInt(240);
      out.writeNullUTF(null);
      out.writeBoolean(false);
      out.writeBoolean(false);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)<0) {
      out.writeBoolean(true);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeBoolean(false);
      out.writeUTF("linux.Server #"+pkey);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31) >= 0) {
      out.writeUTF(hostname.toString());
    }
    out.writeCompressedInt(daemon_bind);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_2) <= 0) {
      out.writeUTF(HashedKey.NO_KEY_VALUE);
    }
    out.writeCompressedInt(pool_size);
    out.writeCompressedInt(distro_hour);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(last_distro_time == null ? -1 : last_distro_time.getTime());
    } else {
      SQLStreamables.writeNullTimestamp(last_distro_time, out);
    }
    out.writeCompressedInt(failover_server);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeCompressedInt(60 * 1000);
      out.writeCompressedInt(5 * 60 * 1000);
      out.writeBoolean(false);
    }
    out.writeNullUTF(daemonDeviceId);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeNullUTF(null);
      out.writeCompressedInt(1200*100);
      out.writeBoolean(true);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_108) >= 0) {
        out.writeNullUTF(null);
        out.writeNullUTF(null);
      } else if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_104) >= 0) {
        out.writeUTF(AoservProtocol.FILTERED);
        out.writeUTF(AoservProtocol.FILTERED);
      }
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_119) >= 0) {
      out.writeCompressedInt(daemon_connect_bind);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_2) >= 0) {
      out.writeUTF(time_zone);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_7) >= 0) {
      out.writeCompressedInt(jilter_bind);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_8) >= 0) {
      out.writeBoolean(restrict_outbound_email);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_11) >= 0) {
      out.writeNullUTF(Objects.toString(daemon_connect_address, null));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_12) >= 0) {
      out.writeCompressedInt(failover_batch_size);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_35) >= 0) {
      out.writeFloat(monitoring_load_low);
      out.writeFloat(monitoring_load_medium);
      out.writeFloat(monitoring_load_high);
      out.writeFloat(monitoring_load_critical);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80) >= 0) {
      out.writeCompressedInt(uidMin.getId());
      out.writeCompressedInt(gidMin.getId());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_18) >= 0) {
      out.writeCompressedInt(uidMax.getId());
      out.writeCompressedInt(gidMax.getId());
      out.writeCompressedInt(lastUid == null ? -1 : lastUid.getId());
      out.writeCompressedInt(lastGid == null ? -1 : lastGid.getId());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_5) >= 0) {
      out.writeLong(sftp_umask);
    }
  }

  public int addCvsRepository(
    PosixPath path,
    UserServer lsa,
    GroupServer lsg,
    long mode
  ) throws IOException, SQLException {
    return table.getConnector().getScm().getCvsRepository().addCvsRepository(
      this,
      path,
      lsa,
      lsg,
      mode
    );
  }

  public int addEmailDomain(DomainName domain, Package packageObject) throws SQLException, IOException {
    return table.getConnector().getEmail().getDomain().addEmailDomain(domain, this, packageObject);
  }

  public int addEmailPipe(String command, Package packageObject) throws IOException, SQLException {
    return table.getConnector().getEmail().getPipe().addEmailPipe(this, command, packageObject);
  }

  public int addHttpdJBossSite(
    String siteName,
    Package packageObj,
    User siteUser,
    Group siteGroup,
    Email serverAdmin,
    boolean useApache,
    IpAddress ipAddress,
    DomainName primaryHttpHostname,
    DomainName[] altHttpHostnames,
    com.aoindustries.aoserv.client.web.jboss.Version jBossVersion
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_jboss().getSite().addHttpdJBossSite(
      this,
      siteName,
      packageObj,
      siteUser,
      siteGroup,
      serverAdmin,
      useApache,
      ipAddress,
      primaryHttpHostname,
      altHttpHostnames,
      jBossVersion
    );
  }

  public int addHttpdSharedTomcat(
    String name,
    com.aoindustries.aoserv.client.web.tomcat.Version version,
    UserServer lsa,
    GroupServer lsg
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcat().addHttpdSharedTomcat(
      name,
      this,
      version,
      lsa,
      lsg
    );
  }

  public int addHttpdTomcatSharedSite(
    String siteName,
    Package packageObj,
    User siteUser,
    Group siteGroup,
    Email serverAdmin,
    boolean useApache,
    IpAddress ipAddress,
    DomainName primaryHttpHostname,
    DomainName[] altHttpHostnames,
    String sharedTomcatName
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcatSite().addHttpdTomcatSharedSite(
      this,
      siteName,
      packageObj,
      siteUser,
      siteGroup,
      serverAdmin,
      useApache,
      ipAddress,
      primaryHttpHostname,
      altHttpHostnames,
      sharedTomcatName
    );
  }

  public int addHttpdTomcatStdSite(
    String siteName,
    Package packageObj,
    User jvmUser,
    Group jvmGroup,
    Email serverAdmin,
    boolean useApache,
    IpAddress ipAddress,
    DomainName primaryHttpHostname,
    DomainName[] altHttpHostnames,
    com.aoindustries.aoserv.client.web.tomcat.Version tomcatVersion
  ) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getPrivateTomcatSite().addHttpdTomcatStdSite(
      this,
      siteName,
      packageObj,
      jvmUser,
      jvmGroup,
      serverAdmin,
      useApache,
      ipAddress,
      primaryHttpHostname,
      altHttpHostnames,
      tomcatVersion
    );
  }

  /**
   * Adds a new system group.  This is for the AOServ Daemon to register newly
   * installed local system groups, such as those added through routine RPM
   * installation.  The master will check that the requested group matches
   * expected settings.
   */
  public int addSystemGroup(Group.Name groupName, int gid) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().addSystemGroup(
      this,
      groupName,
      gid
    );
  }

  /**
   * Adds a new system user.  This is for the AOServ Daemon to register newly
   * installed local system users, such as those added through routine RPM
   * installation.  The master will check that the requested user matches
   * expected settings.
   */
  public int addSystemUser(
    User.Name username,
    int uid,
    int gid,
    User.Gecos fullName,
    User.Gecos officeLocation,
    User.Gecos officePhone,
    User.Gecos homePhone,
    PosixPath home,
    PosixPath shell
  ) throws IOException, SQLException {
    return table.getConnector().getLinux().getUserServer().addSystemUser(
      this,
      username,
      uid,
      gid,
      fullName,
      officeLocation,
      officePhone,
      homePhone,
      home,
      shell
    );
  }

  public List<DaemonAcl> getAOServerDaemonHosts() throws IOException, SQLException {
    return table.getConnector().getLinux().getDaemonAcl().getAOServerDaemonHosts(this);
  }

  public List<BackupPartition> getBackupPartitions() throws IOException, SQLException {
    return table.getConnector().getBackup().getBackupPartition().getBackupPartitions(this);
  }

  public BackupPartition getBackupPartitionForPath(String path) throws IOException, SQLException {
    return table.getConnector().getBackup().getBackupPartition().getBackupPartitionForPath(this, path);
  }

  public List<BlackholeAddress> getBlackholeEmailAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getBlackholeAddress().getBlackholeEmailAddresses(this);
  }

  public CvsRepository getCvsRepository(PosixPath path) throws IOException, SQLException {
    return table.getConnector().getScm().getCvsRepository().getCvsRepository(this, path);
  }

  public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
    return table.getConnector().getScm().getCvsRepository().getCvsRepositories(this);
  }

  public IpAddress getDaemonIPAddress() throws SQLException, IOException {
    Bind nb=getDaemonBind();
    if (nb == null) {
      throw new SQLException("Unable to find daemon NetBind for linux.Server: "+pkey);
    }
    IpAddress ia=nb.getIpAddress();
    InetAddress ip=ia.getInetAddress();
    if (ip.isUnspecified()) {
      DeviceId ndi = getDaemonDeviceId();
      Device nd=getHost().getNetDevice(ndi.getName());
      if (nd == null) {
        throw new SQLException("Unable to find NetDevice: "+ndi.getName()+" on "+pkey);
      }
      ia=nd.getPrimaryIPAddress();
      if (ia == null) {
        throw new SQLException("Unable to find primary IPAddress: "+ndi.getName()+" on "+pkey);
      }
    }
    return ia;
  }

  public CyrusImapdServer getCyrusImapdServer() throws IOException, SQLException {
    return table.getConnector().getEmail().getCyrusImapdServer().get(pkey);
  }

  public List<Address> getEmailAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getAddress().getEmailAddresses(this);
  }

  public Domain getEmailDomain(DomainName domain) throws IOException, SQLException {
    return table.getConnector().getEmail().getDomain().getEmailDomain(this, domain);
  }

  public List<Domain> getEmailDomains() throws IOException, SQLException {
    return table.getConnector().getEmail().getDomain().getEmailDomains(this);
  }

  public List<Forwarding> getEmailForwarding() throws SQLException, IOException {
    return table.getConnector().getEmail().getForwarding().getEmailForwarding(this);
  }

  /**
   * Rename to getEmailList when all uses updated.
   */
  public com.aoindustries.aoserv.client.email.List getEmailList(PosixPath path) throws IOException, SQLException {
    return table.getConnector().getEmail().getList().getEmailList(this, path);
  }

  public List<ListAddress> getEmailListAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getListAddress().getEmailListAddresses(this);
  }

  public List<PipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().getEmailPipeAddresses(this);
  }

  public List<Pipe> getEmailPipes() throws IOException, SQLException {
    return table.getConnector().getEmail().getPipe().getEmailPipes(this);
  }

  public SmtpRelay getEmailSmtpRelay(Package pk, HostAddress host) throws IOException, SQLException {
    return table.getConnector().getEmail().getSmtpRelay().getEmailSmtpRelay(pk, this, host);
  }

  /**
   * Gets all of the smtp relays settings that apply to either all servers or this server specifically.
   */
  public List<SmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
    return table.getConnector().getEmail().getSmtpRelay().getEmailSmtpRelays(this);
  }

  public List<GuestUser> getFTPGuestUsers() throws IOException, SQLException {
    return table.getConnector().getFtp().getGuestUser().getFTPGuestUsers(this);
  }

  public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
    return table.getConnector().getWeb().getHttpdServer().getHttpdServers(this);
  }

  public List<SharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcat().getHttpdSharedTomcats(this);
  }

  public SharedTomcat getHttpdSharedTomcat(String jvmName) throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcat().getHttpdSharedTomcat(jvmName, this);
  }

  public Site getHttpdSite(String siteName) throws IOException, SQLException {
    return table.getConnector().getWeb().getSite().getHttpdSite(siteName, this);
  }

  public List<Site> getHttpdSites() throws IOException, SQLException {
    return table.getConnector().getWeb().getSite().getHttpdSites(this);
  }

  public List<InboxAddress> getLinuxAccAddresses() throws IOException, SQLException {
    return table.getConnector().getEmail().getInboxAddress().getLinuxAccAddresses(this);
  }

  public List<User> getLinuxAccounts() throws SQLException, IOException {
    List<UserServer> lsa=getLinuxServerAccounts();
    int len=lsa.size();
    List<User> la=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      la.add(lsa.get(c).getLinuxAccount());
    }
    return la;
  }

  public List<Group> getLinuxGroups() throws SQLException, IOException {
    List<GroupServer> lsg=getLinuxServerGroups();
    int len=lsg.size();
    List<Group> lg=new ArrayList<>(len);
    for (int c = 0; c < len; c++) {
      lg.add(lsg.get(c).getLinuxGroup());
    }
    return lg;
  }

  public UserServer getLinuxServerAccount(User.Name username) throws IOException, SQLException {
    return table.getConnector().getLinux().getUserServer().getLinuxServerAccount(this, username);
  }

  public UserServer getLinuxServerAccount(LinuxId uid) throws IOException, SQLException {
    return table.getConnector().getLinux().getUserServer().getLinuxServerAccount(this, uid);
  }

  public List<UserServer> getLinuxServerAccounts() throws IOException, SQLException {
    return table.getConnector().getLinux().getUserServer().getLinuxServerAccounts(this);
  }

  public GroupServer getLinuxServerGroup(LinuxId gid) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().getLinuxServerGroup(this, gid);
  }

  public GroupServer getLinuxServerGroup(Group.Name groupName) throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().getLinuxServerGroup(this, groupName);
  }

  public List<GroupServer> getLinuxServerGroups() throws IOException, SQLException {
    return table.getConnector().getLinux().getGroupServer().getLinuxServerGroups(this);
  }

  public List<MajordomoServer> getMajordomoServers() throws IOException, SQLException {
    return table.getConnector().getEmail().getMajordomoServer().getMajordomoServers(this);
  }

  private static final Map<Integer, Object> mrtgLocks = new HashMap<>();

  public void getMrtgFile(final String filename, final OutputStream out) throws IOException, SQLException {
    // Only one MRTG graph per server at a time, if don't get the lock in 15 seconds, return an error
    synchronized (mrtgLocks) {
      long startTime = System.currentTimeMillis();
      do {
        if (mrtgLocks.containsKey(pkey)) {
          long currentTime = System.currentTimeMillis();
          if (startTime > currentTime) {
            startTime = currentTime;
          } else if ((currentTime - startTime) >= 15000) {
            throw new IOException("15 second timeout reached while trying to get lock to access server #"+pkey);
          } else {
            try {
              mrtgLocks.wait(startTime + 15000 - currentTime);
            } catch (InterruptedException err) {
              // Restore the interrupted status
              Thread.currentThread().interrupt();
              InterruptedIOException ioErr = new InterruptedIOException();
              ioErr.initCause(err);
              throw ioErr;
            }
          }
        }
      } while (mrtgLocks.containsKey(pkey));
      mrtgLocks.put(pkey, Boolean.TRUE);
      mrtgLocks.notifyAll();
    }

    try {
      table.getConnector().requestUpdate(
        false,
        AoservProtocol.CommandID.GET_MRTG_FILE,
        new AOServConnector.UpdateRequest() {
          @Override
          public void writeRequest(StreamableOutput masterOut) throws IOException {
            masterOut.writeCompressedInt(pkey);
            masterOut.writeUTF(filename);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            byte[] buff=BufferManager.getBytes();
            try {
              int code;
              while ((code=in.readByte()) == AoservProtocol.NEXT) {
                int len=in.readShort();
                in.readFully(buff, 0, len);
                out.write(buff, 0, len);
              }
              AoservProtocol.checkResult(code, in);
            } finally {
              BufferManager.release(buff, false);
            }
          }

          @Override
          public void afterRelease() {
            // Do nothing
          }
        }
      );
    } finally {
      synchronized (mrtgLocks) {
        mrtgLocks.remove(pkey);
        mrtgLocks.notifyAll();
      }
    }
  }

  public com.aoindustries.aoserv.client.mysql.Server getMySQLServer(com.aoindustries.aoserv.client.mysql.Server.Name name) throws IOException, SQLException {
    return table.getConnector().getMysql().getServer().getMySQLServer(name, this);
  }

  public List<com.aoindustries.aoserv.client.mysql.Server> getMySQLServers() throws IOException, SQLException {
    return table.getConnector().getMysql().getServer().getMySQLServers(this);
  }

  public com.aoindustries.aoserv.client.mysql.Server getPreferredMySQLServer() throws IOException, SQLException {
    // Look for the most-preferred version that has an instance on the server
    List<com.aoindustries.aoserv.client.mysql.Server> pss=getMySQLServers();
    for (String versionPrefix : com.aoindustries.aoserv.client.mysql.Server.PREFERRED_VERSION_PREFIXES) {
      for (com.aoindustries.aoserv.client.mysql.Server ps : pss) {
        if (ps.getVersion().getVersion().startsWith(versionPrefix)) {
          return ps;
        }
      }
    }

    // Default to first available server if no preferred ones round
    return pss.isEmpty()?null:pss.get(0);
  }

  public List<Server> getNestedServers() throws IOException, SQLException {
    return table.getConnector().getLinux().getServer().getNestedServers(this);
  }

  public com.aoindustries.aoserv.client.postgresql.Server getPostgresServer(com.aoindustries.aoserv.client.postgresql.Server.Name name) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getServer().getPostgresServer(name, this);
  }

  public List<com.aoindustries.aoserv.client.postgresql.Server> getPostgresServers() throws IOException, SQLException {
    return table.getConnector().getPostgresql().getServer().getPostgresServers(this);
  }

  public com.aoindustries.aoserv.client.postgresql.Server getPreferredPostgresServer() throws SQLException, IOException {
    // Look for the most-preferred version that has an instance on the server
    List<com.aoindustries.aoserv.client.postgresql.Server> pss=getPostgresServers();
    String[] preferredMinorVersions = com.aoindustries.aoserv.client.postgresql.Version.getPreferredMinorVersions();
    for (String version : preferredMinorVersions) {
      for (com.aoindustries.aoserv.client.postgresql.Server ps : pss) {
        if (ps.getVersion().getMinorVersion().equals(version)) {
          return ps;
        }
      }
    }

    // Default to first available server if no preferred ones round
    return pss.isEmpty()?null:pss.get(0);
  }

  public IpAddress getPrimaryIPAddress() throws SQLException, IOException {
    DeviceId ndi = getDaemonDeviceId();
    String name=ndi.getName();
    Device nd=getHost().getNetDevice(name);
    if (nd == null) {
      throw new SQLException("Unable to find NetDevice: "+name+" on "+pkey);
    }
    return nd.getPrimaryIPAddress();
  }

  /*
  public PrivateFTPServer getPrivateFTPServer(String path) {
    return table.getConnector().privateFTPServers.getPrivateFTPServer(this, path);
  }*/

  public List<PrivateServer> getPrivateFTPServers() throws IOException, SQLException {
    return table.getConnector().getFtp().getPrivateServer().getPrivateFTPServers(this);
  }

  public List<SendmailServer> getSendmailServers() throws IOException, SQLException {
    return table.getConnector().getEmail().getSendmailServer().getSendmailServers(this);
  }

  public List<Certificate> getSslCertificates() throws IOException, SQLException {
    return table.getConnector().getPki().getCertificate().getSslCertificates(this);
  }

  public List<SystemAlias> getSystemEmailAliases() throws IOException, SQLException {
    return table.getConnector().getEmail().getSystemAlias().getSystemEmailAliases(this);
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.AO_SERVERS;
  }

  public boolean isEmailDomainAvailable(DomainName domain) throws SQLException, IOException {
    return table.getConnector().getEmail().getDomain().isEmailDomainAvailable(this, domain);
  }

  public boolean isHomeUsed(PosixPath directory) throws IOException, SQLException {
    return table.getConnector().getLinux().getUserServer().isHomeUsed(this, directory);
  }

  public boolean isMySQLServerNameAvailable(com.aoindustries.aoserv.client.mysql.Server.Name name) throws IOException, SQLException {
    return table.getConnector().getMysql().getServer().isMySQLServerNameAvailable(name, this);
  }

  public boolean isPostgresServerNameAvailable(com.aoindustries.aoserv.client.postgresql.Server.Name name) throws IOException, SQLException {
    return table.getConnector().getPostgresql().getServer().isPostgresServerNameAvailable(name, this);
  }

  public void restartApache() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_APACHE, pkey);
  }

  public void restartCron() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_CRON, pkey);
  }

  public void restartXfs() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_XFS, pkey);
  }

  public void restartXvfb() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.RESTART_XVFB, pkey);
  }

  public static class DaemonAccess {

    private final String protocol;
    private final HostAddress host;
    private final Port port;
    private final long key;

    public DaemonAccess(String protocol, HostAddress host, Port port, long key) {
      this.protocol = protocol;
      this.host = host;
      this.port = port;
      this.key = key;
    }

    public String getProtocol() {
      return protocol;
    }

    public HostAddress getHost() {
      return host;
    }

    public Port getPort() {
      return port;
    }

    public long getKey() {
      return key;
    }
  }

  public void setLastDistroTime(Timestamp distroTime) throws IOException, SQLException {
    table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_LAST_DISTRO_TIME, pkey, distroTime);
  }

  public void startApache() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_APACHE, pkey);
  }

  public void startCron() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_CRON, pkey);
  }

  public void startDistro(boolean includeUser) throws IOException, SQLException {
    table.getConnector().getDistribution_management().getDistroFile().startDistro(this, includeUser);
  }

  public void startXfs() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_XFS, pkey);
  }

  public void startXvfb() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.START_XVFB, pkey);
  }

  public void stopApache() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_APACHE, pkey);
  }

  public void stopCron() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_CRON, pkey);
  }

  public void stopXfs() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_XFS, pkey);
  }

  public void stopXvfb() throws IOException, SQLException {
    table.getConnector().requestUpdate(false, AoservProtocol.CommandID.STOP_XVFB, pkey);
  }

  @Override
  public String toStringImpl() {
    return hostname.toString();
  }

  public void waitForHttpdSiteRebuild() throws IOException, SQLException {
    table.getConnector().getWeb().getSite().waitForRebuild(this);
  }

  public void waitForLinuxAccountRebuild() throws IOException, SQLException {
    table.getConnector().getLinux().getUser().waitForRebuild(this);
  }

  public void waitForMySQLDatabaseRebuild() throws IOException, SQLException {
    table.getConnector().getMysql().getDatabase().waitForRebuild(this);
  }

  public void waitForMySQLDBUserRebuild() throws IOException, SQLException {
    table.getConnector().getMysql().getDatabaseUser().waitForRebuild(this);
  }

  public void waitForMySQLServerRebuild() throws IOException, SQLException {
    table.getConnector().getMysql().getServer().waitForRebuild(this);
  }

  public void waitForMySQLUserRebuild() throws IOException, SQLException {
    table.getConnector().getMysql().getUser().waitForRebuild(this);
  }

  public void waitForPostgresDatabaseRebuild() throws IOException, SQLException {
    table.getConnector().getPostgresql().getDatabase().waitForRebuild(this);
  }

  public void waitForPostgresServerRebuild() throws IOException, SQLException {
    table.getConnector().getPostgresql().getServer().waitForRebuild(this);
  }

  public void waitForPostgresUserRebuild() throws IOException, SQLException {
    table.getConnector().getPostgresql().getUser().waitForRebuild(this);
  }

  /**
   * Gets the 3ware RAID report.
   */
  public String get3wareRaidReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_3WARE_RAID_REPORT, pkey);
  }

  /**
   * Gets the /proc/mdstat report.
   */
  public String getMdStatReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_MD_STAT_REPORT, pkey);
  }

  public enum RaidLevel {
    linear,
    raid0,
    raid1,
    raid4,
    raid5,
    raid6,
    raid10
  }

  /**
   * The results of the most recent weekly RAID check.
   */
  public static class MdMismatchReport {

    private final String device;
    private final RaidLevel level;
    private final long count;

    MdMismatchReport(
      String device,
      RaidLevel level,
      long count
    ) {
      this.device = device;
      this.level = level;
      this.count = count;
    }

    /**
     * The device that was checked.
     */
    public String getDevice() {
      return device;
    }

    /**
     * The RAID level of the device.
     */
    public RaidLevel getLevel() {
      return level;
    }

    /**
     * The number bytes that did not match.
     */
    public long getCount() {
      return count;
    }
  }

  /**
   * Gets the MD mismatch report.
   */
  public List<MdMismatchReport> getMdMismatchReport() throws IOException, SQLException, ParseException {
    return parseMdMismatchReport(table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_MD_MISMATCH_REPORT, pkey));
  }

  /**
   * Parses a MD mismatch report.
   */
  public static List<MdMismatchReport> parseMdMismatchReport(String mismatchReport) throws ParseException {
    List<String> lines = Strings.splitLines(mismatchReport);
    int lineNum = 0;
    List<MdMismatchReport> reports = new ArrayList<>(lines.size());
    for (String line : lines) {
      lineNum++;
      List<String> values = Strings.split(line, '\t');
      if (values.size() != 3) {
        throw new ParseException(
          RESOURCES.getMessage(
            "MdMismatchReport.ParseException.badColumnCount",
            line
          ),
          lineNum
        );
      }

      // Device
      String device = values.get(0);
      if (!device.startsWith("/dev/md")) {
        throw new ParseException(
          RESOURCES.getMessage(
            "MdMismatchReport.ParseException.badDeviceStart",
            device
          ),
          lineNum
        );
      }

      // Level
      RaidLevel level = RaidLevel.valueOf(values.get(1));

      // Count
      String countString = values.get(2);
      long count;
      try {
        count = Long.parseLong(countString);
      } catch (NumberFormatException e) {
        ParseException parseException = new ParseException(
          RESOURCES.getMessage(
            "MdMismatchReport.ParseException.countNotNumber",
            countString
          ),
          lineNum
        );
        parseException.initCause(e);
        throw parseException;
      }

      reports.add(new MdMismatchReport(device, level, count));
    }
    return reports;
  }

  public static class DrbdReport {

    /**
     * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-connection-states
     */
    public enum ConnectionState {
      Unconfigured,
      StandAlone,
      Disconnecting,
      Unconnected,
      Timeout,
      BrokenPipe,
      NetworkFailure,
      ProtocolError,
      TearDown,
      WFConnection,
      WFReportParams,
      Connected,
      StartingSyncS,
      StartingSyncT,
      WFBitMapS,
      WFBitMapT,
      WFSyncUUID,
      SyncSource,
      SyncTarget,
      PausedSyncS,
      PausedSyncT,
      VerifyS,
      VerifyT
    }

    /**
     * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-roles
     */
    public enum Role {
      Unconfigured,
      Primary,
      Secondary,
      Unknown
    }

    /**
     * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-disk-states
     */
    public enum DiskState {
      Unconfigured,
      Diskless,
      Attaching,
      Failed,
      Negotiating,
      Inconsistent,
      Outdated,
      DUnknown,
      Consistent,
      UpToDate,
    }

    private final String device;
    private final String resourceHostname;
    private final String resourceDevice;
    private final ConnectionState connectionState;
    private final DiskState localDiskState;
    private final DiskState remoteDiskState;
    private final Role localRole;
    private final Role remoteRole;
    private final Long lastVerified;
    private final Long outOfSync;

    DrbdReport(
      String device,
      String resourceHostname,
      String resourceDevice,
      ConnectionState connectionState,
      DiskState localDiskState,
      DiskState remoteDiskState,
      Role localRole,
      Role remoteRole,
      Long lastVerified,
      Long outOfSync
    ) {
      this.device = device;
      this.resourceHostname = resourceHostname;
      this.resourceDevice = resourceDevice;
      this.connectionState = connectionState;
      this.localDiskState = localDiskState;
      this.remoteDiskState = remoteDiskState;
      this.localRole = localRole;
      this.remoteRole = remoteRole;
      this.lastVerified = lastVerified;
      this.outOfSync = outOfSync;
    }

    public ConnectionState getConnectionState() {
      return connectionState;
    }

    public String getDevice() {
      return device;
    }

    public DiskState getLocalDiskState() {
      return localDiskState;
    }

    public Role getLocalRole() {
      return localRole;
    }

    public DiskState getRemoteDiskState() {
      return remoteDiskState;
    }

    public Role getRemoteRole() {
      return remoteRole;
    }

    public String getResourceDevice() {
      return resourceDevice;
    }

    public String getResourceHostname() {
      return resourceHostname;
    }

    /**
     * Gets the time verification was last started from this node
     * or {@code null} if never started.
     */
    public Long getLastVerified() {
      return lastVerified;
    }

    /**
     * Gets the number of kilobytes of data out of sync, in Kibibytes.
     * <a href="http://www.drbd.org/users-guide/ch-admin.html#s-performance-indicators">http://www.drbd.org/users-guide/ch-admin.html#s-performance-indicators</a>
     */
    public Long getOutOfSync() {
      return outOfSync;
    }
  }

  /**
   * Gets the DRBD report.
   */
  public List<DrbdReport> getDrbdReport() throws IOException, SQLException, ParseException {
    return parseDrbdReport(table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_DRBD_REPORT, pkey));
  }

  /**
   * Parses a DRBD report.
   */
  public static List<DrbdReport> parseDrbdReport(String drbdReport) throws ParseException {
    List<String> lines = Strings.splitLines(drbdReport);
    int lineNum = 0;
    List<DrbdReport> reports = new ArrayList<>(lines.size());
    for (String line : lines) {
      lineNum++;
      List<String> values = Strings.split(line, '\t');
      if (values.size() != 7) {
        throw new ParseException(
          RESOURCES.getMessage(
            "DrbdReport.ParseException.badColumnCount",
            line
          ),
          lineNum
        );
      }

      // Device
      String device = values.get(0);
      if (!device.startsWith("/dev/drbd")) {
        throw new ParseException(
          RESOURCES.getMessage(
            "DrbdReport.ParseException.badDeviceStart",
            device
          ),
          lineNum
        );
      }

      // Resource
      String resource = values.get(1);
      int dashPos = resource.lastIndexOf('-');
      if (dashPos == -1) {
        throw new ParseException(
          RESOURCES.getMessage(
            "DrbdReport.ParseException.noDash",
            resource
          ),
          lineNum
        );
      }
      String domUHostname = resource.substring(0, dashPos);
      String domUDevice = resource.substring(dashPos+1);
      if (
        domUDevice.length() != 4
        || domUDevice.charAt(0) != 'x'
        || domUDevice.charAt(1) != 'v'
        || domUDevice.charAt(2) != 'd'
        || domUDevice.charAt(3)<'a'
        || domUDevice.charAt(3)>'z'
      ) {
        throw new ParseException(
          RESOURCES.getMessage(
            "DrbdReport.ParseException.unexpectedResourceEnding",
            domUDevice
          ),
          lineNum
        );
      }

      // Connection State
      String connectionStateString = values.get(2);
      final DrbdReport.ConnectionState connectionState =
        "null".equals(connectionStateString)
        ? null
        : DrbdReport.ConnectionState.valueOf(connectionStateString);

      // Disk states
      String ds = values.get(3);
      final DrbdReport.DiskState localDiskState;
      final DrbdReport.DiskState remoteDiskState;
      if ("null".equals(ds)) {
        localDiskState = null;
        remoteDiskState = null;
      } else if (DrbdReport.DiskState.Unconfigured.name().equals(ds)) {
        localDiskState = DrbdReport.DiskState.Unconfigured;
        remoteDiskState = DrbdReport.DiskState.Unconfigured;
      } else {
        int dsSlashPos = ds.indexOf('/');
        if (dsSlashPos == -1) {
          throw new ParseException(
            RESOURCES.getMessage(
              "DrbdReport.ParseException.noSlashInDiskStates",
              ds
            ),
            lineNum
          );
        }
        localDiskState = DrbdReport.DiskState.valueOf(ds.substring(0, dsSlashPos));
        remoteDiskState = DrbdReport.DiskState.valueOf(ds.substring(dsSlashPos+1));
      }

      // Roles
      String state = values.get(4);
      final DrbdReport.Role localRole;
      final DrbdReport.Role remoteRole;
      if ("null".equals(state)) {
        localRole = null;
        remoteRole = null;
      } else if (DrbdReport.Role.Unconfigured.name().equals(state)) {
        localRole = DrbdReport.Role.Unconfigured;
        remoteRole = DrbdReport.Role.Unconfigured;
      } else {
        int slashPos = state.indexOf('/');
        if (slashPos == -1) {
          throw new ParseException(
            RESOURCES.getMessage(
              "DrbdReport.ParseException.noSlashInState",
              state
            ),
            lineNum
          );
        }
        localRole = DrbdReport.Role.valueOf(state.substring(0, slashPos));
        remoteRole = DrbdReport.Role.valueOf(state.substring(slashPos+1));
      }

      // Last Verified
      String lastVerifiedString = values.get(5);
      Long lastVerified =
        "null".equals(lastVerifiedString)
        ? null
        : (Long.parseLong(lastVerifiedString)*1000)
      ;

      // Out of Sync
      String outOfSyncString = values.get(6);
      Long outOfSync =
        "null".equals(outOfSyncString)
        ? null
        : Long.parseLong(outOfSyncString)
      ;

      reports.add(
        new DrbdReport(
          device,
          domUHostname,
          domUDevice,
          connectionState,
          localDiskState,
          remoteDiskState,
          localRole,
          remoteRole,
          lastVerified,
          outOfSync
        )
      );
    }
    return reports;
  }

  public static class LvmReport {

    private static boolean overlaps(long start1, long size1, long start2, long size2) {
      return
        (start2+size2)>start1
        && (start1+size1)>start2
      ;
    }

    public static class VolumeGroup implements Comparable<VolumeGroup> {

      /**
       * Parses the output of vgs --noheadings --separator=$'\t' --units=b -o vg_name,vg_extent_size,vg_extent_count,vg_free_count,pv_count,lv_count
       */
      private static Map<String, VolumeGroup> parseVgsReport(String vgs) throws ParseException {
        List<String> lines = Strings.splitLines(vgs);
        int size = lines.size();
        Map<String, VolumeGroup> volumeGroups = AoCollections.newHashMap(size);
        for (int c=0;c<size;c++) {
          final int lineNum = c+1;
          String line = lines.get(c);
          List<String> fields = Strings.split(line, '\t');
          if (fields.size() != 6) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.VolumeGroup.parseVgsReport.badColumnCount",
                6,
                fields.size()
              ),
              lineNum
            );
          }
          String vgExtentSize = fields.get(1).trim();
          if (!vgExtentSize.endsWith("B")) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.VolumeGroup.parseVgsReport.invalidateVgExtentSize",
                vgExtentSize
              ),
              lineNum
            );
          }
          vgExtentSize = vgExtentSize.substring(0, vgExtentSize.length()-1);
          String vgName = fields.get(0).trim();
          if (
            volumeGroups.put(
              vgName,
              new VolumeGroup(
                vgName,
                Integer.parseInt(vgExtentSize),
                Long.parseLong(fields.get(2).trim()),
                Long.parseLong(fields.get(3).trim()),
                Integer.parseInt(fields.get(4).trim()),
                Integer.parseInt(fields.get(5).trim())
              )
            ) != null
          ) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.VolumeGroup.parseVgsReport.vgNameFoundTwice",
                vgName
              ),
              lineNum
            );
          }
        }
        return Collections.unmodifiableMap(volumeGroups);
      }

      private final String vgName;
      private final int vgExtentSize;
      private final long vgExtentCount;
      private final long vgFreeCount;
      private final int pvCount;
      private final int lvCount;
      private final Map<String, LogicalVolume> logicalVolumes = new HashMap<>();
      private final Map<String, LogicalVolume> unmodifiableLogicalVolumes = Collections.unmodifiableMap(logicalVolumes);

      private VolumeGroup(String vgName, int vgExtentSize, long vgExtentCount, long vgFreeCount, int pvCount, int lvCount) {
        this.vgName = vgName;
        this.vgExtentSize = vgExtentSize;
        this.vgExtentCount = vgExtentCount;
        this.vgFreeCount = vgFreeCount;
        this.pvCount = pvCount;
        this.lvCount = lvCount;
      }

      @Override
      public String toString() {
        return vgName;
      }

      /**
       * Sorts ascending by:
       * <ol>
       *   <li>vgName</li>
       * </ol>
       */
      @Override
      public int compareTo(VolumeGroup other) {
        return vgName.compareTo(other.vgName);
      }

      public int getLvCount() {
        return lvCount;
      }

      public int getPvCount() {
        return pvCount;
      }

      public long getVgExtentCount() {
        return vgExtentCount;
      }

      public int getVgExtentSize() {
        return vgExtentSize;
      }

      public long getVgFreeCount() {
        return vgFreeCount;
      }

      public String getVgName() {
        return vgName;
      }

      public LogicalVolume getLogicalVolume(String lvName) {
        return logicalVolumes.get(lvName);
      }

      public Map<String, LogicalVolume> getLogicalVolumes() {
        return unmodifiableLogicalVolumes;
      }
    }

    public static class PhysicalVolume implements Comparable<PhysicalVolume> {

      /**
       * Parses the output of pvs --noheadings --separator=$'\t' --units=b -o pv_name,pv_pe_count,pv_pe_alloc_count,pv_size,vg_name
       */
      private static Map<String, PhysicalVolume> parsePvsReport(String pvs, Map<String, VolumeGroup> volumeGroups) throws ParseException {
        List<String> lines = Strings.splitLines(pvs);
        int size = lines.size();
        Map<String, PhysicalVolume> physicalVolumes = AoCollections.newHashMap(size);
        Map<String, Integer> vgPhysicalVolumeCounts = AoCollections.newHashMap(volumeGroups.size());
        Map<String, Long> vgExtentCountTotals = AoCollections.newHashMap(volumeGroups.size());
        Map<String, Long> vgAllocCountTotals = AoCollections.newHashMap(volumeGroups.size());
        for (int c=0;c<size;c++) {
          final int lineNum = c+1;
          String line = lines.get(c);
          List<String> fields = Strings.split(line, '\t');
          if (fields.size() != 5) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.PhysicalVolume.parsePvsReport.badColumnCount",
                5,
                fields.size()
              ),
              lineNum
            );
          }
          String pvName = fields.get(0).trim();
          String vgName = fields.get(4).trim();
          long pvPeCount = Long.parseLong(fields.get(1).trim());
          long pvPeAllocCount = Long.parseLong(fields.get(2).trim());
          String pvSizeString = fields.get(3).trim();
          if (pvSizeString.endsWith("B")) {
            pvSizeString = pvSizeString.substring(0, pvSizeString.length()-1);
          }
          long pvSize = Long.parseLong(pvSizeString);
          VolumeGroup volumeGroup;
          if (vgName.length() == 0) {
            if (pvPeCount != 0 || pvPeAllocCount != 0) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.PhysicalVolume.parsePvsReport.invalidValues",
                  pvPeCount,
                  pvPeAllocCount,
                  vgName
                ),
                lineNum
              );
            }
            volumeGroup = null;
          } else {
            if (pvPeCount<1 && pvPeAllocCount<0 && pvPeAllocCount>pvPeCount) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.PhysicalVolume.parsePvsReport.invalidValues",
                  pvPeCount,
                  pvPeAllocCount,
                  vgName
                ),
                lineNum
              );
            }
            volumeGroup = volumeGroups.get(vgName);
            if (volumeGroup == null) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.PhysicalVolume.parsePvsReport.volumeGroupNotFound",
                  vgName
                ),
                lineNum
              );
            }
            // Add to totals for consistency checks
            Integer count = vgPhysicalVolumeCounts.get(vgName);
            vgPhysicalVolumeCounts.put(
              vgName,
              count == null ? 1 : (count+1)
            );
            Long vgExtentCountTotal = vgExtentCountTotals.get(vgName);
            vgExtentCountTotals.put(
              vgName,
              vgExtentCountTotal == null ? pvPeCount : (vgExtentCountTotal+pvPeCount)
            );
            Long vgFreeCountTotal = vgAllocCountTotals.get(vgName);
            vgAllocCountTotals.put(
              vgName,
              vgFreeCountTotal == null ? pvPeAllocCount : (vgFreeCountTotal+pvPeAllocCount)
            );
          }
          if (
            physicalVolumes.put(
              pvName,
              new PhysicalVolume(
                pvName,
                pvPeCount,
                pvPeAllocCount,
                pvSize,
                volumeGroup
              )
            ) != null
          ) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.PhysicalVolume.parsePvsReport.pvNameFoundTwice",
                pvName
              ),
              lineNum
            );
          }
        }
        for (Map.Entry<String, VolumeGroup> entry : volumeGroups.entrySet()) {
          // Make sure counts match vgs report
          VolumeGroup volumeGroup = entry.getValue();
          String vgName = entry.getKey();
          // Check pvCount
          int expectedPvCount = volumeGroup.getPvCount();
          Integer actualPvCountI = vgPhysicalVolumeCounts.get(vgName);
          int actualPvCount = actualPvCountI == null ? 0 : actualPvCountI;
          if (expectedPvCount != actualPvCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.PhysicalVolume.parsePvsReport.mismatchPvCount",
                vgName
              ),
              0
            );
          }
          // Check vgExtentCount
          long expectedVgExtentCount = volumeGroup.getVgExtentCount();
          Long actualVgExtentCountL = vgExtentCountTotals.get(vgName);
          long actualVgExtentCount = actualVgExtentCountL == null ? 0 : actualVgExtentCountL;
          if (expectedVgExtentCount != actualVgExtentCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.PhysicalVolume.parsePvsReport.badVgExtentCount",
                vgName
              ),
              0
            );
          }
          // Check vgFreeCount
          long expectedVgFreeCount = volumeGroup.getVgFreeCount();
          Long vgAllocCountTotalL = vgAllocCountTotals.get(vgName);
          long actualVgFreeCount = vgAllocCountTotalL == null ? expectedVgExtentCount : (expectedVgExtentCount-vgAllocCountTotalL);
          if (expectedVgFreeCount != actualVgFreeCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.PhysicalVolume.parsePvsReport.badVgFreeCount",
                vgName
              ),
              0
            );
          }
        }
        return Collections.unmodifiableMap(physicalVolumes);
      }

      private final String pvName;
      private final long pvPeCount;
      private final long pvPeAllocCount;
      private final long pvSize;
      private final VolumeGroup volumeGroup;

      private PhysicalVolume(String pvName, long pvPeCount, long pvPeAllocCount, long pvSize, VolumeGroup volumeGroup) {
        this.pvName = pvName;
        this.pvPeCount = pvPeCount;
        this.pvPeAllocCount = pvPeAllocCount;
        this.pvSize = pvSize;
        this.volumeGroup = volumeGroup;
      }

      @Override
      public String toString() {
        return pvName;
      }

      /**
       * Sorts ascending by:
       * <ol>
       *   <li>pvName</li>
       * </ol>
       */
      @Override
      public int compareTo(PhysicalVolume other) {
        return pvName.compareTo(other.pvName);
      }

      public String getPvName() {
        return pvName;
      }

      /**
       * The number of extents allocated, this is 0 when not allocated.
       */
      public long getPvPeAllocCount() {
        return pvPeAllocCount;
      }

      /**
       * The total number of extents, this is 0 when not allocated.
       */
      public long getPvPeCount() {
        return pvPeCount;
      }

      /**
       * The size of the physical volume in bytes.  This is always available,
       * even when not allocated.
       */
      public long getPvSize() {
        return pvSize;
      }

      public VolumeGroup getVolumeGroup() {
        return volumeGroup;
      }
    }

    public static class LogicalVolume implements Comparable<LogicalVolume> {

      /**
       * Parses the output from lvs --noheadings --separator=$'\t' -o vg_name,lv_name,seg_count,segtype,stripes,seg_start_pe,seg_pe_ranges
       */
      private static void parseLvsReport(String lvs, Map<String, VolumeGroup> volumeGroups, Map<String, PhysicalVolume> physicalVolumes) throws ParseException {
        final List<String> lines = Strings.splitLines(lvs);
        final int size = lines.size();
        for (int c=0;c<size;c++) {
          final int lineNum = c+1;
          final String line = lines.get(c);
          final List<String> fields = Strings.split(line, '\t');
          if (fields.size() != 7) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.badColumnCount",
                7,
                fields.size()
              ),
              lineNum
            );
          }
          final String vgName = fields.get(0).trim();
          final String lvName = fields.get(1).trim();
          final int segCount = Integer.parseInt(fields.get(2).trim());
          final SegmentType segType = SegmentType.valueOf(fields.get(3).trim());
          final int stripeCount = Integer.parseInt(fields.get(4).trim());
          final long segStartPe = Long.parseLong(fields.get(5).trim());
          final List<String> segPeRanges = Strings.split(fields.get(6).trim(), ' ');

          // Find the volume group
          VolumeGroup volumeGroup = volumeGroups.get(vgName);
          if (volumeGroup == null) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.volumeGroupNotFound",
                vgName
              ),
              lineNum
            );
          }

          // Find or add the logical volume
          if (segCount<1) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.badSegCount",
                segCount
              ),
              lineNum
            );
          }
          LogicalVolume logicalVolume = volumeGroup.getLogicalVolume(lvName);
          if (logicalVolume == null) {
            logicalVolume = new LogicalVolume(volumeGroup, lvName, segCount);
            volumeGroup.logicalVolumes.put(lvName, logicalVolume);
          } else {
            if (segCount != logicalVolume.segCount) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.LogicalVolume.parseLsvReport.segCountChanged",
                  logicalVolume.segCount,
                  segCount
                ),
                lineNum
              );
            }
          }

          // Add the segment
          if (stripeCount<1) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.badStripeCount",
                stripeCount
              ),
              lineNum
            );
          }
          if (segPeRanges.size() != stripeCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.mismatchStripeCount"
              ),
              lineNum
            );
          }
          Segment newSegment = new Segment(logicalVolume, segType, stripeCount, segStartPe);
          // Check no overlap in segments
          for (Segment existingSegment : logicalVolume.segments) {
            if (newSegment.overlaps(existingSegment)) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.LogicalVolume.parseLsvReport.segmentOverlap",
                  existingSegment,
                  newSegment
                ),
                lineNum
              );
            }
          }
          logicalVolume.segments.add(newSegment);

          // Add the stripes
          for (String segPeRange : segPeRanges) {
            int colonPos = segPeRange.indexOf(':');
            if (colonPos == -1) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoColon",
                  segPeRange
                ),
                lineNum
              );
            }
            int dashPos = segPeRange.indexOf('-', colonPos+1);
            if (dashPos == -1) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoDash",
                  segPeRange
                ),
                lineNum
              );
            }
            String stripeDevice = segPeRange.substring(0, colonPos).trim();
            PhysicalVolume stripePv = physicalVolumes.get(stripeDevice);
            if (stripePv == null) {
              throw new ParseException(
                RESOURCES.getMessage(
                  "LvmReport.LogicalVolume.parseLsvReport.physicalVolumeNotFound",
                  stripeDevice
                ),
                lineNum
              );
            }
            long firstPe = Long.parseLong(segPeRange.substring(colonPos+1, dashPos).trim());
            if (firstPe<0) {
              throw new AssertionError("firstPe<0: "+firstPe);
            }
            long lastPe = Long.parseLong(segPeRange.substring(dashPos+1).trim());
            if (lastPe<firstPe) {
              throw new AssertionError("lastPe<firstPe: "+lastPe+"<"+firstPe);
            }
            // Make sure no overlap with other stripes in the same physical volume
            Stripe newStripe = new Stripe(newSegment, stripePv, firstPe, lastPe);
            for (VolumeGroup existingVG : volumeGroups.values()) {
              for (LogicalVolume existingLV : existingVG.logicalVolumes.values()) {
                for (Segment existingSegment : existingLV.segments) {
                  for (Stripe existingStripe : existingSegment.stripes) {
                    if (newStripe.overlaps(existingStripe)) {
                      throw new ParseException(
                        RESOURCES.getMessage(
                          "LvmReport.LogicalVolume.parseLsvReport.stripeOverlap",
                          existingStripe,
                          newStripe
                        ),
                        lineNum
                      );
                    }
                  }
                }
              }
            }
            newSegment.stripes.add(newStripe);
          }
          Collections.sort(newSegment.stripes);
        }

        // Final cleaning and sanity checks
        for (VolumeGroup volumeGroup : volumeGroups.values()) {
          // Make sure counts match vgs report
          int expectedLvCount = volumeGroup.getLvCount();
          int actualLvCount = volumeGroup.logicalVolumes.size();
          if (expectedLvCount != actualLvCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.mismatchLvCount",
                volumeGroup
              ),
              0
            );
          }

          // Check vgExtentCount and vgFreeCount matches total in logicalVolumes
          long totalLvExtents = 0;
          for (LogicalVolume lv : volumeGroup.logicalVolumes.values()) {
            for (Segment segment : lv.segments) {
              for (Stripe stripe : segment.stripes) {
                totalLvExtents += stripe.getLastPe()-stripe.getFirstPe()+1;
              }
            }
          }
          long expectedFreeCount = volumeGroup.vgFreeCount;
          long actualFreeCount = volumeGroup.vgExtentCount - totalLvExtents;
          if (expectedFreeCount != actualFreeCount) {
            throw new ParseException(
              RESOURCES.getMessage(
                "LvmReport.LogicalVolume.parseLsvReport.mismatchFreeCount",
                volumeGroup
              ),
              0
            );
          }

          // Sort segments by segStartPe
          for (LogicalVolume logicalVolume : volumeGroup.logicalVolumes.values()) {
            Collections.sort(logicalVolume.segments);
          }
        }
      }

      private final VolumeGroup volumeGroup;
      private final String lvName;
      private final int segCount;
      private final List<Segment> segments = new ArrayList<>();
      private final List<Segment> unmodifiableSegments = Collections.unmodifiableList(segments);

      private LogicalVolume(VolumeGroup volumeGroup, String lvName, int segCount) {
        this.volumeGroup = volumeGroup;
        this.lvName = lvName;
        this.segCount = segCount;
      }

      @Override
      public String toString() {
        return volumeGroup+"/"+lvName;
      }

      /**
       * Sorts ascending by:
       * <ol>
       *   <li>volumeGroup</li>
       *   <li>lvName</li>
       * </ol>
       */
      @Override
      public int compareTo(LogicalVolume other) {
        int diff = volumeGroup.compareTo(other.volumeGroup);
        if (diff != 0) {
          return diff;
        }
        return lvName.compareTo(other.lvName);
      }

      public VolumeGroup getVolumeGroup() {
        return volumeGroup;
      }

      public String getLvName() {
        return lvName;
      }

      public int getSegCount() {
        return segCount;
      }

      public List<Segment> getSegments() {
        return unmodifiableSegments;
      }
    }

    public enum SegmentType {
      linear,
      striped
    }

    public static class Segment implements Comparable<Segment> {

      private final LogicalVolume logicalVolume;
      private final SegmentType segtype;
      private final int stripeCount;
      private final long segStartPe;
      private final List<Stripe> stripes = new ArrayList<>();
      private final List<Stripe> unmodifiableStripes = Collections.unmodifiableList(stripes);

      private Segment(LogicalVolume logicalVolume, SegmentType segtype, int stripeCount, long segStartPe) {
        this.logicalVolume = logicalVolume;
        this.segtype = segtype;
        this.stripeCount = stripeCount;
        this.segStartPe = segStartPe;
      }

      @Override
      public String toString() {
        return logicalVolume+"("+segStartPe+"-"+getSegEndPe()+")";
      }

      /**
       * Sorts ascending by:
       * <ol>
       *   <li>logicalVolume</li>
       *   <li>segStartPe</li>
       * </ol>
       */
      @Override
      public int compareTo(Segment other) {
        int diff = logicalVolume.compareTo(other.logicalVolume);
        if (diff != 0) {
          return diff;
        }
        if (segStartPe<other.segStartPe) {
          return -1;
        }
        if (segStartPe>other.segStartPe) {
          return 1;
        }
        return 0;
      }

      public LogicalVolume getLogicalVolume() {
        return logicalVolume;
      }

      public SegmentType getSegtype() {
        return segtype;
      }

      public int getStripeCount() {
        return stripeCount;
      }

      public long getSegStartPe() {
        return segStartPe;
      }

      /**
       * Gets the last logical physical extent as determined by counting
       * the total size of the stripes and using the following function:
       * <pre>segStartPe + totalStripePE - 1</pre>
       */
      public long getSegEndPe() {
        long segmentCount = 0;
        for (Stripe stripe : stripes) {
          segmentCount += stripe.getLastPe() - stripe.getFirstPe() + 1;
        }
        return segStartPe+segmentCount-1;
      }

      public List<Stripe> getStripes() {
        return unmodifiableStripes;
      }

      public boolean overlaps(Segment other) {
        // Doesn't overlap self
        return
          this != other
          && logicalVolume == other.logicalVolume
          && LvmReport.overlaps(
            segStartPe,
            getSegEndPe()-segStartPe+1,
            other.segStartPe,
            other.getSegEndPe()-other.segStartPe+1
          )
        ;
      }
    }

    public static class Stripe implements Comparable<Stripe> {

      private final Segment segment;
      private final PhysicalVolume physicalVolume;
      private final long firstPe;
      private final long lastPe;

      private Stripe(Segment segment, PhysicalVolume physicalVolume, long firstPe, long lastPe) {
        this.segment = segment;
        this.physicalVolume = physicalVolume;
        this.firstPe = firstPe;
        this.lastPe = lastPe;
      }

      @Override
      public String toString() {
        return segment+":"+physicalVolume+"("+firstPe+"-"+lastPe+")";
      }

      /**
       * Sorts ascending by:
       * <ol>
       *   <li>segment</li>
       *   <li>firstPe</li>
       * </ol>
       */
      @Override
      public int compareTo(Stripe other) {
        int diff = segment.compareTo(other.segment);
        if (diff != 0) {
          return diff;
        }
        if (firstPe<other.firstPe) {
          return -1;
        }
        if (firstPe>other.firstPe) {
          return 1;
        }
        return 0;
      }

      public Segment getSegment() {
        return segment;
      }

      public PhysicalVolume getPhysicalVolume() {
        return physicalVolume;
      }

      public long getFirstPe() {
        return firstPe;
      }

      public long getLastPe() {
        return lastPe;
      }

      public boolean overlaps(Stripe other) {
        // Doesn't overlap self
        return
          this != other
          && physicalVolume == other.physicalVolume
          && LvmReport.overlaps(
            firstPe,
            lastPe-firstPe+1,
            other.firstPe,
            other.lastPe-other.firstPe+1
          )
        ;
      }
    }

    private final Map<String, VolumeGroup> volumeGroups;
    private final Map<String, PhysicalVolume> physicalVolumes;

    private LvmReport(String vgs, String pvs, String lvs) throws ParseException {
      this.volumeGroups = VolumeGroup.parseVgsReport(vgs);
      this.physicalVolumes = PhysicalVolume.parsePvsReport(pvs, volumeGroups);
      LogicalVolume.parseLvsReport(lvs, volumeGroups, physicalVolumes);
    }

    public PhysicalVolume getPhysicalVolume(String pvName) {
      return physicalVolumes.get(pvName);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
    public Map<String, PhysicalVolume> getPhysicalVolumes() {
      return physicalVolumes;
    }

    public VolumeGroup getVolumeGroup(String vgName) {
      return volumeGroups.get(vgName);
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
    public Map<String, VolumeGroup> getVolumeGroups() {
      return volumeGroups;
    }
  }

  /**
   * Gets the LVM report.
   */
  public LvmReport getLvmReport() throws IOException, SQLException, ParseException {
    try {
      return table.getConnector().requestResult(
        true,
        AoservProtocol.CommandID.GET_AO_SERVER_LVM_REPORT,
        // Java 9: new AOServConnector.ResultRequest<>
        new AOServConnector.ResultRequest<LvmReport>() {
          private String vgs;
          private String pvs;
          private String lvs;
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
          }
          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code=in.readByte();
            if (code == AoservProtocol.DONE) {
              vgs = in.readUTF();
              pvs = in.readUTF();
              lvs = in.readUTF();
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: "+code);
            }
          }
          @Override
          public LvmReport afterRelease() {
            try {
              return new LvmReport(vgs, pvs, lvs);
            } catch (ParseException err) {
              throw new WrappedException(err);
            }
          }
        }
      );
    } catch (WrappedException err) {
      Throwable cause = err.getCause();
      if (cause instanceof ParseException) {
        throw (ParseException)cause;
      }
      throw err;
    }
  }

  /**
   * Gets the hard drive temperature report.
   */
  public String getHddTempReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_HDD_TEMP_REPORT, pkey);
  }

  /**
   * Gets the model of each hard drive on the server.  The key
   * is the device name and the value is the model name.
   */
  public Map<String, String> getHddModelReport() throws IOException, SQLException, ParseException {
    String report = table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_HDD_MODEL_REPORT, pkey);
    List<String> lines = Strings.splitLines(report);
    int lineNum = 0;
    Map<String, String> results = AoCollections.newHashMap(lines.size());
    for (String line : lines) {
      lineNum++;
      int colonPos = line.indexOf(':');
      if (colonPos == -1) {
        throw new ParseException(
          RESOURCES.getMessage(
            "getHddModelReport.ParseException.noColon",
            line
          ),
          lineNum
        );
      }
      String device = line.substring(0, colonPos).trim();
      String model = line.substring(colonPos+1).trim();
      if (results.put(device, model) != null) {
        throw new ParseException(
          RESOURCES.getMessage(
            "getHddModelReport.ParseException.duplicateDevice",
            device
          ),
          lineNum
        );
      }
    }
    return results;
  }

  /**
   * Gets the filesystem states report.
   *
   * @deprecated  Use {@code getFilesystemsCsvReport()} instead to let the API parse the report.
   */
  @Deprecated
  public String getFilesystemsCsvReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_FILESYSTEMS_CSV_REPORT, pkey);
  }

  public static class FilesystemReport {

    private final String mountPoint;
    private final String device;
    private final long bytes;
    private final long used;
    private final long free;
    private final byte use;
    private final Long inodes;
    private final Long inodesUsed;
    private final Long inodesFree;
    private final Byte inodeUse;
    private final String fsType;
    private final String mountOptions;
    private final String extState;
    private final String extMaxMount;
    private final String extCheckInterval;

    private FilesystemReport(
      String mountPoint,
      String device,
      long bytes,
      long used,
      long free,
      byte use,
      Long inodes,
      Long inodesUsed,
      Long inodesFree,
      Byte inodeUse,
      String fsType,
      String mountOptions,
      String extState,
      String extMaxMount,
      String extCheckInterval
    ) {
      this.mountPoint = mountPoint;
      this.device = device;
      this.bytes = bytes;
      this.used = used;
      this.free = free;
      this.use = use;
      this.inodes = inodes;
      this.inodesUsed = inodesUsed;
      this.inodesFree = inodesFree;
      this.inodeUse = inodeUse;
      this.fsType = fsType;
      this.mountOptions = mountOptions;
      this.extState = extState;
      this.extMaxMount = extMaxMount;
      this.extCheckInterval = extCheckInterval;
    }

    public String getMountPoint() {
      return mountPoint;
    }

    public String getDevice() {
      return device;
    }

    public long getBytes() {
      return bytes;
    }

    public long getUsed() {
      return used;
    }

    public long getFree() {
      return free;
    }

    public byte getUse() {
      return use;
    }

    public Long getInodes() {
      return inodes;
    }

    public Long getInodesUsed() {
      return inodesUsed;
    }

    public Long getInodesFree() {
      return inodesFree;
    }

    public Byte getInodeUse() {
      return inodeUse;
    }

    public String getFsType() {
      return fsType;
    }

    public String getMountOptions() {
      return mountOptions;
    }

    public String getExtState() {
      return extState;
    }

    public String getExtMaxMount() {
      return extMaxMount;
    }

    public String getExtCheckInterval() {
      return extCheckInterval;
    }

    /**
     * Checks that this filesystem matches the expected configuration for a {@link Server}.
     *
     * @return  the message describing the configuration warning or {@code null} if all configs OK.
     */
    public String getConfigMessage() {
      switch (fsType) {
        case "ext3":
          // Make sure extmaxmount is -1
          if (!"-1".equals(extMaxMount)) {
            return RESOURCES.getMessage("FilesystemReport.configMessage.extmaxmount.ext3", extMaxMount);
          }
          // Make sure extchkint is 0
          if (!"0 (<none>)".equals(extCheckInterval)) {
            return RESOURCES.getMessage("FilesystemReport.configMessage.extchkint.ext3", extCheckInterval);
          }
          return null;
        case "ext2":
          // Make sure extmaxmount is never -1
          if ("-1".equals(extMaxMount)) {
            return RESOURCES.getMessage("FilesystemReport.configMessage.extmaxmount.ext2", extMaxMount);
          }
          // Make sure extchkint is never 0
          if ("0 (<none>)".equals(extCheckInterval)) {
            return RESOURCES.getMessage("FilesystemReport.configMessage.extchkint.ext2", extCheckInterval);
          }
          return null;
        default:
          // No specific expectations for other types of filesystems
          return null;
      }
    }

    /**
     * Checks that this filesystem is in a clean state and does not require any corrective action.
     */
    public boolean isClean() {
      switch (fsType) {
        case "ext3":
          return "clean".equals(extState);
        case "ext2":
          return
            "not clean".equals(extState) // Normal state when mounted
            || "clean".equals(extState)
          ;
        default:
          // Other types of filesystems are assumed to be clean until we have more information
          return true;
      }
    }
  }

  private static Byte parsePercent(String value) throws NumberFormatException {
    if (value.isEmpty()) {
      return null;
    }
    if (!value.endsWith("%")) {
      throw new NumberFormatException("Percentage does not end with '%': " + value);
    }
    return Byte.parseByte(value.substring(0, value.length()-1));
  }

  private static Long parseLong(String value) throws NumberFormatException {
    if (value.isEmpty()) {
      return null;
    }
    return Long.parseLong(value);
  }

  public Map<String, FilesystemReport> getFilesystemsReport() throws IOException, SQLException {
    Map<String, FilesystemReport> reports = new LinkedHashMap<>();
    // Extremely simple CSV parser, but sufficient for the known format of the source data
    List<String> lines = Strings.splitLines(getFilesystemsCsvReport());
    if (lines.isEmpty()) {
      throw new IOException("No lines from report");
    }
    for (int i=0, numLines=lines.size(); i<numLines; i++) {
      String line = lines.get(i);
      List<String> columns = Strings.split(line, "\",\"");
      if (columns.size() != 15) {
        throw new IOException("Line does not have 15 columns: " + columns.size());
      }
      String mountPoint = columns.get(0);
      if (!mountPoint.startsWith("\"")) {
        throw new AssertionError();
      }
      mountPoint = mountPoint.substring(1);
      String extchkint = columns.get(14);
      if (!extchkint.endsWith("\"")) {
        throw new AssertionError();
      }
      extchkint = extchkint.substring(0, extchkint.length() - 1);
      if (i == 0) {
        if (
          !"mountpoint".equals(mountPoint)
          || !"device".equals(columns.get(1))
          || !"bytes".equals(columns.get(2))
          || !"used".equals(columns.get(3))
          || !"free".equals(columns.get(4))
          || !"use".equals(columns.get(5))
          || !"inodes".equals(columns.get(6))
          || !"iused".equals(columns.get(7))
          || !"ifree".equals(columns.get(8))
          || !"iuse".equals(columns.get(9))
          || !"fstype".equals(columns.get(10))
          || !"mountoptions".equals(columns.get(11))
          || !"extstate".equals(columns.get(12))
          || !"extmaxmount".equals(columns.get(13))
          || !"extchkint".equals(extchkint)
        ) {
          throw new IOException("First line is not the expected column labels");
        }
      } else {
        if (
          reports.put(
            mountPoint,
            new FilesystemReport(
              mountPoint,
              columns.get(1), // device
              Long.parseLong(columns.get(2)), // bytes
              Long.parseLong(columns.get(3)), // used
              Long.parseLong(columns.get(4)), // free
              parsePercent(columns.get(5)), // use
              parseLong(columns.get(6)), // inodes
              parseLong(columns.get(7)), // inodesUsed
              parseLong(columns.get(8)), // inodesFree
              parsePercent(columns.get(9)), // inodeUse
              columns.get(10), // fsType
              columns.get(11), // mountOptions
              columns.get(12), // extState
              columns.get(13), // extMaxMount
              extchkint // extCheckInterval
            )
          ) != null
        ) {
          throw new IOException("Duplicate mount point: " + mountPoint);
        }
      }
    }
    return AoCollections.optimalUnmodifiableMap(reports);
  }

  /**
   * Gets the output of /proc/loadavg
   */
  public String getLoadAvgReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_LOADAVG_REPORT, pkey);
  }

  /**
   * Gets the output of /proc/meminfo
   */
  public String getMemInfoReport() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_MEMINFO_REPORT, pkey);
  }

  /**
   * Checks a port from the daemon's point of view.  This is required for monitoring of private and loopback IPs.
   */
  public String checkPort(InetAddress ipAddress, Port port, String appProtocol, URIParameters monitoringParameters) throws IOException, SQLException {
    return table.getConnector().requestStringQuery(
      true,
      AoservProtocol.CommandID.AO_SERVER_CHECK_PORT,
      pkey,
      ipAddress.toString(),
      port,
      appProtocol,
      Bind.encodeParameters(monitoringParameters)
    );
  }

  /**
   * Gets the current system time in milliseconds.
   */
  public long getSystemTimeMillis() throws IOException, SQLException {
    return table.getConnector().requestLongQuery(true, AoservProtocol.CommandID.GET_AO_SERVER_SYSTEM_TIME_MILLIS, pkey);
  }

  public List<MysqlReplication> getFailoverMySQLReplications() throws IOException, SQLException {
    return table.getConnector().getBackup().getMysqlReplication().getFailoverMySQLReplications(this);
  }

  /**
   * Gets the status line of a SMTP server from the server from the provided source IP.
   */
  public String checkSmtpBlacklist(InetAddress sourceIp, InetAddress connectIp) throws IOException, SQLException {
    return table.getConnector().requestStringQuery(false, AoservProtocol.CommandID.AO_SERVER_CHECK_SMTP_BLACKLIST, pkey, sourceIp, connectIp);
  }

  /**
   * Gets UPS status report
   */
  public String getUpsStatus() throws IOException, SQLException {
    return table.getConnector().requestStringQuery(true, AoservProtocol.CommandID.GET_UPS_STATUS, pkey);
  }

  // <editor-fold defaultstate="collapsed" desc="DTO">
  @Override
  public com.aoindustries.aoserv.client.dto.LinuxServer getDto() {
    return new com.aoindustries.aoserv.client.dto.LinuxServer(
      getPkey(),
      getDto(hostname),
      daemon_bind == -1 ? null : daemon_bind,
      pool_size,
      distro_hour,
      last_distro_time == null ? null : last_distro_time.getTime(),
      failover_server == -1 ? null : failover_server,
      daemonDeviceId,
      daemon_connect_bind == -1 ? null : daemon_connect_bind,
      time_zone,
      jilter_bind == -1 ? null : jilter_bind,
      restrict_outbound_email,
      getDto(daemon_connect_address),
      failover_batch_size,
      Float.isNaN(monitoring_load_low) ? null : monitoring_load_low,
      Float.isNaN(monitoring_load_medium) ? null : monitoring_load_medium,
      Float.isNaN(monitoring_load_high) ? null : monitoring_load_high,
      Float.isNaN(monitoring_load_critical) ? null : monitoring_load_critical,
      getDto(uidMin),
      getDto(gidMin),
      getDto(uidMax),
      getDto(gidMax),
      getDto(lastUid),
      getDto(lastGid),
      sftp_umask == -1 ? null : sftp_umask
    );
  }
  // </editor-fold>
}
