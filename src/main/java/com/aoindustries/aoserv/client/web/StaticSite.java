/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>HttpdStaticSite</code> indicates that an <code>HttpdSite</code>
 * serves static content only.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class StaticSite extends CachedObjectIntegerKey<StaticSite> {

  static final int COLUMN_HTTPD_SITE = 0;
  static final String COLUMN_HTTPD_SITE_name = "httpd_site";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  StaticSite#init(java.sql.ResultSet)
   * @see  StaticSite#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public StaticSite() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_HTTPD_SITE) {
      return pkey;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public Site getHttpdSite() throws SQLException, IOException {
    Site obj = table.getConnector().getWeb().getSite().get(pkey);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdSite: " + pkey);
    }
    return obj;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_STATIC_SITES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getHttpdSite().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
  }
}
