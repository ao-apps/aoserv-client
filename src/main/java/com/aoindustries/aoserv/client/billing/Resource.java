/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.billing;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * A <code>Resource</code> is a measurable hardware resource.  A <code>Package</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the {@link Account}.
 *
 * @see  Package
 *
 * @author  AO Industries, Inc.
 */
public final class Resource extends GlobalObjectStringKey<Resource> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Resource.class);

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  public static final String AOSERV_DAEMON = "aoserv_daemon";
  public static final String AOSERV_MASTER = "aoserv_master";
  public static final String BANDWIDTH = "bandwidth";
  public static final String CONSULTING = "consulting";
  public static final String DISK = "disk";
  public static final String DISTRIBUTION_SCAN = "distribution_scan";
  public static final String DRUPAL = "drupal";
  public static final String EMAIL = "email";
  public static final String FAILOVER = "failover";
  public static final String HARDWARE_DISK_7200_120 = "hardware_disk_7200_120";
  public static final String HTTPD = "httpd";
  public static final String IP = "ip";
  public static final String JAVAVM = "javavm";
  public static final String JOOMLA = "joomla";
  public static final String MYSQL_REPLICATION = "mysql_replication";
  public static final String RACK = "rack";
  public static final String SERVER_DATABASE = "server_database";
  public static final String SERVER_ENTERPRISE = "server_enterprise";
  public static final String SERVER_P4 = "server_p4";
  public static final String SERVER_SCSI = "server_scsi";
  public static final String SERVER_XEON = "server_xeon";
  public static final String SITE = "site";
  public static final String SYSADMIN = "sysadmin";
  public static final String USER = "user";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Resource() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  /**
   * Gets the unique name of this resource.
   */
  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.RESOURCES;
  }

  public String getDisplayUnit(int quantity) {
    if (quantity == 1) {
      return RESOURCES.getMessage(pkey + ".singularDisplayUnit", quantity);
    } else {
      return RESOURCES.getMessage(pkey + ".pluralDisplayUnit", quantity);
    }
  }

  public String getPerUnit(Object amount) {
    return RESOURCES.getMessage(pkey + ".perUnit", amount);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
  }

  @Override
  public String toStringImpl() {
    return RESOURCES.getMessage(pkey + ".toString");
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60) <= 0) {
      out.writeUTF(RESOURCES.getMessage(pkey + ".singularDisplayUnit", ""));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_123) >= 0 && protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60) <= 0) {
      out.writeUTF(RESOURCES.getMessage(pkey + ".pluralDisplayUnit", ""));
      out.writeUTF(getPerUnit(""));
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_60) <= 0) {
      out.writeUTF(toString());
    } // description
  }
}
