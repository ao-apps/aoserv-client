/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.util.SystemdUtil;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * An <code>SendmailServer</code> represents one running instance of Sendmail.
 *
 * @see  SendmailBind
 *
 * @author  AO Industries, Inc.
 */
public final class SendmailServer extends CachedObjectIntegerKey<SendmailServer> {

  static final int COLUMN_ID = 0;
  static final int COLUMN_AO_SERVER = 1;
  static final int COLUMN_PACKAGE = 3;
  static final int COLUMN_SERVER_CERTIFICATE = 5;
  static final int COLUMN_CLIENT_CERTIFICATE = 6;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_NAME_name = "name";

  /**
   * Default value for sendmail_servers.allow_plaintext_auth
   */
  public static final boolean DEFAULT_ALLOW_PLAINTEXT_AUTH = false;

  /**
   * Default value for sendmail_servers.max_queue_children
   */
  public static final int DEFAULT_MAX_QUEUE_CHILDREN = 100;

  /**
   * Default value for sendmail_servers.nice_queue_run
   */
  public static final int DEFAULT_NICE_QUEUE_RUN = 10;

  /**
   * Default value for sendmail_servers.delay_la
   */
  public static final int DEFAULT_DELAY_LA = 40;

  /**
   * Default value for sendmail_servers.queue_la
   */
  public static final int DEFAULT_QUEUE_LA = 50;

  /**
   * Default value for sendmail_servers.refuse_la
   */
  public static final int DEFAULT_REFUSE_LA = 80;

  /**
   * Default value for sendmail_servers.max_daemon_children
   */
  public static final int DEFAULT_MAX_DAEMON_CHILDREN = 1000;

  /**
   * Default value for sendmail_servers.bad_rcpt_throttle
   */
  public static final int DEFAULT_BAD_RCPT_THROTTLE = 10;

  /**
   * Default value for sendmail_servers.connection_rate_throttle
   */
  public static final int DEFAULT_CONNECTION_RATE_THROTTLE = 100;

  /**
   * Default value for sendmail_servers.max_message_size
   */
  public static final int DEFAULT_MAX_MESSAGE_SIZE = 100000000;

  /**
   * Default value for sendmail_servers.min_free_blocks
   */
  public static final int DEFAULT_MIN_FREE_BLOCKS = 65536;

  private int linuxServer_server_id;
  private String name;
  private int packageNum;
  private DomainName hostname;
  private int serverCertificate;
  private int clientCertificate;
  private boolean allowPlaintextAuth;
  private int maxQueueChildren;
  private int niceQueueRun;
  private int delayLa;
  private int queueLa;
  private int refuseLa;
  private int maxDaemonChildren;
  private int badRcptThrottle;
  private int connectionRateThrottle;
  private int maxMessageSize;
  private int minFreeBlocks;
  private int clientAddrInet;
  private int clientAddrInet6;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public SendmailServer() {
    // Do nothing
  }

