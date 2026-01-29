/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * A <code>PackageCategory</code> represents one type of service.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
public final class PackageCategory extends GlobalObjectStringKey<PackageCategory> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, PackageCategory.class);

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  public static final String AOSERV = "aoserv";
  public static final String APPLICATION = "application";
  public static final String DEDICATED = "dedicated";
  public static final String MANAGED = "managed";
  public static final String RESELLER = "reseller";
  public static final String SYSADMIN = "sysadmin";
  public static final String VIRTUAL = "virtual";
  public static final String VIRTUAL_DEDICATED = "virtual_dedicated";
  public static final String VIRTUAL_MANAGED = "virtual_managed";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  PackageCategory#init(java.sql.ResultSet)
   * @see  PackageCategory#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public PackageCategory() {
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

  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.PACKAGE_CATEGORIES;
  }

  @Override
  public void init(ResultSet results) throws SQLException {
    pkey = results.getString(1);
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
      out.writeUTF(toString());
    } // display
  }
}
