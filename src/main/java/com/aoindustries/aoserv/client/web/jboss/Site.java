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

package com.aoindustries.aoserv.client.web.jboss;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdJbossSite</code> indicates that an <code>HttpdSite</code>
 * uses JBoss as its servlet engine.  The servlet engine may be
 * configured in several ways, only what is common to every type of
 * JBoss installation is stored in <code>HttpdJbossSite</code>.
 *
 * @see  com.aoindustries.aoserv.client.web.Site
 *
 * @author  AO Industries, Inc.
 */

public final class Site extends CachedObjectIntegerKey<Site> {

  static final int COLUMN_TOMCAT_SITE = 0;
  static final int COLUMN_JNP_BIND = 2;
  static final int COLUMN_WEBSERVER_BIND = 3;
  static final int COLUMN_RMI_BIND = 4;
  static final int COLUMN_HYPERSONIC_BIND = 5;
  static final int COLUMN_JMX_BIND = 6;
  static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

  private int version;
  private int jnpBind;
  private int webserverBind;
  private int rmiBind;
  private int hypersonicBind;
  private int jmxBind;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Site#init(java.sql.ResultSet)
   * @see  Site#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Site() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_TOMCAT_SITE) {
      return pkey;
    }
    if (i == 1) {
      return version;
    }
    if (i == COLUMN_JNP_BIND) {
      return jnpBind;
    }
    if (i == COLUMN_WEBSERVER_BIND) {
      return webserverBind;
    }
    if (i == COLUMN_RMI_BIND) {
      return rmiBind;
    }
    if (i == COLUMN_HYPERSONIC_BIND) {
      return hypersonicBind;
    }
    if (i == COLUMN_JMX_BIND) {
      return jmxBind;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public Version getHttpdJbossVersion() throws SQLException, IOException {
    Version obj = table.getConnector().getWeb_jboss().getVersion().get(version);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdJbossVersion: " + version);
    }
    return obj;
  }

  public com.aoindustries.aoserv.client.web.tomcat.Site getHttpdTomcatSite() throws SQLException, IOException {
    com.aoindustries.aoserv.client.web.tomcat.Site obj = table.getConnector().getWeb_tomcat().getSite().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + pkey);
    }
    return obj;
  }

  public int getHypersonicBind_id() {
    return hypersonicBind;
  }

  public Bind getHypersonicBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(hypersonicBind);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + hypersonicBind);
    }
    return obj;
  }

  public int getJmxBind_id() {
    return jmxBind;
  }

  public Bind getJmxBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(jmxBind);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + jmxBind);
    }
    return obj;
  }

  public int getJnpBind_id() {
    return jnpBind;
  }

  public Bind getJnpBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(jnpBind);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + jnpBind);
    }
    return obj;
  }

  public int getRmiBind_id() {
    return rmiBind;
  }

  public Bind getRmiBind() throws SQLException, IOException {
    Bind obj = table.getConnector().getNet().getBind().get(rmiBind);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + rmiBind);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_JBOSS_SITES;
  }

  public int getWebserverBind_id() {
    return webserverBind;
  }

  public Bind getWebserverBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(webserverBind);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + webserverBind);
    }
    return obj;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    version = result.getInt(2);
    jnpBind = result.getInt(3);
    webserverBind = result.getInt(4);
    rmiBind = result.getInt(5);
    hypersonicBind = result.getInt(6);
    jmxBind = result.getInt(7);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    version = in.readCompressedInt();
    jnpBind = in.readCompressedInt();
    webserverBind = in.readCompressedInt();
    rmiBind = in.readCompressedInt();
    hypersonicBind = in.readCompressedInt();
    jmxBind = in.readCompressedInt();
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHttpdTomcatSite().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(version);
    out.writeCompressedInt(jnpBind);
    out.writeCompressedInt(webserverBind);
    out.writeCompressedInt(rmiBind);
    out.writeCompressedInt(hypersonicBind);
    out.writeCompressedInt(jmxBind);
  }
}