  @Override
  public String toStringImpl() {
    return name == null ? "sendmail" : ("sendmail(" + name + ')');
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return pkey;
      case COLUMN_AO_SERVER:
        return linuxServer_server_id;
      case 2:
        return name;
      case COLUMN_PACKAGE:
        return packageNum;
      case 4:
        return hostname;
      case COLUMN_SERVER_CERTIFICATE:
        return serverCertificate;
      case COLUMN_CLIENT_CERTIFICATE:
        return clientCertificate;
      case 7:
        return allowPlaintextAuth;
      case 8:
        return maxQueueChildren == -1 ? null : maxQueueChildren;
      case 9:
        return niceQueueRun == -1 ? null : niceQueueRun;
      case 10:
        return delayLa == -1 ? null : delayLa;
      case 11:
        return queueLa == -1 ? null : queueLa;
      case 12:
        return refuseLa == -1 ? null : refuseLa;
      case 13:
        return maxDaemonChildren == -1 ? null : maxDaemonChildren;
      case 14:
        return badRcptThrottle == -1 ? null : badRcptThrottle;
      case 15:
        return connectionRateThrottle == -1 ? null : connectionRateThrottle;
      case 16:
        return maxMessageSize == -1 ? null : maxMessageSize;
      case 17:
        return minFreeBlocks == -1 ? null : minFreeBlocks;
      case 18:
        return clientAddrInet == -1 ? null : clientAddrInet;
      case 19:
        return clientAddrInet6 == -1 ? null : clientAddrInet6;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.SENDMAIL_SERVERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      linuxServer_server_id = result.getInt(pos++);
      name = result.getString(pos++);
      packageNum = result.getInt(pos++);
      hostname = DomainName.valueOf(result.getString(pos++));
      serverCertificate = result.getInt(pos++);
      clientCertificate = result.getInt(pos++);
      allowPlaintextAuth = result.getBoolean(pos++);
      maxQueueChildren = result.getInt(pos++);
      if (result.wasNull()) {
        maxQueueChildren = -1;
      }
      niceQueueRun = result.getInt(pos++);
      if (result.wasNull()) {
        niceQueueRun = -1;
      }
      delayLa = result.getInt(pos++);
      if (result.wasNull()) {
        delayLa = -1;
      }
      queueLa = result.getInt(pos++);
      if (result.wasNull()) {
        queueLa = -1;
      }
      refuseLa = result.getInt(pos++);
      if (result.wasNull()) {
        refuseLa = -1;
      }
      maxDaemonChildren = result.getInt(pos++);
      if (result.wasNull()) {
        maxDaemonChildren = -1;
      }
      badRcptThrottle = result.getInt(pos++);
      if (result.wasNull()) {
        badRcptThrottle = -1;
      }
      connectionRateThrottle = result.getInt(pos++);
      if (result.wasNull()) {
        connectionRateThrottle = -1;
      }
      maxMessageSize = result.getInt(pos++);
      if (result.wasNull()) {
        maxMessageSize = -1;
      }
      minFreeBlocks = result.getInt(pos++);
      if (result.wasNull()) {
        minFreeBlocks = -1;
      }
      clientAddrInet = result.getInt(pos++);
      if (result.wasNull()) {
        clientAddrInet = -1;
      }
      clientAddrInet6 = result.getInt(pos++);
      if (result.wasNull()) {
        clientAddrInet6 = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      linuxServer_server_id = in.readCompressedInt();
      name = in.readNullUTF();
      packageNum = in.readCompressedInt();
      hostname = DomainName.valueOf(in.readNullUTF());
      serverCertificate = in.readCompressedInt();
      clientCertificate = in.readCompressedInt();
      allowPlaintextAuth = in.readBoolean();
      maxQueueChildren = in.readCompressedInt();
      niceQueueRun = in.readCompressedInt();
      delayLa = in.readCompressedInt();
      queueLa = in.readCompressedInt();
      refuseLa = in.readCompressedInt();
      maxDaemonChildren = in.readCompressedInt();
      badRcptThrottle = in.readCompressedInt();
      connectionRateThrottle = in.readCompressedInt();
      maxMessageSize = in.readCompressedInt();
      minFreeBlocks = in.readCompressedInt();
      clientAddrInet = in.readCompressedInt();
      clientAddrInet6 = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(linuxServer_server_id);
    out.writeNullUTF(name);
    out.writeCompressedInt(packageNum);
    out.writeNullUTF(Objects.toString(hostname, null));
    out.writeCompressedInt(serverCertificate);
    out.writeCompressedInt(clientCertificate);
    out.writeBoolean(allowPlaintextAuth);
    out.writeCompressedInt(maxQueueChildren);
    out.writeCompressedInt(niceQueueRun);
    out.writeCompressedInt(delayLa);
    out.writeCompressedInt(queueLa);
    out.writeCompressedInt(refuseLa);
    out.writeCompressedInt(maxDaemonChildren);
    out.writeCompressedInt(badRcptThrottle);
    out.writeCompressedInt(connectionRateThrottle);
    out.writeCompressedInt(maxMessageSize);
    out.writeCompressedInt(minFreeBlocks);
    out.writeCompressedInt(clientAddrInet);
    out.writeCompressedInt(clientAddrInet6);
  }

  public int getId() {
    return pkey;
  }

  public int getLinuxServer_server_id() {
    return linuxServer_server_id;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server obj = table.getConnector().getLinux().getServer().get(linuxServer_server_id);
    if (obj == null) {
      throw new SQLException("Unable to find linux.Server: " + linuxServer_server_id);
    }
    return obj;
  }

  /**
   * Gets the name of the sendmail server instance.  The default instance has a null name.
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
   * name of the sendmail server instance.  The default instance has a null name.
   * Additional instances will have non-empty names.
   * The name is unique per server, including only one default instance.
   *
   * @see #getName()
   * @see SystemdUtil#encode(java.lang.String)
   */
  public String getSystemdEscapedName() {
    return SystemdUtil.encode(name);
  }

  public int getPackage_pkey() {
    return packageNum;
  }

  public Package getPackage() throws IOException, SQLException {
    // Package may be filtered
    return table.getConnector().getBilling().getPackage().get(packageNum);
  }

  /**
   * The fully qualified hostname for <code>Dw</code>, <code>Dm</code>, and <code>Dj</code>.
   * <p>
   * When {@code null}, defaults to {@link Server#getHostname()}.
   * </p>
   */
  public DomainName getHostname() {
    return hostname;
  }

  public int getServerCertificate_pkey() {
    return serverCertificate;
  }

  /**
   * Gets the server certificate for this server.
   *
   * @return  the server SSL certificate or {@code null} when filtered
   */
  public Certificate getServerCertificate() throws SQLException, IOException {
    // May be filtered
    return table.getConnector().getPki().getCertificate().get(serverCertificate);
  }

  public int getClientCertificate_pkey() {
    return clientCertificate;
  }

  /**
   * Gets the client certificate for this server.
   *
   * @return  the client SSL certificate or {@code null} when filtered
   */
  public Certificate getClientCertificate() throws SQLException, IOException {
    // May be filtered
    return table.getConnector().getPki().getCertificate().get(clientCertificate);
  }

  /**
   * Allows plaintext authentication (PLAIN/LOGIN) on non-TLS links.
   * When enabled, removes "p" from AuthOptions.
   */
  public boolean getAllowPlaintextAuth() {
    return allowPlaintextAuth;
  }

  /**
   * The value for <code>confMAX_QUEUE_CHILDREN</code> or {@code -1} if not set.
   */
  public int getMaxQueueChildren() {
    return maxQueueChildren;
  }

  /**
   * The value for <code>confNICE_QUEUE_RUN</code> or {@code -1} if not set.
   */
  public int getNiceQueueRun() {
    return niceQueueRun;
  }

  /**
   * The value for <code>confDELAY_LA</code> or {@code -1} if not set.
   */
  public int getDelayLa() {
    return delayLa;
  }

  /**
   * The value for <code>confQUEUE_LA</code> or {@code -1} if not set.
   */
  public int getQueueLa() {
    return queueLa;
  }

  /**
   * The value for <code>confREFUSE_LA</code> or {@code -1} if not set.
   */
  public int getRefuseLa() {
    return refuseLa;
  }

  /**
   * The value for <code>confMAX_DAEMON_CHILDREN</code> or {@code -1} if not set.
   */
  public int getMaxDaemonChildren() {
    return maxDaemonChildren;
  }

  /**
   * The value for <code>confBAD_RCPT_THROTTLE</code> or {@code -1} if not set.
   */
  public int getBadRcptThrottle() {
    return badRcptThrottle;
  }

  /**
   * The value for <code>confCONNECTION_RATE_THROTTLE</code> or {@code -1} if not set.
   */
  public int getConnectionRateThrottle() {
    return connectionRateThrottle;
  }

  /**
   * The value for <code>confMAX_MESSAGE_SIZE</code> or {@code -1} if not set.
   */
  public int getMaxMessageSize() {
    return maxMessageSize;
  }

  /**
   * The value for <code>confMIN_FREE_BLOCKS</code> or {@code -1} if not set.
   */
  public int getMinFreeBlocks() {
    return minFreeBlocks;
  }

  public Integer getClientAddrInet_id() {
    return clientAddrInet == -1 ? null : clientAddrInet;
  }

  /**
   * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet</code> or {@code null} if not set.
   */
  @SuppressWarnings("deprecation")
  public IpAddress getClientAddrInet() throws IOException, SQLException {
    if (clientAddrInet == -1) {
      return null;
    }
    IpAddress obj = table.getConnector().getNet().getIpAddress().get(clientAddrInet);
    if (obj == null) {
      throw new SQLException("Unable to find IpAddress: " + clientAddrInet);
    }
    InetAddress address = obj.getInetAddress();
    com.aoapps.net.AddressFamily family = address.getAddressFamily();
    if (family != com.aoapps.net.AddressFamily.INET) {
      throw new SQLException("Unexpected address family for clientAddrInet #" + clientAddrInet + ": " + family);
    }
    if (address.isUnspecified()) {
      throw new SQLException("May not use unspecified address for clientAddrInet #" + clientAddrInet);
    }
    if (!getLinuxServer().getHost().equals(obj.getDevice().getHost())) {
      throw new SQLException("IpAddress is not on this server for clientAddrInet #" + clientAddrInet);
    }
    return obj;
  }

  public Integer getClientAddrInet6_id() {
    return clientAddrInet6 == -1 ? null : clientAddrInet6;
  }

  /**
   * The <code>Addr</code> for <code>ClientPortOptions</code> with <code>Family=inet6</code> or {@code null} if not set.
   */
  @SuppressWarnings("deprecation")
  public IpAddress getClientAddrInet6() throws IOException, SQLException {
    if (clientAddrInet6 == -1) {
      return null;
    }
    IpAddress obj = table.getConnector().getNet().getIpAddress().get(clientAddrInet6);
    if (obj == null) {
      throw new SQLException("Unable to find IpAddress: " + clientAddrInet6);
    }
    InetAddress address = obj.getInetAddress();
    com.aoapps.net.AddressFamily family = address.getAddressFamily();
    if (family != com.aoapps.net.AddressFamily.INET6) {
      throw new SQLException("Unexpected address family for clientAddrInet6 #" + clientAddrInet6 + ": " + family);
    }
    if (address.isUnspecified()) {
      throw new SQLException("May not use unspecified address for clientAddrInet6 #" + clientAddrInet6);
    }
    if (!getLinuxServer().getHost().equals(obj.getDevice().getHost())) {
      throw new SQLException("IpAddress is not on this server for clientAddrInet6 #" + clientAddrInet6);
    }
    return obj;
  }

  public List<SendmailBind> getSendmailBinds() throws IOException, SQLException {
    return table.getConnector().getEmail().getSendmailBind().getSendmailBinds(this);
  }
}
