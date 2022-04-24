/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.AOPool;
import com.aoapps.lang.AutoCloseables;
import com.aoapps.net.DomainName;
import com.aoapps.net.HostAddress;
import com.aoapps.net.InetAddress;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.account.User;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLSocketFactory;

/**
 * A <code>SSLConnector</code> provides the connection between
 * the client and server over secured SSL sockets.
 *
 * @see  AOServConnector
 *
 * @author  AO Industries, Inc.
 */
public class SSLConnector extends TCPConnector {

  /**
   * The trust store used for this connector.
   */
  private static String trustStorePath;

  /**
   * The password for the trust store.
   */
  private static String trustStorePassword;

  /**
   * The protocol for this connector.
   */
  public static final String SSL_PROTOCOL = "ssl";

  /**
   * Instances of connectors are created once and then reused.
   */
  private static final List<SSLConnector> connectors = new ArrayList<>();

  protected SSLConnector(
      HostAddress hostname,
      InetAddress local_ip,
      Port port,
      User.Name connectAs,
      User.Name authenticateAs,
      String password,
      DomainName daemonServer,
      int poolSize,
      long maxConnectionAge,
      String trustStorePath,
      String trustStorePassword
  ) {
    super(hostname, local_ip, port, connectAs, authenticateAs, password, daemonServer, poolSize, maxConnectionAge);
    if (
        (
            SSLConnector.trustStorePath != null
                && !SSLConnector.trustStorePath.equals(trustStorePath)
        ) || (
            SSLConnector.trustStorePassword != null
                && !SSLConnector.trustStorePassword.equals(trustStorePassword)
        )
    ) {
      throw new IllegalArgumentException(
          "Trust store path and password may only be set once, currently '"
              + SSLConnector.trustStorePath
              + "', trying to set to '"
              + trustStorePath
              + "'"
      );
    }
    if (SSLConnector.trustStorePath == null) {
      SSLConnector.trustStorePath = trustStorePath;
      SSLConnector.trustStorePassword = trustStorePassword;
    }
  }

  @Override
  public String getProtocol() {
    return SSL_PROTOCOL;
  }

  @Override
  @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
  Socket getSocket() throws IOException {
    if (trustStorePath != null && trustStorePath.length() > 0) {
      System.setProperty("javax.net.ssl.trustStore", trustStorePath);
    }
    if (trustStorePassword != null && trustStorePassword.length() > 0) {
      System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    SSLSocketFactory sslFact = (SSLSocketFactory) SSLSocketFactory.getDefault();
    Socket socket = new Socket();
    try {
      socket.setKeepAlive(true);
      socket.setSoLinger(true, AOPool.DEFAULT_SOCKET_SO_LINGER);
      socket.setTcpNoDelay(true);
      if (local_ip != null && !local_ip.isUnspecified()) {
        socket.bind(new InetSocketAddress(local_ip.toString(), 0));
      }
      socket.connect(new InetSocketAddress(hostname.toString(), port.getPort()), AOPool.DEFAULT_CONNECT_TIMEOUT);
      return sslFact.createSocket(socket, hostname.toString(), port.getPort(), true);
    } catch (Throwable t) {
      throw AutoCloseables.closeAndWrap(t, IOException.class, IOException::new, socket);
    }
  }

  public static synchronized SSLConnector getSSLConnector(
      HostAddress hostname,
      InetAddress local_ip,
      Port port,
      User.Name connectAs,
      User.Name authenticateAs,
      String password,
      DomainName daemonServer,
      int poolSize,
      long maxConnectionAge,
      String trustStorePath,
      String trustStorePassword
  ) {
    if (connectAs == null) {
      throw new IllegalArgumentException("connectAs is null");
    }
    if (authenticateAs == null) {
      throw new IllegalArgumentException("authenticateAs is null");
    }
    if (password == null) {
      throw new IllegalArgumentException("password is null");
    }
    int size = connectors.size();
    for (int c = 0; c < size; c++) {
      SSLConnector connector = connectors.get(c);
      if (connector == null) {
        throw new NullPointerException("connector is null");
      }
      if (connector.connectAs == null) {
        throw new NullPointerException("connector.connectAs is null");
      }
      if (connector.authenticateAs == null) {
        throw new NullPointerException("connector.authenticateAs is null");
      }
      if (connector.password == null) {
        throw new NullPointerException("connector.password is null");
      }
      if (
          connector.hostname.equals(hostname)
              && Objects.equals(local_ip, connector.local_ip)
              && connector.port == port
              && connector.connectAs.equals(connectAs)
              && connector.authenticateAs.equals(authenticateAs)
              && connector.password.equals(password)
              && Objects.equals(daemonServer, connector.daemonServer)
              && connector.poolSize == poolSize
              && connector.maxConnectionAge == maxConnectionAge
              && Objects.equals(SSLConnector.trustStorePath, trustStorePath)
              && Objects.equals(SSLConnector.trustStorePassword, trustStorePassword)
      ) {
        return connector;
      }
    }
    SSLConnector newConnector = new SSLConnector(
        hostname,
        local_ip,
        port,
        connectAs,
        authenticateAs,
        password,
        daemonServer,
        poolSize,
        maxConnectionAge,
        trustStorePath,
        trustStorePassword
    );
    connectors.add(newConnector);
    return newConnector;
  }

  @Override
  public boolean isSecure() {
    return true;
  }

  @Override
  public AOServConnector switchUsers(User.Name username) {
    if (username.equals(connectAs)) {
      return this;
    }
    return getSSLConnector(
        hostname,
        local_ip,
        port,
        username,
        authenticateAs,
        password,
        daemonServer,
        poolSize,
        maxConnectionAge,
        trustStorePath,
        trustStorePassword
    );
  }
}
