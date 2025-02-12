/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.web.tomcat;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.PosixPath;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>HttpdSharedTomcat</code> stores configuration information
 * about the Jakarta Tomcat JVM under which run one or more
 * <code>HttpdTomcatSharedSite</code>s.
 *
 * @see  SharedTomcatSite
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class SharedTomcat extends CachedObjectIntegerKey<SharedTomcat> implements Disablable, Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 2;
  static final int COLUMN_LINUX_SERVER_ACCOUNT = 4;
  static final int COLUMN_TOMCAT4_WORKER = 7;
  static final int COLUMN_TOMCAT4_SHUTDOWN_PORT = 8;
  static final String COLUMN_NAME_name = "name";
  static final String COLUMN_AO_SERVER_name = "ao_server";

  /**
   * The default setting of maxParameterCount on the &lt;Connector /&gt; in server.xml.
   * This matches the default of {@code 1000} since Tomcat 8.5.88, 9.0.74, 10.1.8.
   * Previously, the default was {@code 10000}.
   *
   * @see  #getMaxParameterCount()
   */
  public static final int DEFAULT_MAX_PARAMETER_COUNT = 1000;

  /**
   * The default setting of maxPostSize on the &lt;Connector /&gt; in server.xml.
   * This raises the value from the Tomcat default of 2 MiB to a more real-world
   * value, such as allowing uploads of pictures from modern digital cameras.
   *
   * @see  #getMaxPostSize()
   */
  public static final int DEFAULT_MAX_POST_SIZE = 16 * 1024 * 1024; // 16 MiB

  public static final int MAX_NAME_LENGTH = 32;

  public static final String DEFAULT_TOMCAT_VERSION_PREFIX = Version.VERSION_10_1_PREFIX;

  private String name;
  private int aoServer;
  private int version;
  private int linuxServerAccount;
  private int linuxServerGroup;
  private int disableLog;
  private int tomcat4Worker;
  private int tomcat4ShutdownPort;
  private String tomcat4ShutdownKey;
  private boolean isManual;
  private int maxParameterCount;
  private int maxPostSize;
  private boolean unpackWars;
  private boolean autoDeploy;
  private boolean undeployOldVersions;
  private boolean tomcatAuthentication;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SharedTomcat() {
    // Do nothing
  }

  @Override
  public boolean canDisable() {
    return disableLog == -1;
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return
          dl.canEnable()
              && !getLinuxServerGroup().getLinuxGroup().getPackage().isDisabled()
              && !getLinuxServerAccount().isDisabled();
    }
  }

  @Override
  public List<CannotRemoveReason<SharedTomcatSite>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<SharedTomcatSite>> reasons = new ArrayList<>();

    for (SharedTomcatSite htss : getHttpdTomcatSharedSites()) {
      com.aoindustries.aoserv.client.web.Site hs = htss.getHttpdTomcatSite().getHttpdSite();
      reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat website " + hs.getInstallDirectory() + " on " + hs.getLinuxServer().getHostname(), htss));
    }

    return reasons;
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.HTTPD_SHARED_TOMCATS, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.HTTPD_SHARED_TOMCATS, pkey);
  }

  public PosixPath getInstallDirectory() throws SQLException, IOException {
    try {
      return PosixPath.valueOf(
          getLinuxServer().getHost().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
              + '/' + name
      );
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return name;
      case COLUMN_AO_SERVER:
        return aoServer;
      case 3:
        return version;
      case COLUMN_LINUX_SERVER_ACCOUNT:
        return linuxServerAccount;
      case 5:
        return linuxServerGroup;
      case 6:
        return disableLog == -1 ? null : disableLog;
      case COLUMN_TOMCAT4_WORKER:
        return tomcat4Worker == -1 ? null : tomcat4Worker;
      case COLUMN_TOMCAT4_SHUTDOWN_PORT:
        return tomcat4ShutdownPort == -1 ? null : tomcat4ShutdownPort;
      case 9:
        return tomcat4ShutdownKey;
      case 10:
        return isManual;
      case 11:
        return maxParameterCount == -1 ? null : maxParameterCount;
      case 12:
        return maxPostSize == -1 ? null : maxPostSize;
      case 13:
        return unpackWars;
      case 14:
        return autoDeploy;
      case 15:
        return undeployOldVersions;
      case 16:
        return tomcatAuthentication;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
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

  public List<SharedTomcatSite> getHttpdTomcatSharedSites() throws IOException, SQLException {
    return table.getConnector().getWeb_tomcat().getSharedTomcatSite().getHttpdTomcatSharedSites(this);
  }

  public Version getHttpdTomcatVersion() throws SQLException, IOException {
    Version obj = table.getConnector().getWeb_tomcat().getVersion().get(version);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatVersion: " + version);
    }
    if (
        obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
            != getLinuxServer().getHost().getOperatingSystemVersion_id()
    ) {
      throw new SQLException("resource/operating system version mismatch on HttpdSharedTomcat: #" + pkey);
    }
    return obj;
  }

  public void setHttpdTomcatVersion(Version version) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_SHARED_TOMCAT_VERSION, pkey, version.getPkey());
  }

  public int getLinuxServerAccount_pkey() {
    return linuxServerAccount;
  }

  public UserServer getLinuxServerAccount() throws SQLException, IOException {
    UserServer obj = table.getConnector().getLinux().getUserServer().get(linuxServerAccount);
    if (obj == null) {
      throw new SQLException("Unable to find LinuxServerAccount: " + linuxServerAccount);
    }
    return obj;
  }

  public int getLinuxServerGroup_pkey() {
    return linuxServerGroup;
  }

  public GroupServer getLinuxServerGroup() throws SQLException, IOException {
    GroupServer obj = table.getConnector().getLinux().getGroupServer().get(linuxServerGroup);
    if (obj == null) {
      throw new SQLException("Unable to find LinuxServerGroup: " + linuxServerGroup);
    }
    return obj;
  }

  public String getName() {
    return name;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server obj = table.getConnector().getLinux().getServer().get(aoServer);
    if (obj == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_SHARED_TOMCATS;
  }

  public Worker getTomcat4Worker() throws SQLException, IOException {
    if (tomcat4Worker == -1) {
      return null;
    }
    Worker hw = table.getConnector().getWeb_tomcat().getWorker().get(tomcat4Worker);
    if (hw == null) {
      throw new SQLException("Unable to find HttpdWorker: " + tomcat4Worker);
    }
    return hw;
  }

  public String getTomcat4ShutdownKey() {
    return tomcat4ShutdownKey;
  }

  public Bind getTomcat4ShutdownPort() throws IOException, SQLException {
    if (tomcat4ShutdownPort == -1) {
      return null;
    }
    Bind nb = table.getConnector().getNet().getBind().get(tomcat4ShutdownPort);
    if (nb == null) {
      throw new SQLException("Unable to find NetBind: " + tomcat4ShutdownPort);
    }
    return nb;
  }

  public boolean isManual() {
    return isManual;
  }

  public void setIsManual(boolean isManual) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, pkey, isManual);
  }

  /**
   * Gets the <code>maxParameterCount</code> or {@code -1} if not limited.
   */
  public int getMaxParameterCount() {
    return maxParameterCount;
  }

  public void setMaxParameterCount(int maxParameterCount) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.web_tomcat_SharedTomcat_maxParameterCount_set,
        new AoservConnector.UpdateRequestInvalidating(table) {
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeInt(maxParameterCount);
          }
        }
    );
  }

  /**
   * Gets the <code>maxPostSize</code> or {@code -1} if not limited.
   */
  public int getMaxPostSize() {
    return maxPostSize;
  }

  public void setMaxPostSize(final int maxPostSize) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE,
        new AoservConnector.UpdateRequestInvalidating(table) {
          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeInt(maxPostSize);
          }
        }
    );
  }

  /**
   * Gets the <code>unpackWars</code> setting for this Tomcat.
   */
  public boolean getUnpackWars() {
    return unpackWars;
  }

  public void setUnpackWars(boolean unpackWars) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS, pkey, unpackWars);
  }

  /**
   * Gets the <code>autoDeploy</code> setting for this Tomcat.
   */
  public boolean getAutoDeploy() {
    return autoDeploy;
  }

  public void setAutoDeploy(boolean autoDeploy) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY, pkey, autoDeploy);
  }

  /**
   * Gets the <code>undeployOldVersions</code> setting for this Tomcat.
   */
  public boolean getUndeployOldVersions() {
    return undeployOldVersions;
  }

  public void setUndeployOldVersions(boolean undeployOldVersions) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.web_tomcat_SharedTomcat_undeployOldVersions_set, pkey, undeployOldVersions);
  }

  /**
   * Gets the <code>tomcatAuthentication</code> setting for this Tomcat.
   */
  public boolean getTomcatAuthentication() {
    return tomcatAuthentication;
  }

  public void setTomcatAuthentication(boolean tomcatAuthentication) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.web_tomcat_SharedTomcat_tomcatAuthentication_set, pkey, tomcatAuthentication);
  }

  /**
   * Checks the format of the name of the shared Tomcat, as used in the <code>/wwwgroup</code>
   * directory.  The name must be 12 characters or less, and comprised of
   * only <code>a-z</code>,<code>0-9</code>, or <code>-</code>.  The first
   * character must be <code>a-z</code>.
   *
   * <p>Note: This matches the check constraint on the httpd_shared_tomcats table.
   * Note: This matches keepWwwgroupDirs in HttpdSharedTomcatManager.</p>
   *
   * <p>TODO: Self-validating type (Shared site Site.Name validator, and/or PosixPortableFilename?)</p>
   */
  public static boolean isValidSharedTomcatName(String name) {
    // These are the other files/directories that may exist under /www.  To avoid
    // potential conflicts, these may not be used as site names.
    if (
        // Other filesystem patterns
        "lost+found".equals(name)
            || "aquota.group".equals(name)
            || "aquota.user".equals(name)
    ) {
      return false;
    }

    int len = name.length();
    if (len == 0 || len > MAX_NAME_LENGTH) {
      return false;
    }
    // The first character must be [a-z]
    char ch = name.charAt(0);
    if (ch < 'a' || ch > 'z') {
      return false;
    }
    // The rest may have additional characters
    for (int c = 1; c < len; c++) {
      ch = name.charAt(c);
      if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '-') {
        return false;
      }
    }
    return true;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    name = result.getString(pos++);
    aoServer = result.getInt(pos++);
    version = result.getInt(pos++);
    linuxServerAccount = result.getInt(pos++);
    linuxServerGroup = result.getInt(pos++);
    disableLog = result.getInt(pos++);
    if (result.wasNull()) {
      disableLog = -1;
    }
    tomcat4Worker = result.getInt(pos++);
    if (result.wasNull()) {
      tomcat4Worker = -1;
    }
    tomcat4ShutdownPort = result.getInt(pos++);
    if (result.wasNull()) {
      tomcat4ShutdownPort = -1;
    }
    tomcat4ShutdownKey = result.getString(pos++);
    isManual = result.getBoolean(pos++);
    maxParameterCount = result.getInt(pos++);
    if (result.wasNull()) {
      maxParameterCount = -1;
    }
    maxPostSize = result.getInt(pos++);
    if (result.wasNull()) {
      maxPostSize = -1;
    }
    unpackWars = result.getBoolean(pos++);
    autoDeploy = result.getBoolean(pos++);
    undeployOldVersions = result.getBoolean(pos++);
    tomcatAuthentication = result.getBoolean(pos++);
  }

  /**
   * readImpl method comment.
   */
  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    name = in.readUTF();
    aoServer = in.readCompressedInt();
    version = in.readCompressedInt();
    linuxServerAccount = in.readCompressedInt();
    linuxServerGroup = in.readCompressedInt();
    disableLog = in.readCompressedInt();
    tomcat4Worker = in.readCompressedInt();
    tomcat4ShutdownPort = in.readCompressedInt();
    tomcat4ShutdownKey = in.readNullUTF();
    isManual = in.readBoolean();
    maxParameterCount = in.readInt();
    maxPostSize = in.readInt();
    unpackWars = in.readBoolean();
    autoDeploy = in.readBoolean();
    undeployOldVersions = in.readBoolean();
    tomcatAuthentication = in.readBoolean();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.HTTPD_SHARED_TOMCATS, pkey);
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return name + " on " + getLinuxServer().getHostname();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name);
    out.writeCompressedInt(aoServer);
    out.writeCompressedInt(version);
    out.writeCompressedInt(linuxServerAccount);
    out.writeCompressedInt(linuxServerGroup);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) <= 0) {
      out.writeBoolean(false); // is_secure
      out.writeBoolean(false); // is_overflow
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30) <= 0) {
      out.writeShort(0);
      out.writeShort(7);
      out.writeShort(0);
      out.writeShort(7);
      out.writeShort(0);
      out.writeShort(7);
    }
    out.writeCompressedInt(disableLog);
    out.writeCompressedInt(tomcat4Worker);
    out.writeCompressedInt(tomcat4ShutdownPort);
    out.writeNullUTF(tomcat4ShutdownKey);
    out.writeBoolean(isManual);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_92_0) >= 0) {
      out.writeInt(maxParameterCount);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
      out.writeInt(maxPostSize);
      out.writeBoolean(unpackWars);
      out.writeBoolean(autoDeploy);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_92_0) >= 0) {
      out.writeBoolean(undeployOldVersions);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_2) >= 0) {
      out.writeBoolean(tomcatAuthentication);
    }
  }
}
