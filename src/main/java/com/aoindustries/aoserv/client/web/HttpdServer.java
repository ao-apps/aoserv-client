/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.util.SystemdUtil;
import com.aoindustries.aoserv.client.web.tomcat.Worker;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>HttpdServer</code> represents one running instance of the
 * Apache web server.  Each physical server may run any number of
 * Apache web servers, and each of those may respond to multiple
 * IP addresses and ports, and serve content for many sites.
 *
 * @see  HttpdBind
 * @see  Site
 * @see  VirtualHost
 *
 * @author  AO Industries, Inc.
 */
public final class HttpdServer extends CachedObjectIntegerKey<HttpdServer> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 1;
  static final int COLUMN_PACKAGE = 8;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_NAME_name = "name";

  private int aoServer;
  private String name;
  private boolean canAddSites;
  private int linuxServerAccount;
  private int linuxServerGroup;
  private int modPhpVersion;
  private boolean useSuexec;
  private int packageNum;
  private boolean isShared;
  private boolean useModPerl;
  private int timeout;
  private int maxConcurrency;
  private int monitoringConcurrencyLow;
  private int monitoringConcurrencyMedium;
  private int monitoringConcurrencyHigh;
  private int monitoringConcurrencyCritical;
  private Boolean modAccessCompat;
  private Boolean modActions;
  private Boolean modAlias;
  private Boolean modAuthBasic;
  private Boolean modAuthnCore;
  private Boolean modAuthnFile;
  private Boolean modAuthzCore;
  private Boolean modAuthzGroupfile;
  private Boolean modAuthzHost;
  private Boolean modAuthzUser;
  private Boolean modAutoindex;
  private Boolean modBrotli;
  private Boolean modDeflate;
  private Boolean modDir;
  private Boolean modFilter;
  private Boolean modHeaders;
  private Boolean modHttp2;
  private Boolean modInclude;
  private Boolean modJk;
  private Boolean modLogConfig;
  private Boolean modMime;
  private Boolean modMimeMagic;
  private Boolean modNegotiation;
  private Boolean modProxy;
  private Boolean modProxyHttp;
  private Boolean modProxyHttp2;
  private Boolean modReqtimeout;
  private Boolean modRewrite;
  private Boolean modSetenvif;
  private Boolean modSocacheShmcb;
  private Boolean modSsl;
  private Boolean modStatus;
  private Boolean modWsgi;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public HttpdServer() {
    // Do nothing
  }

  public boolean canAddSites() {
    return canAddSites;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_AO_SERVER:
        return aoServer;
      case 2:
        return name;
      case 3:
        return canAddSites;
      case 4:
        return linuxServerAccount;
      case 5:
        return linuxServerGroup;
      case 6:
        return (modPhpVersion == -1) ? null : modPhpVersion;
      case 7:
        return useSuexec;
      case COLUMN_PACKAGE:
        return packageNum;
      case 9:
        return isShared;
      case 10:
        return useModPerl;
      case 11:
        return timeout;
      case 12:
        return maxConcurrency;
      case 13:
        return (monitoringConcurrencyLow == -1) ? null : monitoringConcurrencyLow;
      case 14:
        return (monitoringConcurrencyMedium == -1) ? null : monitoringConcurrencyMedium;
      case 15:
        return (monitoringConcurrencyHigh == -1) ? null : monitoringConcurrencyHigh;
      case 16:
        return (monitoringConcurrencyCritical == -1) ? null : monitoringConcurrencyCritical;
      case 17:
        return modAccessCompat;
      case 18:
        return modActions;
      case 19:
        return modAlias;
      case 20:
        return modAuthBasic;
      case 21:
        return modAuthnCore;
      case 22:
        return modAuthnFile;
      case 23:
        return modAuthzCore;
      case 24:
        return modAuthzGroupfile;
      case 25:
        return modAuthzHost;
      case 26:
        return modAuthzUser;
      case 27:
        return modAutoindex;
      case 28:
        return modBrotli;
      case 29:
        return modDeflate;
      case 30:
        return modDir;
      case 31:
        return modFilter;
      case 32:
        return modHeaders;
      case 33:
        return modHttp2;
      case 34:
        return modInclude;
      case 35:
        return modJk;
      case 36:
        return modLogConfig;
      case 37:
        return modMime;
      case 38:
        return modMimeMagic;
      case 39:
        return modNegotiation;
      case 40:
        return modProxy;
      case 41:
        return modProxyHttp;
      case 42:
        return modProxyHttp2;
      case 43:
        return modReqtimeout;
      case 44:
        return modRewrite;
      case 45:
        return modSetenvif;
      case 46:
        return modSocacheShmcb;
      case 47:
        return modSsl;
      case 48:
        return modStatus;
      case 49:
        return modWsgi;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public List<HttpdBind> getHttpdBinds() throws IOException, SQLException {
    return table.getConnector().getWeb().getHttpdBind().getHttpdBinds(this);
  }

  public List<Site> getHttpdSites() throws IOException, SQLException {
    return table.getConnector().getWeb().getSite().getHttpdSites(this);
  }

  public List<Worker> getHttpdWorkers() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getWorker().getHttpdWorkers(this);
  }

  public int getLinuxServerAccount_pkey() {
    return linuxServerAccount;
  }

  public UserServer getLinuxServerAccount() throws SQLException, IOException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(linuxServerAccount);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + linuxServerAccount);
    }
    return lsa;
  }

  public int getLinuxServerGroup_pkey() {
    return linuxServerGroup;
  }

  public GroupServer getLinuxServerGroup() throws SQLException, IOException {
    GroupServer lsg = table.getConnector().getLinux().getGroupServer().get(linuxServerGroup);
    if (lsg == null) {
      throw new SQLException("Unable to find LinuxServerGroup: " + linuxServerGroup);
    }
    return lsg;
  }

  public SoftwareVersion getModPhpVersion() throws SQLException, IOException {
    if (modPhpVersion == -1) {
      return null;
    }
    SoftwareVersion tv = table.getConnector().getDistribution().getSoftwareVersion().get(modPhpVersion);
    if (tv == null) {
      throw new SQLException("Unable to find TechnologyVersion: " + modPhpVersion);
    }
    if (
        tv.getOperatingSystemVersion(table.getConnector()).getPkey()
            != getLinuxServer().getHost().getOperatingSystemVersion_id()
    ) {
      throw new SQLException("mod_php/operating system version mismatch on HttpdServer: #" + pkey);
    }
    return tv;
  }

  public boolean useSuexec() {
    return useSuexec;
  }

  public Package getPackage() throws IOException, SQLException {
    // Package may be filtered
    return table.getConnector().getBilling().getPackage().get(packageNum);
  }

  public boolean isShared() {
    return isShared;
  }

  public boolean useModPerl() {
    return useModPerl;
  }

  /**
   * Gets the timeout value in seconds.
   */
  public int getTimeOut() {
    return timeout;
  }

  /**
   * Gets the maximum concurrency of this server (number of children processes/threads).
   */
  public int getMaxConcurrency() {
    return maxConcurrency;
  }

  /**
   * Gets the concurrency that is considered a low-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyLow() {
    return monitoringConcurrencyLow;
  }

  /**
   * Gets the concurrency that is considered a medium-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyMedium() {
    return monitoringConcurrencyMedium;
  }

  /**
   * Gets the concurrency that is considered a high-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyHigh() {
    return monitoringConcurrencyHigh;
  }

  /**
   * Gets the concurrency that is considered a critical-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   * This is the level that will alert people 24x7.
   */
  public int getMonitoringConcurrencyCritical() {
    return monitoringConcurrencyCritical;
  }

  /**
   * Gets the name of the httpd server instance.  The default instance has a null name.
   * Additional instances will have non-empty names.
   * The name is unique per server, including only one default instance.
   *
   * @see #getSystemdEscapedName()
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the <a href="https://www.freedesktop.org/software/systemd/man/systemd.unit.html">systemd-encoded</a>
   * name of the httpd server instance.  The default instance has a null name.
   * Additional instances will have non-empty names.
   * The name is unique per server, including only one default instance.
   *
   * @see #getName()
   * @see SystemdUtil#encode(java.lang.String)
   */
  public String getSystemdEscapedName() {
    return SystemdUtil.encode(name);
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server obj = table.getConnector().getLinux().getServer().get(aoServer);
    if (obj == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return obj;
  }

  public Boolean getModAccessCompat() {
    return modAccessCompat;
  }

  public Boolean getModActions() {
    return modActions;
  }

  public Boolean getModAlias() {
    return modAlias;
  }

  public Boolean getModAuthBasic() {
    return modAuthBasic;
  }

  public Boolean getModAuthnCore() {
    return modAuthnCore;
  }

  public Boolean getModAuthnFile() {
    return modAuthnFile;
  }

  public Boolean getModAuthzCore() {
    return modAuthzCore;
  }

  public Boolean getModAuthzGroupfile() {
    return modAuthzGroupfile;
  }

  public Boolean getModAuthzHost() {
    return modAuthzHost;
  }

  public Boolean getModAuthzUser() {
    return modAuthzUser;
  }

  public Boolean getModAutoindex() {
    return modAutoindex;
  }

  public Boolean getModBrotli() {
    return modBrotli;
  }

  public Boolean getModDeflate() {
    return modDeflate;
  }

  public Boolean getModDir() {
    return modDir;
  }

  public Boolean getModFilter() {
    return modFilter;
  }

  public Boolean getModHeaders() {
    return modHeaders;
  }

  public Boolean getModHttp2() {
    return modHttp2;
  }

  public Boolean getModInclude() {
    return modInclude;
  }

  public Boolean getModJk() {
    return modJk;
  }

  public Boolean getModLogConfig() {
    return modLogConfig;
  }

  public Boolean getModMime() {
    return modMime;
  }

  public Boolean getModMimeMagic() {
    return modMimeMagic;
  }

  public Boolean getModNegotiation() {
    return modNegotiation;
  }

  public Boolean getModProxy() {
    return modProxy;
  }

  public Boolean getModProxyHttp() {
    return modProxyHttp;
  }

  public Boolean getModProxyHttp2() {
    return modProxyHttp2;
  }

  public Boolean getModReqtimeout() {
    return modReqtimeout;
  }

  public Boolean getModRewrite() {
    return modRewrite;
  }

  public Boolean getModSetenvif() {
    return modSetenvif;
  }

  public Boolean getModSocacheShmcb() {
    return modSocacheShmcb;
  }

  public Boolean getModSsl() {
    return modSsl;
  }

  public Boolean getModStatus() {
    return modStatus;
  }

  public Boolean getModWsgi() {
    return modWsgi;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_SERVERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    aoServer = result.getInt(pos++);
    name = result.getString(pos++);
    canAddSites = result.getBoolean(pos++);
    linuxServerAccount = result.getInt(pos++);
    linuxServerGroup = result.getInt(pos++);
    modPhpVersion = result.getInt(pos++);
    if (result.wasNull()) {
      modPhpVersion = -1;
    }
    useSuexec = result.getBoolean(pos++);
    packageNum = result.getInt(pos++);
    isShared = result.getBoolean(pos++);
    useModPerl = result.getBoolean(pos++);
    timeout = result.getInt(pos++);
    maxConcurrency = result.getInt(pos++);
    monitoringConcurrencyLow = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringConcurrencyLow = -1;
    }
    monitoringConcurrencyMedium = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringConcurrencyMedium = -1;
    }
    monitoringConcurrencyHigh = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringConcurrencyHigh = -1;
    }
    monitoringConcurrencyCritical = result.getInt(pos++);
    if (result.wasNull()) {
      monitoringConcurrencyCritical = -1;
    }
    modAccessCompat = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAccessCompat = null;
    }
    modActions = result.getBoolean(pos++);
    if (result.wasNull()) {
      modActions = null;
    }
    modAlias = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAlias = null;
    }
    modAuthBasic = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthBasic = null;
    }
    modAuthnCore = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthnCore = null;
    }
    modAuthnFile = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthnFile = null;
    }
    modAuthzCore = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthzCore = null;
    }
    modAuthzGroupfile = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthzGroupfile = null;
    }
    modAuthzHost = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthzHost = null;
    }
    modAuthzUser = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAuthzUser = null;
    }
    modAutoindex = result.getBoolean(pos++);
    if (result.wasNull()) {
      modAutoindex = null;
    }
    modBrotli = result.getBoolean(pos++);
    if (result.wasNull()) {
      modBrotli = null;
    }
    modDeflate = result.getBoolean(pos++);
    if (result.wasNull()) {
      modDeflate = null;
    }
    modDir = result.getBoolean(pos++);
    if (result.wasNull()) {
      modDir = null;
    }
    modFilter = result.getBoolean(pos++);
    if (result.wasNull()) {
      modFilter = null;
    }
    modHeaders = result.getBoolean(pos++);
    if (result.wasNull()) {
      modHeaders = null;
    }
    modHttp2 = result.getBoolean(pos++);
    if (result.wasNull()) {
      modHttp2 = null;
    }
    modInclude = result.getBoolean(pos++);
    if (result.wasNull()) {
      modInclude = null;
    }
    modJk = result.getBoolean(pos++);
    if (result.wasNull()) {
      modJk = null;
    }
    modLogConfig = result.getBoolean(pos++);
    if (result.wasNull()) {
      modLogConfig = null;
    }
    modMime = result.getBoolean(pos++);
    if (result.wasNull()) {
      modMime = null;
    }
    modMimeMagic = result.getBoolean(pos++);
    if (result.wasNull()) {
      modMimeMagic = null;
    }
    modNegotiation = result.getBoolean(pos++);
    if (result.wasNull()) {
      modNegotiation = null;
    }
    modProxy = result.getBoolean(pos++);
    if (result.wasNull()) {
      modProxy = null;
    }
    modProxyHttp = result.getBoolean(pos++);
    if (result.wasNull()) {
      modProxyHttp = null;
    }
    modProxyHttp2 = result.getBoolean(pos++);
    if (result.wasNull()) {
      modProxyHttp2 = null;
    }
    modReqtimeout = result.getBoolean(pos++);
    if (result.wasNull()) {
      modReqtimeout = null;
    }
    modRewrite = result.getBoolean(pos++);
    if (result.wasNull()) {
      modRewrite = null;
    }
    modSetenvif = result.getBoolean(pos++);
    if (result.wasNull()) {
      modSetenvif = null;
    }
    modSocacheShmcb = result.getBoolean(pos++);
    if (result.wasNull()) {
      modSocacheShmcb = null;
    }
    modSsl = result.getBoolean(pos++);
    if (result.wasNull()) {
      modSsl = null;
    }
    modStatus = result.getBoolean(pos++);
    if (result.wasNull()) {
      modStatus = null;
    }
    modWsgi = result.getBoolean(pos++);
    if (result.wasNull()) {
      modWsgi = null;
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    aoServer = in.readCompressedInt();
    name = in.readNullUTF();
    canAddSites = in.readBoolean();
    linuxServerAccount = in.readCompressedInt();
    linuxServerGroup = in.readCompressedInt();
    modPhpVersion = in.readCompressedInt();
    useSuexec = in.readBoolean();
    packageNum = in.readCompressedInt();
    isShared = in.readBoolean();
    useModPerl = in.readBoolean();
    timeout = in.readCompressedInt();
    maxConcurrency = in.readCompressedInt();
    monitoringConcurrencyLow = in.readCompressedInt();
    monitoringConcurrencyMedium = in.readCompressedInt();
    monitoringConcurrencyHigh = in.readCompressedInt();
    monitoringConcurrencyCritical = in.readCompressedInt();
    modAccessCompat = in.readNullBoolean();
    modActions = in.readNullBoolean();
    modAlias = in.readNullBoolean();
    modAuthBasic = in.readNullBoolean();
    modAuthnCore = in.readNullBoolean();
    modAuthnFile = in.readNullBoolean();
    modAuthzCore = in.readNullBoolean();
    modAuthzGroupfile = in.readNullBoolean();
    modAuthzHost = in.readNullBoolean();
    modAuthzUser = in.readNullBoolean();
    modAutoindex = in.readNullBoolean();
    modBrotli = in.readNullBoolean();
    modDeflate = in.readNullBoolean();
    modDir = in.readNullBoolean();
    modFilter = in.readNullBoolean();
    modHeaders = in.readNullBoolean();
    modHttp2 = in.readNullBoolean();
    modInclude = in.readNullBoolean();
    modJk = in.readNullBoolean();
    modLogConfig = in.readNullBoolean();
    modMime = in.readNullBoolean();
    modMimeMagic = in.readNullBoolean();
    modNegotiation = in.readNullBoolean();
    modProxy = in.readNullBoolean();
    modProxyHttp = in.readNullBoolean();
    modProxyHttp2 = in.readNullBoolean();
    modReqtimeout = in.readNullBoolean();
    modRewrite = in.readNullBoolean();
    modSetenvif = in.readNullBoolean();
    modSocacheShmcb = in.readNullBoolean();
    modSsl = in.readNullBoolean();
    modStatus = in.readNullBoolean();
    modWsgi = in.readNullBoolean();
  }

  @Override
  public String toStringImpl() {
    return name == null ? "httpd" : ("httpd(" + name + ')');
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(aoServer);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_8) < 0) {
      out.writeCompressedInt(name == null ? 1 : Integer.parseInt(name));
    } else {
      out.writeNullUTF(name);
    }
    out.writeBoolean(canAddSites);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) <= 0) {
      out.writeBoolean(true); // is_mod_jk
      out.writeCompressedInt(128); // max_binds
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_102) >= 0) {
      out.writeCompressedInt(linuxServerAccount);
      out.writeCompressedInt(linuxServerGroup);
      out.writeCompressedInt(modPhpVersion);
      out.writeBoolean(useSuexec);
      out.writeCompressedInt(packageNum);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122) <= 0) {
        out.writeCompressedInt(-1);
      }
      out.writeBoolean(isShared);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_103) >= 0) {
      out.writeBoolean(useModPerl);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130) >= 0) {
      out.writeCompressedInt(timeout);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_68) >= 0) {
      out.writeCompressedInt(maxConcurrency);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_11) >= 0) {
      out.writeCompressedInt(monitoringConcurrencyLow);
      out.writeCompressedInt(monitoringConcurrencyMedium);
      out.writeCompressedInt(monitoringConcurrencyHigh);
      out.writeCompressedInt(monitoringConcurrencyCritical);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_7) >= 0) {
      out.writeNullBoolean(modAccessCompat);
      out.writeNullBoolean(modActions);
      out.writeNullBoolean(modAlias);
      out.writeNullBoolean(modAuthBasic);
      out.writeNullBoolean(modAuthnCore);
      out.writeNullBoolean(modAuthnFile);
      out.writeNullBoolean(modAuthzCore);
      out.writeNullBoolean(modAuthzGroupfile);
      out.writeNullBoolean(modAuthzHost);
      out.writeNullBoolean(modAuthzUser);
      out.writeNullBoolean(modAutoindex);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_92_1) >= 0) {
        out.writeNullBoolean(modBrotli);
      }
      out.writeNullBoolean(modDeflate);
      out.writeNullBoolean(modDir);
      out.writeNullBoolean(modFilter);
      out.writeNullBoolean(modHeaders);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_92_1) >= 0) {
        out.writeNullBoolean(modHttp2);
      }
      out.writeNullBoolean(modInclude);
      out.writeNullBoolean(modJk);
      out.writeNullBoolean(modLogConfig);
      out.writeNullBoolean(modMime);
      out.writeNullBoolean(modMimeMagic);
      out.writeNullBoolean(modNegotiation);
      out.writeNullBoolean(modProxy);
      out.writeNullBoolean(modProxyHttp);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_92_1) >= 0) {
        out.writeNullBoolean(modProxyHttp2);
      }
      out.writeNullBoolean(modReqtimeout);
      out.writeNullBoolean(modRewrite);
      out.writeNullBoolean(modSetenvif);
      out.writeNullBoolean(modSocacheShmcb);
      out.writeNullBoolean(modSsl);
      out.writeNullBoolean(modStatus);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_10) >= 0) {
      out.writeNullBoolean(modWsgi);
    }
  }

  /**
   * Gets the current concurrency of this HTTP server.
   */
  public int getConcurrency() throws IOException, SQLException {
    return table.getConnector().requestIntQuery(
        true,
        AoservProtocol.CommandId.GET_HTTPD_SERVER_CONCURRENCY,
        pkey
    );
  }
}
