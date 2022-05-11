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

package com.aoindustries.aoserv.client.web.tomcat;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdStdTomcatSite</code> indicates that a
 * <code>HttpdTomcatSite</code> is configured in the standard layout
 * of one Tomcat instance per Java virtual machine.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class PrivateTomcatSite extends CachedObjectIntegerKey<PrivateTomcatSite> {

  static final int COLUMN_TOMCAT_SITE = 0;
  static final int COLUMN_TOMCAT4_SHUTDOWN_PORT = 1;
  static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

  /**
   * The default setting of maxPostSize on the &lt;Connector /&gt; in server.xml.
   * This raises the value from the Tomcat default of 2 MiB to a more real-world
   * value, such as allowing uploads of pictures from modern digital cameras.
   *
   * @see  #getMaxPostSize()
   */
  public static final int DEFAULT_MAX_POST_SIZE = 16 * 1024 * 1024; // 16 MiB

  public static final String DEFAULT_TOMCAT_VERSION_PREFIX = Version.VERSION_10_0_PREFIX;

  private int tomcat4ShutdownPort;
  private String tomcat4ShutdownKey;
  private int maxPostSize;
  private boolean unpackWars;
  private boolean autoDeploy;
  private boolean tomcatAuthentication;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public PrivateTomcatSite() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_TOMCAT_SITE:
        return pkey;
      case COLUMN_TOMCAT4_SHUTDOWN_PORT:
        return tomcat4ShutdownPort == -1 ? null : tomcat4ShutdownPort;
      case 2:
        return tomcat4ShutdownKey;
      case 3:
        return maxPostSize == -1 ? null : maxPostSize;
      case 4:
        return unpackWars;
      case 5:
        return autoDeploy;
      case 6:
        return tomcatAuthentication;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Site getHttpdTomcatSite() throws SQLException, IOException {
    Site obj = table.getConnector().getWeb_tomcat().getSite().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + pkey);
    }
    return obj;
  }

  public String getTomcat4ShutdownKey() {
    return tomcat4ShutdownKey;
  }

  /**
   * Gets the max post size or {@code -1} of not limited.
   */
  public int getMaxPostSize() {
    return maxPostSize;
  }

  public void setMaxPostSize(final int maxPostSize) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.SET_HTTPD_TOMCAT_STD_SITE_MAX_POST_SIZE,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeInt(maxPostSize);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
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
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_TOMCAT_STD_SITE_UNPACK_WARS, pkey, unpackWars);
  }

  /**
   * Gets the <code>autoDeploy</code> setting for this Tomcat.
   */
  public boolean getAutoDeploy() {
    return autoDeploy;
  }

  public void setAutoDeploy(boolean autoDeploy) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_TOMCAT_STD_SITE_AUTO_DEPLOY, pkey, autoDeploy);
  }

  /**
   * Gets the <code>tomcatAuthentication</code> setting for this Tomcat.
   */
  public boolean getTomcatAuthentication() {
    return tomcatAuthentication;
  }

  public void setTomcatAuthentication(boolean tomcatAuthentication) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.web_tomcat_PrivateTomcatSite_tomcatAuthentication_set, pkey, tomcatAuthentication);
  }

  public Integer getTomcat4ShutdownPort_id() {
    return (tomcat4ShutdownPort == -1) ? null : tomcat4ShutdownPort;
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

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_STD_SITES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    tomcat4ShutdownPort = result.getInt(2);
    if (result.wasNull()) {
      tomcat4ShutdownPort = -1;
    }
    tomcat4ShutdownKey = result.getString(3);
    maxPostSize = result.getInt(4);
    if (result.wasNull()) {
      maxPostSize = -1;
    }
    unpackWars = result.getBoolean(5);
    autoDeploy = result.getBoolean(6);
    tomcatAuthentication = result.getBoolean(7);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    tomcat4ShutdownPort = in.readCompressedInt();
    tomcat4ShutdownKey = in.readNullUTF();
    maxPostSize = in.readInt();
    unpackWars = in.readBoolean();
    autoDeploy = in.readBoolean();
    tomcatAuthentication = in.readBoolean();
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHttpdTomcatSite().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(tomcat4ShutdownPort);
    out.writeNullUTF(tomcat4ShutdownKey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
      out.writeInt(maxPostSize);
      out.writeBoolean(unpackWars);
      out.writeBoolean(autoDeploy);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_2) >= 0) {
      out.writeBoolean(tomcatAuthentication);
    }
  }

  public void setHttpdTomcatVersion(Version version) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.SET_HTTPD_TOMCAT_STD_SITE_VERSION, pkey, version.getPkey());
  }
}
