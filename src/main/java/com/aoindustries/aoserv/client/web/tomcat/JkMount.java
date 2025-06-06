/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Each {@link Site} has independently configured <a href="https://tomcat.apache.org/connectors-doc/reference/apache.html">JkMount and JkUnMount directives</a>.
 *
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
public final class JkMount extends CachedObjectIntegerKey<JkMount> implements Removable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_HTTPD_TOMCAT_SITE = 1;
  static final String COLUMN_HTTPD_TOMCAT_SITE_name = "httpd_tomcat_site";
  static final String COLUMN_PATH_name = "path";
  static final String COLUMN_MOUNT_name = "mount";

  /**
   * Checks if the path is valid for JkMount
   * and JkUnMount.
   */
  public static boolean isValidPath(String path) {
    return
        path.length() > 1
            && path.charAt(0) == '/'
            && !path.contains("//")
            && !path.contains("..")
            && path.indexOf('"') == -1
            && path.indexOf('\\') == -1
            && path.indexOf('\n') == -1
            && path.indexOf('\r') == -1
            && path.indexOf('\0') == -1;
  }

  private int httpdTomcatSite;
  private String path;
  private boolean mount;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public JkMount() {
    // Do nothing
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_HTTPD_TOMCAT_SITE:
        return httpdTomcatSite;
      case 2:
        return path;
      case 3:
        return mount;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Site getHttpdTomcatSite() throws SQLException, IOException {
    Site obj = table.getConnector().getWeb_tomcat().getSite().get(httpdTomcatSite);
    if (obj == null) {
      throw new SQLException("Unable to find HttpdTomcatSite: " + httpdTomcatSite);
    }
    return obj;
  }

  public String getPath() {
    return path;
  }

  /**
   * When {@code true} is a <code>JkMount</code> directive.
   * When {@code false} is a <code>JkUnMount</code> directive.
   */
  public boolean isMount() {
    return mount;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.HTTPD_TOMCAT_SITE_JK_MOUNTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    httpdTomcatSite = result.getInt(2);
    path = result.getString(3);
    mount = result.getBoolean(4);
    if (!isValidPath(path)) {
      throw new SQLException("Invalid path: " + path);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    httpdTomcatSite = in.readCompressedInt();
    path = in.readUTF();
    mount = in.readBoolean();
    if (!isValidPath(path)) {
      throw new IOException("Invalid path: " + path);
    }
  }

  @Override
  public String toStringImpl() {
    return (mount ? "JkMount " : "JkUnMount ") + path;
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.REMOVE, Table.TableId.HTTPD_TOMCAT_SITE_JK_MOUNTS, pkey);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(httpdTomcatSite);
    out.writeUTF(path);
    out.writeBoolean(mount);
  }
}
