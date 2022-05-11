/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.reseller;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Resources;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.ticket.Ticket;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class Category extends CachedObjectIntegerKey<Category> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Category.class);

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_PARENT = 1;
  static final int COLUMN_NAME = 2;
  static final String COLUMN_PKEY_name = "pkey";
  static final String COLUMN_PARENT_name = "parent";
  static final String COLUMN_NAME_name = "name";

  /**
   * Some conveniences constants for specific categories.
   */
  public static final int AOSERV_MASTER_PKEY = 110;

  private int parent;
  private String name;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Category() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_PARENT:
        return parent == -1 ? null : parent;
      case COLUMN_NAME:
        return name;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Integer getParent_id() {
    return (parent == -1) ? null : parent;
  }

  /**
   * Gets the parent category or {@code null} if this is a top-level category.
   */
  public Category getParent() throws IOException, SQLException {
    if (parent == -1) {
      return null;
    }
    Category tc = table.getConnector().getReseller().getCategory().get(parent);
    if (tc == null) {
      throw new SQLException("Unable to find TicketCategory: " + parent);
    }
    return tc;
  }

  public String getName() {
    return name;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKET_CATEGORIES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
    parent = result.getInt(2);
    if (result.wasNull()) {
      parent = -1;
    }
    name = result.getString(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
    parent = in.readCompressedInt();
    name = in.readUTF().intern();
  }

  private String slashPath;

  public synchronized String getSlashPath() throws IOException, SQLException {
    if (slashPath == null) {
      slashPath = parent == -1 ? name : (getParent().getSlashPath() + '/' + name);
    }
    return slashPath;
  }

  private String dotPath;

  public synchronized String getDotPath() throws IOException, SQLException {
    if (dotPath == null) {
      dotPath = parent == -1 ? name : (getParent().getDotPath() + '.' + name);
    }
    return dotPath;
  }

  @Override
  public String toStringImpl() throws IOException, SQLException {
    return RESOURCES.getMessage(getDotPath() + ".toString");
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(parent);
    out.writeUTF(name);
  }

  public List<BrandCategory> getTicketBrandCategorys() throws IOException, SQLException {
    return table.getConnector().getReseller().getBrandCategory().getTicketBrandCategories(this);
  }

  public List<Category> getChildrenCategories() throws IOException, SQLException {
    return table.getConnector().getReseller().getCategory().getChildrenCategories(this);
  }
}
