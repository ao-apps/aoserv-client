/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.ticket.Ticket;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class BrandCategory extends CachedObjectIntegerKey<BrandCategory> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_BRAND = 1;
  static final int COLUMN_CATEGORY = 2;
  static final String COLUMN_PKEY_name = "pkey";
  static final String COLUMN_BRAND_name = "brand";
  static final String COLUMN_CATEGORY_name = "category";

  private Account.Name brand;
  private int category;
  private boolean enabled;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public BrandCategory() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_BRAND:
        return brand;
      case COLUMN_CATEGORY:
        return category;
      case 3:
        return enabled;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Brand getBrand() throws SQLException, IOException {
    Brand br = table.getConnector().getReseller().getBrand().get(brand);
    if (br == null) {
      throw new SQLException("Unable to find Brand: " + brand);
    }
    return br;
  }

  public Category getCategory() throws IOException, SQLException {
    Category tc = table.getConnector().getReseller().getCategory().get(category);
    if (tc == null) {
      throw new SQLException("Unable to find TicketCategory: " + category);
    }
    return tc;
  }

  public boolean getEnabled() {
    return enabled;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKET_BRAND_CATEGORIES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      brand = Account.Name.valueOf(result.getString(2));
      category = result.getInt(3);
      enabled = result.getBoolean(4);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      brand = Account.Name.valueOf(in.readUTF()).intern();
      category = in.readCompressedInt();
      enabled = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return brand + "|" + category + '|' + enabled;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(brand.toString());
    out.writeCompressedInt(category);
    out.writeBoolean(enabled);
  }
}
