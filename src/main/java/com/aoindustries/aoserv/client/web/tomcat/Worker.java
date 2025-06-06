/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdWorker</code> represents a unique combination of
 * <code>HttpdJKCode</code> and <code>HttpdTomcatSite</code>.  The
 * details about which IP address and port the servlet engine is
 * listening on is available.
 *
 * @see  WorkerName
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class Worker extends CachedObjectIntegerKey<Worker> {

  static final int COLUMN_BIND = 0;
  static final int COLUMN_TOMCAT_SITE = 2;
  static final String COLUMN_BIND_name = "bind";
  static final String COLUMN_NAME_name = "name";

  private String name;
  private int tomcatSite;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Worker() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_BIND:
        return pkey;
      case 1:
        return name;
      case COLUMN_TOMCAT_SITE:
        return tomcatSite == -1 ? null : tomcatSite;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getBind_id() {
    return pkey;
  }

  public Bind getBind() throws IOException, SQLException {
    Bind obj = table.getConnector().getNet().getBind().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find NetBind: " + pkey);
    }
    return obj;
  }

  public String getName_code() {
    return name;
  }

  public WorkerName getName() throws SQLException, IOException {
    WorkerName obj = table.getConnector().getWeb_tomcat().getWorkerName().get(name);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdJKCode: " + name);
    }
    return obj;
  }

  public int getTomcatSite_httpdSite() {
    return tomcatSite;
  }

  public Site getTomcatSite() throws SQLException, IOException {
    if (tomcatSite == -1) {
      return null;
    }
    Site obj = table.getConnector().getWeb_tomcat().getSite().get(tomcatSite);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + tomcatSite);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_WORKERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    int pos = 1;
    pkey = result.getInt(pos++);
    name = result.getString(pos++);
    tomcatSite = result.getInt(pos++);
    if (result.wasNull()) {
      tomcatSite = -1;
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    name = in.readUTF();
    tomcatSite = in.readCompressedInt();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(name);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeCompressedInt(pkey);
    }
    out.writeCompressedInt(tomcatSite);
  }

  @Override
  public String toStringImpl() {
    return pkey + "|" + name;
  }

  public JkProtocol getHttpdJkProtocol(AoservConnector connector) throws IOException, SQLException {
    AppProtocol appProtocol = getBind().getAppProtocol();
    JkProtocol obj = appProtocol.getHttpdJkProtocol(connector);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdJkProtocol: " + appProtocol);
    }
    return obj;
  }

  public SharedTomcat getHttpdSharedTomcat() throws SQLException, IOException {
    return table.getConnector().getWeb_tomcat().getSharedTomcat().getHttpdSharedTomcat(this);
  }
}
