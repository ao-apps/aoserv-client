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
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.net.AppProtocol;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Multiple <code>HttpdSiteURL</code>s may be attached to a unique
 * combination of <code>HttpdSite</code> and <code>HttpdBind</code>,
 * represented by an <code>HttpdSiteBind</code>.  This allows a web
 * site to respond to several different hostnames on the same IP/port
 * combination.
 *
 * @see  VirtualHost
 * @see  Site
 * @see  HttpdBind
 *
 * @author  AO Industries, Inc.
 */
public final class VirtualHostName extends CachedObjectIntegerKey<VirtualHostName> implements Removable {

  static final int
    COLUMN_PKEY=0,
    COLUMN_HTTPD_SITE_BIND=1
  ;
  static final String COLUMN_HOSTNAME_name = "hostname";
  static final String COLUMN_HTTPD_SITE_BIND_name = "httpd_site_bind";

  private int httpd_site_bind;
  private DomainName hostname;
  private boolean isPrimary;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated/* Java 9: (forRemoval = true) */
  public VirtualHostName() {
    // Do nothing
  }

  @Override
  public List<CannotRemoveReason<VirtualHostName>> getCannotRemoveReasons() throws SQLException, IOException {
    List<CannotRemoveReason<VirtualHostName>> reasons=new ArrayList<>();

    if (isPrimary) {
      reasons.add(new CannotRemoveReason<>("Not allowed to remove the primary URL", this));
    }
    if (isTestURL()) {
      reasons.add(new CannotRemoveReason<>("Not allowed to remove the test URL", this));
    }

    return reasons;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY: return pkey;
      case COLUMN_HTTPD_SITE_BIND: return httpd_site_bind;
      case 2: return hostname;
      case 3: return isPrimary;
      default: throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public DomainName getHostname() {
    return hostname;
  }

  public VirtualHost getHttpdSiteBind() throws SQLException, IOException {
    VirtualHost obj=table.getConnector().getWeb().getVirtualHost().get(httpd_site_bind);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdSiteBind: "+httpd_site_bind);
    }
    return obj;
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.HTTPD_SITE_URLS;
  }

  public String getURL() throws SQLException, IOException {
    VirtualHost siteBind=getHttpdSiteBind();
    Bind netBind=siteBind.getHttpdBind().getNetBind();
    Port port=netBind.getPort();
    StringBuilder url=new StringBuilder();
    AppProtocol appProtocol = siteBind.getHttpdBind().getNetBind().getAppProtocol();
    String protocol = appProtocol.getProtocol();
    if (AppProtocol.HTTP.equals(protocol)) {
      url.append("http://");
    } else if (AppProtocol.HTTPS.equals(protocol)) {
      url.append("https://");
    } else {
      throw new SQLException("Unsupported protocol: " + protocol);
    }
    url.append(hostname);
    if (!port.equals(appProtocol.getPort())) {
      url.append(':').append(port.getPort());
    }
    url.append('/');
    return url.toString();
  }

  public String getURLNoSlash() throws SQLException, IOException {
    VirtualHost siteBind=getHttpdSiteBind();
    Bind netBind=siteBind.getHttpdBind().getNetBind();
    Port port=netBind.getPort();
    StringBuilder url=new StringBuilder();
    AppProtocol appProtocol = siteBind.getHttpdBind().getNetBind().getAppProtocol();
    String protocol = appProtocol.getProtocol();
    if (AppProtocol.HTTP.equals(protocol)) {
      url.append("http://");
    } else if (AppProtocol.HTTPS.equals(protocol)) {
      url.append("https://");
    } else {
      throw new SQLException("Unsupported protocol: " + protocol);
    }
    url.append(hostname);
    if (!port.equals(appProtocol.getPort())) {
      url.append(':').append(port.getPort());
    }
    return url.toString();
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey=result.getInt(1);
      httpd_site_bind=result.getInt(2);
      hostname=DomainName.valueOf(result.getString(3));
      isPrimary=result.getBoolean(4);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  /**
   * TODO: "Primary Hostname" to "Canonical Hostname" across the AO system.
   */
  public boolean isPrimary() {
    return isPrimary;
  }

  public boolean isTestURL() throws SQLException, IOException {
    Site hs=getHttpdSiteBind().getHttpdSite();
    return hostname.toString().equalsIgnoreCase(hs.getName()+"."+hs.getLinuxServer().getHostname());
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey=in.readCompressedInt();
      httpd_site_bind=in.readCompressedInt();
      hostname=DomainName.valueOf(in.readUTF());
      isPrimary=in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.HTTPD_SITE_URLS, pkey);
  }

  public void setAsPrimary() throws IOException, SQLException {
    table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_PRIMARY_HTTPD_SITE_URL, pkey);
  }

  @Override
  public String toStringImpl() {
    return hostname.toString();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(httpd_site_bind);
    out.writeUTF(hostname.toString());
    out.writeBoolean(isPrimary);
  }
}
