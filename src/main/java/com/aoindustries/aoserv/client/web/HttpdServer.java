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

  static final int
      COLUMN_PKEY = 0,
      COLUMN_AO_SERVER = 1,
      COLUMN_PACKAGE = 8
  ;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_NAME_name = "name";

  private int ao_server;
  private String name;
  private boolean can_add_sites;
  private int linux_server_account;
  private int linux_server_group;
  private int mod_php_version;
  private boolean use_suexec;
  private int packageNum;
  private boolean is_shared;
  private boolean use_mod_perl;
  private int timeout;
  private int max_concurrency;
  private int monitoring_concurrency_low;
  private int monitoring_concurrency_medium;
  private int monitoring_concurrency_high;
  private int monitoring_concurrency_critical;
  private Boolean mod_access_compat;
  private Boolean mod_actions;
  private Boolean mod_alias;
  private Boolean mod_auth_basic;
  private Boolean mod_authn_core;
  private Boolean mod_authn_file;
  private Boolean mod_authz_core;
  private Boolean mod_authz_groupfile;
  private Boolean mod_authz_host;
  private Boolean mod_authz_user;
  private Boolean mod_autoindex;
  private Boolean mod_deflate;
  private Boolean mod_dir;
  private Boolean mod_filter;
  private Boolean mod_headers;
  private Boolean mod_include;
  private Boolean mod_jk;
  private Boolean mod_log_config;
  private Boolean mod_mime;
  private Boolean mod_mime_magic;
  private Boolean mod_negotiation;
  private Boolean mod_proxy;
  private Boolean mod_proxy_http;
  private Boolean mod_reqtimeout;
  private Boolean mod_rewrite;
  private Boolean mod_setenvif;
  private Boolean mod_socache_shmcb;
  private Boolean mod_ssl;
  private Boolean mod_status;
  private Boolean mod_wsgi;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public HttpdServer() {
    // Do nothing
  }

  public boolean canAddSites() {
    return can_add_sites;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_AO_SERVER: return ao_server;
      case 2: return name;
      case 3: return can_add_sites;
      case 4: return linux_server_account;
      case 5: return linux_server_group;
      case 6: return (mod_php_version == -1) ? null : mod_php_version;
      case 7: return use_suexec;
      case COLUMN_PACKAGE: return packageNum;
      case 9: return is_shared;
      case 10: return use_mod_perl;
      case 11: return timeout;
      case 12: return max_concurrency;
      case 13: return (monitoring_concurrency_low == -1) ? null : monitoring_concurrency_low;
      case 14: return (monitoring_concurrency_medium == -1) ? null : monitoring_concurrency_medium;
      case 15: return (monitoring_concurrency_high == -1) ? null : monitoring_concurrency_high;
      case 16: return (monitoring_concurrency_critical == -1) ? null : monitoring_concurrency_critical;
      case 17: return mod_access_compat;
      case 18: return mod_actions;
      case 19: return mod_alias;
      case 20: return mod_auth_basic;
      case 21: return mod_authn_core;
      case 22: return mod_authn_file;
      case 23: return mod_authz_core;
      case 24: return mod_authz_groupfile;
      case 25: return mod_authz_host;
      case 26: return mod_authz_user;
      case 27: return mod_autoindex;
      case 28: return mod_deflate;
      case 29: return mod_dir;
      case 30: return mod_filter;
      case 31: return mod_headers;
      case 32: return mod_include;
      case 33: return mod_jk;
      case 34: return mod_log_config;
      case 35: return mod_mime;
      case 36: return mod_mime_magic;
      case 37: return mod_negotiation;
      case 38: return mod_proxy;
      case 39: return mod_proxy_http;
      case 40: return mod_reqtimeout;
      case 41: return mod_rewrite;
      case 42: return mod_setenvif;
      case 43: return mod_socache_shmcb;
      case 44: return mod_ssl;
      case 45: return mod_status;
      case 46: return mod_wsgi;
      default: throw new IllegalArgumentException("Invalid index: " + i);
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
    return linux_server_account;
  }

  public UserServer getLinuxServerAccount() throws SQLException, IOException {
    UserServer lsa = table.getConnector().getLinux().getUserServer().get(linux_server_account);
    if (lsa == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
    }
    return lsa;
  }

  public int getLinuxServerGroup_pkey() {
    return linux_server_group;
  }

  public GroupServer getLinuxServerGroup() throws SQLException, IOException {
    GroupServer lsg = table.getConnector().getLinux().getGroupServer().get(linux_server_group);
    if (lsg == null) {
      throw new SQLException("Unable to find LinuxServerGroup: " + linux_server_group);
    }
    return lsg;
  }

  public SoftwareVersion getModPhpVersion() throws SQLException, IOException {
    if (mod_php_version == -1) {
      return null;
    }
    SoftwareVersion tv = table.getConnector().getDistribution().getSoftwareVersion().get(mod_php_version);
    if (tv == null) {
      throw new SQLException("Unable to find TechnologyVersion: " + mod_php_version);
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
    return use_suexec;
  }

  public Package getPackage() throws IOException, SQLException {
    // Package may be filtered
    return table.getConnector().getBilling().getPackage().get(packageNum);
  }

  public boolean isShared() {
    return is_shared;
  }

  public boolean useModPERL() {
    return use_mod_perl;
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
    return max_concurrency;
  }

  /**
   * Gets the concurrency that is considered a low-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyLow() {
    return monitoring_concurrency_low;
  }

  /**
   * Gets the concurrency that is considered a medium-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyMedium() {
    return monitoring_concurrency_medium;
  }

  /**
   * Gets the concurrency that is considered a high-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   */
  public int getMonitoringConcurrencyHigh() {
    return monitoring_concurrency_high;
  }

  /**
   * Gets the concurrency that is considered a critical-priority alert or
   * <code>-1</code> if no alert allowed at this level.
   * This is the level that will alert people 24x7.
   */
  public int getMonitoringConcurrencyCritical() {
    return monitoring_concurrency_critical;
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
    Server obj = table.getConnector().getLinux().getServer().get(ao_server);
    if (obj == null) {
      throw new SQLException("Unable to find linux.Server: " + ao_server);
    }
    return obj;
  }

  public Boolean getModAccessCompat() {
    return mod_access_compat;
  }

  public Boolean getModActions() {
    return mod_actions;
  }

  public Boolean getModAlias() {
    return mod_alias;
  }

  public Boolean getModAuthBasic() {
    return mod_auth_basic;
  }

  public Boolean getModAuthnCore() {
    return mod_authn_core;
  }

  public Boolean getModAuthnFile() {
    return mod_authn_file;
  }

  public Boolean getModAuthzCore() {
    return mod_authz_core;
  }

  public Boolean getModAuthzGroupfile() {
    return mod_authz_groupfile;
  }

  public Boolean getModAuthzHost() {
    return mod_authz_host;
  }

  public Boolean getModAuthzUser() {
    return mod_authz_user;
  }

  public Boolean getModAutoindex() {
    return mod_autoindex;
  }

  public Boolean getModDeflate() {
    return mod_deflate;
  }

  public Boolean getModDir() {
    return mod_dir;
  }

  public Boolean getModFilter() {
    return mod_filter;
  }

  public Boolean getModHeaders() {
    return mod_headers;
  }

  public Boolean getModInclude() {
    return mod_include;
  }

  public Boolean getModJk() {
    return mod_jk;
  }

  public Boolean getModLogConfig() {
    return mod_log_config;
  }

  public Boolean getModMime() {
    return mod_mime;
  }

  public Boolean getModMimeMagic() {
    return mod_mime_magic;
  }

  public Boolean getModNegotiation() {
    return mod_negotiation;
  }

  public Boolean getModProxy() {
    return mod_proxy;
  }

  public Boolean getModProxyHttp() {
    return mod_proxy_http;
  }

  public Boolean getModReqtimeout() {
    return mod_reqtimeout;
  }

  public Boolean getModRewrite() {
    return mod_rewrite;
  }

  public Boolean getModSetenvif() {
    return mod_setenvif;
  }

  public Boolean getModSocacheShmcb() {
    return mod_socache_shmcb;
  }

  public Boolean getModSsl() {
    return mod_ssl;
  }

  public Boolean getModStatus() {
    return mod_status;
  }

  public Boolean getModWsgi() {
    return mod_wsgi;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_SERVERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    ao_server = result.getInt(pos++);
    name = result.getString(pos++);
    can_add_sites = result.getBoolean(pos++);
    linux_server_account = result.getInt(pos++);
    linux_server_group = result.getInt(pos++);
    mod_php_version = result.getInt(pos++);
    if (result.wasNull()) {
      mod_php_version = -1;
    }
    use_suexec = result.getBoolean(pos++);
    packageNum = result.getInt(pos++);
    is_shared = result.getBoolean(pos++);
    use_mod_perl = result.getBoolean(pos++);
    timeout = result.getInt(pos++);
    max_concurrency = result.getInt(pos++);
    monitoring_concurrency_low = result.getInt(pos++);
    if (result.wasNull()) {
      monitoring_concurrency_low = -1;
    }
    monitoring_concurrency_medium = result.getInt(pos++);
    if (result.wasNull()) {
      monitoring_concurrency_medium = -1;
    }
    monitoring_concurrency_high = result.getInt(pos++);
    if (result.wasNull()) {
      monitoring_concurrency_high = -1;
    }
    monitoring_concurrency_critical = result.getInt(pos++);
    if (result.wasNull()) {
      monitoring_concurrency_critical = -1;
    }
    mod_access_compat = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_access_compat = null;
    }
    mod_actions = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_actions = null;
    }
    mod_alias = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_alias = null;
    }
    mod_auth_basic = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_auth_basic = null;
    }
    mod_authn_core = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authn_core = null;
    }
    mod_authn_file = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authn_file = null;
    }
    mod_authz_core = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authz_core = null;
    }
    mod_authz_groupfile = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authz_groupfile = null;
    }
    mod_authz_host = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authz_host = null;
    }
    mod_authz_user = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_authz_user = null;
    }
    mod_autoindex = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_autoindex = null;
    }
    mod_deflate = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_deflate = null;
    }
    mod_dir = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_dir = null;
    }
    mod_filter = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_filter = null;
    }
    mod_headers = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_headers = null;
    }
    mod_include = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_include = null;
    }
    mod_jk = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_jk = null;
    }
    mod_log_config = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_log_config = null;
    }
    mod_mime = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_mime = null;
    }
    mod_mime_magic = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_mime_magic = null;
    }
    mod_negotiation = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_negotiation = null;
    }
    mod_proxy = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_proxy = null;
    }
    mod_proxy_http = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_proxy_http = null;
    }
    mod_reqtimeout = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_reqtimeout = null;
    }
    mod_rewrite = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_rewrite = null;
    }
    mod_setenvif = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_setenvif = null;
    }
    mod_socache_shmcb = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_socache_shmcb = null;
    }
    mod_ssl = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_ssl = null;
    }
    mod_status = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_status = null;
    }
    mod_wsgi = result.getBoolean(pos++);
    if (result.wasNull()) {
      mod_wsgi = null;
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    ao_server = in.readCompressedInt();
    name = in.readNullUTF();
    can_add_sites = in.readBoolean();
    linux_server_account = in.readCompressedInt();
    linux_server_group = in.readCompressedInt();
    mod_php_version = in.readCompressedInt();
    use_suexec = in.readBoolean();
    packageNum = in.readCompressedInt();
    is_shared = in.readBoolean();
    use_mod_perl = in.readBoolean();
    timeout = in.readCompressedInt();
    max_concurrency = in.readCompressedInt();
    monitoring_concurrency_low = in.readCompressedInt();
    monitoring_concurrency_medium = in.readCompressedInt();
    monitoring_concurrency_high = in.readCompressedInt();
    monitoring_concurrency_critical = in.readCompressedInt();
    mod_access_compat = in.readNullBoolean();
    mod_actions = in.readNullBoolean();
    mod_alias = in.readNullBoolean();
    mod_auth_basic = in.readNullBoolean();
    mod_authn_core = in.readNullBoolean();
    mod_authn_file = in.readNullBoolean();
    mod_authz_core = in.readNullBoolean();
    mod_authz_groupfile = in.readNullBoolean();
    mod_authz_host = in.readNullBoolean();
    mod_authz_user = in.readNullBoolean();
    mod_autoindex = in.readNullBoolean();
    mod_deflate = in.readNullBoolean();
    mod_dir = in.readNullBoolean();
    mod_filter = in.readNullBoolean();
    mod_headers = in.readNullBoolean();
    mod_include = in.readNullBoolean();
    mod_jk = in.readNullBoolean();
    mod_log_config = in.readNullBoolean();
    mod_mime = in.readNullBoolean();
    mod_mime_magic = in.readNullBoolean();
    mod_negotiation = in.readNullBoolean();
    mod_proxy = in.readNullBoolean();
    mod_proxy_http = in.readNullBoolean();
    mod_reqtimeout = in.readNullBoolean();
    mod_rewrite = in.readNullBoolean();
    mod_setenvif = in.readNullBoolean();
    mod_socache_shmcb = in.readNullBoolean();
    mod_ssl = in.readNullBoolean();
    mod_status = in.readNullBoolean();
    mod_wsgi = in.readNullBoolean();
  }

  @Override
  public String toStringImpl() {
    return name == null ? "httpd" : ("httpd(" + name + ')');
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(ao_server);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_8) < 0) {
      out.writeCompressedInt(name == null ? 1 : Integer.parseInt(name));
    } else {
      out.writeNullUTF(name);
    }
    out.writeBoolean(can_add_sites);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) <= 0) {
      out.writeBoolean(true); // is_mod_jk
      out.writeCompressedInt(128); // max_binds
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_102) >= 0) {
      out.writeCompressedInt(linux_server_account);
      out.writeCompressedInt(linux_server_group);
      out.writeCompressedInt(mod_php_version);
      out.writeBoolean(use_suexec);
      out.writeCompressedInt(packageNum);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122) <= 0) {
        out.writeCompressedInt(-1);
      }
      out.writeBoolean(is_shared);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_103) >= 0) {
      out.writeBoolean(use_mod_perl);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_130) >= 0) {
      out.writeCompressedInt(timeout);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_68) >= 0) {
      out.writeCompressedInt(max_concurrency);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_11) >= 0) {
      out.writeCompressedInt(monitoring_concurrency_low);
      out.writeCompressedInt(monitoring_concurrency_medium);
      out.writeCompressedInt(monitoring_concurrency_high);
      out.writeCompressedInt(monitoring_concurrency_critical);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_7) >= 0) {
      out.writeNullBoolean(mod_access_compat);
      out.writeNullBoolean(mod_actions);
      out.writeNullBoolean(mod_alias);
      out.writeNullBoolean(mod_auth_basic);
      out.writeNullBoolean(mod_authn_core);
      out.writeNullBoolean(mod_authn_file);
      out.writeNullBoolean(mod_authz_core);
      out.writeNullBoolean(mod_authz_groupfile);
      out.writeNullBoolean(mod_authz_host);
      out.writeNullBoolean(mod_authz_user);
      out.writeNullBoolean(mod_autoindex);
      out.writeNullBoolean(mod_deflate);
      out.writeNullBoolean(mod_dir);
      out.writeNullBoolean(mod_filter);
      out.writeNullBoolean(mod_headers);
      out.writeNullBoolean(mod_include);
      out.writeNullBoolean(mod_jk);
      out.writeNullBoolean(mod_log_config);
      out.writeNullBoolean(mod_mime);
      out.writeNullBoolean(mod_mime_magic);
      out.writeNullBoolean(mod_negotiation);
      out.writeNullBoolean(mod_proxy);
      out.writeNullBoolean(mod_proxy_http);
      out.writeNullBoolean(mod_reqtimeout);
      out.writeNullBoolean(mod_rewrite);
      out.writeNullBoolean(mod_setenvif);
      out.writeNullBoolean(mod_socache_shmcb);
      out.writeNullBoolean(mod_ssl);
      out.writeNullBoolean(mod_status);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_10) >= 0) {
      out.writeNullBoolean(mod_wsgi);
    }
  }

  /**
   * Gets the current concurrency of this HTTP server.
   */
  public int getConcurrency() throws IOException, SQLException {
    return table.getConnector().requestIntQuery(
        true,
        AoservProtocol.CommandID.GET_HTTPD_SERVER_CONCURRENCY,
        pkey
    );
  }
}
