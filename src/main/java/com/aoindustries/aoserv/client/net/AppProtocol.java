/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.net;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.web.tomcat.JkProtocol;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
public final class AppProtocol extends GlobalObjectStringKey<AppProtocol> {

  static final int COLUMN_PROTOCOL = 0;
  static final String COLUMN_PORT_name = "port";

  public static final String AOSERV_DAEMON = "aoserv-daemon";
  public static final String AOSERV_DAEMON_SSL = "aoserv-daemon-ssl";
  public static final String AOSERV_MASTER = "aoserv-master";
  public static final String AOSERV_MASTER_SSL = "aoserv-master-ssl";
  public static final String AUTH = "auth";
  public static final String CVSPSERVER = "cvspserver";
  public static final String DNS = "DNS";
  public static final String FTP = "FTP";
  public static final String FTP_DATA = "FTP-DATA";
  public static final String HTTP = "HTTP";
  public static final String HTTPS = "HTTPS";
  public static final String HYPERSONIC = "hypersonic";
  public static final String IMAP2 = "IMAP2";
  public static final String JMX = "JMX";
  public static final String JNP = "JNP";
  public static final String MEMCACHED = "memcached";
  public static final String MILTER = "milter";
  public static final String MYSQL = "MySQL";
  public static final String NTALK = "ntalk";
  public static final String POP3 = "POP3";
  public static final String POSTGRESQL = "PostgreSQL";
  public static final String REDIS = "redis";
  public static final String REDIS_CLUSTER = "redis-cluster";
  public static final String REDIS_SENTINEL = "redis-sentinel";
  public static final String RFB = "RFB";
  public static final String RMI = "RMI";
  public static final String SIEVE = "sieve";
  public static final String SIMAP = "SIMAP";
  public static final String SPAMD = "spamd";
  public static final String SPOP3 = "SPOP3";
  public static final String SSH = "SSH";
  public static final String SMTP = "SMTP";
  public static final String SMTPS = "SMTPS";
  public static final String SUBMISSION = "submission";
  public static final String TALK = "talk";
  public static final String TELNET = "Telnet";
  public static final String TOMCAT4_SHUTDOWN = "tomcat4-shutdown";
  public static final String WEBSERVER = "webserver";

  private Port port;
  private String name;
  private boolean isUserService;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public AppProtocol() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PROTOCOL:
        return pkey;
      case 1:
        return port;
      case 2:
        return name;
      case 3:
        return isUserService;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public JkProtocol getHttpdJkProtocol(AoservConnector connector) throws IOException, SQLException {
    return connector.getWeb_tomcat().getJkProtocol().get(pkey);
  }

  public String getName() {
    return name;
  }

  public boolean isUserService() {
    return isUserService;
  }

  public Port getPort() {
    return port;
  }

  /**
   * Gets the unique name of the protocol.
   */
  public String getProtocol() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PROTOCOLS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getString(1);
      int portNum = result.getInt(2);
      name = result.getString(3);
      isUserService = result.getBoolean(4);
      port = Port.valueOf(
          portNum,
          com.aoapps.net.Protocol.valueOf(result.getString(5).toUpperCase(Locale.ROOT))
      );
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readUTF().intern();
      int portNum = in.readCompressedInt();
      name = in.readUTF();
      isUserService = in.readBoolean();
      port = Port.valueOf(
          portNum,
          in.readEnum(com.aoapps.net.Protocol.class)
      );
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeCompressedInt(port.getPort());
    out.writeUTF(name);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_105) >= 0) {
      out.writeBoolean(isUserService);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_0) < 0) {
        out.writeUTF(port.getProtocol().name().toLowerCase(Locale.ROOT));
      } else {
        out.writeEnum(port.getProtocol());
      }
    }
  }
}
