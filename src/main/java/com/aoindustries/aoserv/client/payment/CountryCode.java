/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2024  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.payment;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>CountryCode</code> is a simple wrapper for country
 * code and name mappings.  Each code is a two-digit ISO 3166-1 alpha-2 country
 * code.
 *
 * <p>See <a href="https://wikipedia.org/wiki/ISO_3166-1_alpha-2">https://wikipedia.org/wiki/ISO_3166-1_alpha-2</a></p>
 *
 * @author  AO Industries, Inc.
 */
public final class CountryCode extends GlobalObjectStringKey<CountryCode> {

  static final int COLUMN_CODE = 0;
  static final String COLUMN_NAME_name = "name";

  /**
   * <code>CountryCode</code>s used as constants.
   */
  public static final String US = "US";

  private String name;
  private boolean chargeComSupported;
  private String chargeComName;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public CountryCode() {
    // Do nothing
  }

  /**
   * Gets the two-character unique code for this country.
   */
  public String getCode() {
    return pkey;
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_CODE) {
      return pkey;
    }
    if (i == 1) {
      return name;
    }
    if (i == 2) {
      return chargeComSupported;
    }
    if (i == 3) {
      return chargeComName;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public String getName() {
    return name;
  }

  public boolean getChargeComSupported() {
    return chargeComSupported;
  }

  public String getChargeComName() {
    return chargeComName == null ? name : chargeComName;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.COUNTRY_CODES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    name = result.getString(2);
    chargeComSupported = result.getBoolean(3);
    chargeComName = result.getString(4);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    name = in.readUTF();
    chargeComSupported = in.readBoolean();
    chargeComName = in.readNullUTF();
  }

  @Override
  public String toStringImpl() {
    return name;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(name);
    out.writeBoolean(chargeComSupported);
    out.writeNullUTF(chargeComName);
  }
}
