/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdTomcatSharedSite</code> is an <code>HttpdTomcatSite</code>
 * running under an <code>HttpdSharedTomcat</code>.
 *
 * @see  SharedTomcat
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class SharedTomcatSite extends CachedObjectIntegerKey<SharedTomcatSite> {

  static final int COLUMN_TOMCAT_SITE = 0;
  static final int COLUMN_HTTPD_SHARED_TOMCAT = 1;
  static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";

  private int httpdSharedTomcat;

  public static final String DEFAULT_TOMCAT_VERSION_PREFIX = Version.VERSION_10_1_PREFIX;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SharedTomcatSite() {
    // Do nothing
  }

  /**
   * Determines if the API user is allowed to stop the Java virtual machine associated
   * with this site.
   */
  public boolean canStop() throws SQLException, IOException {
    SharedTomcat hst = getHttpdSharedTomcat();
    return getHttpdSharedTomcat() != null && !hst.isDisabled();
  }

  /**
   * Determines if the API user is allowed to start the Java virtual machine associated
   * with this site.
   */
  public boolean canStart() throws SQLException, IOException {
    // This site must be enabled
    if (getHttpdTomcatSite().getHttpdSite().isDisabled()) {
      return false;
    }
    SharedTomcat hst = getHttpdSharedTomcat();
    if (hst == null) {
      // Filtered, assume can start
      return true;
    }
    // Has at least one enabled site: this one
    return !hst.isDisabled();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_TOMCAT_SITE:
        return pkey;
      case COLUMN_HTTPD_SHARED_TOMCAT:
        return httpdSharedTomcat;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public SharedTomcat getHttpdSharedTomcat() throws SQLException, IOException {
    // May be null when filtered
    return table.getConnector().getWeb_tomcat().getSharedTomcat().get(httpdSharedTomcat);
  }

  public Site getHttpdTomcatSite() throws SQLException, IOException {
    Site obj = table.getConnector().getWeb_tomcat().getSite().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + pkey);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_SHARED_SITES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    httpdSharedTomcat = result.getInt(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    httpdSharedTomcat = in.readCompressedInt();
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHttpdTomcatSite().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(httpdSharedTomcat);
  }
}
