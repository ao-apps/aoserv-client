/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.distribution;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>TechnologyName</code> represents one piece of software installed in
 * the system.
 *
 * @author  AO Industries, Inc.
 */
public final class Software extends GlobalObjectStringKey<Software> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_NAME_name = "name";

  public static final String MYSQL = "MySQL";

  public static final String PHP = "php";

  private String imageFilename;
  private int imageWidth;
  private int imageHeight;
  private String imageAlt;
  private String homePageUrl;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Software#init(java.sql.ResultSet)
   * @see  Software#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Software() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_NAME) {
      return pkey;
    }
    if (i == 1) {
      return imageFilename;
    }
    if (i == 2) {
      return imageWidth == -1 ? null : imageWidth;
    }
    if (i == 3) {
      return imageHeight == -1 ? null : imageHeight;
    }
    if (i == 4) {
      return imageAlt;
    }
    if (i == 5) {
      return homePageUrl;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public String getHomePageUrl() {
    return homePageUrl;
  }

  public String getImageAlt() {
    return imageAlt;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public String getName() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TECHNOLOGY_NAMES;
  }

  public List<SoftwareCategorization> getTechnologies(AoservConnector connector) throws IOException, SQLException {
    return connector.getDistribution().getSoftwareCategorization().getTechnologies(this);
  }

  public SoftwareVersion getTechnologyVersion(AoservConnector connector, String version, OperatingSystemVersion osv) throws IOException, SQLException {
    return connector.getDistribution().getSoftwareVersion().getTechnologyVersion(this, version, osv);
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    imageFilename = result.getString(2);
    imageWidth = result.getInt(3);
    if (result.wasNull()) {
      imageWidth = -1;
    }
    imageHeight = result.getInt(4);
    if (result.wasNull()) {
      imageHeight = -1;
    }
    imageAlt = result.getString(5);
    homePageUrl = result.getString(6);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    imageFilename = in.readNullUTF();
    imageWidth = in.readCompressedInt();
    imageHeight = in.readCompressedInt();
    imageAlt = in.readNullUTF();
    homePageUrl = in.readNullUTF();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeNullUTF(imageFilename);
    out.writeCompressedInt(imageWidth);
    out.writeCompressedInt(imageHeight);
    out.writeNullUTF(imageAlt);
    out.writeNullUTF(homePageUrl);
  }
}
