/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2019, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.i18n.ThreadLocale;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A {@link Currency} represents one type supported {@link java.util.Currency}.
 *
 * @author  AO Industries, Inc.
 */
public final class Currency extends GlobalObjectStringKey<Currency> {

  static final int COLUMN_currencyCode = 0;
  static final String COLUMN_currencyCode_name = "currencyCode";

  /**
   * The currency assumed by the system before multiple currencies were supported.
   */
  public static final java.util.Currency USD = java.util.Currency.getInstance("USD");

  private short fractionDigits;
  private Money autoEnableMinimumPayment;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Currency() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_currencyCode:
        return pkey;
      case 1:
        return fractionDigits;
      case 2:
        return autoEnableMinimumPayment;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getCurrencyCode() {
    return pkey;
  }

  public java.util.Currency getCurrency() {
    return autoEnableMinimumPayment.getCurrency();
  }

  public short getFractionDigits() {
    assert getCurrency().getDefaultFractionDigits() == fractionDigits;
    return fractionDigits;
  }

  public Money getAutoEnableMinimumPayment() {
    return autoEnableMinimumPayment;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.Currency;
  }

  @Override
  public void init(ResultSet results) throws SQLException {
    pkey = results.getString("currencyCode");
    fractionDigits = results.getShort("fractionDigits");
    autoEnableMinimumPayment = new Money(
        java.util.Currency.getInstance(pkey),
        results.getBigDecimal("autoEnableMinimumPayment")
    );
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    fractionDigits = in.readShort();
    autoEnableMinimumPayment = new Money(
        java.util.Currency.getInstance(pkey),
        in.readLong(),
        in.readCompressedInt()
    );
  }

  @Override
  public String toStringImpl() {
    return getCurrency().getDisplayName(ThreadLocale.get());
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeShort(fractionDigits);
    out.writeLong(autoEnableMinimumPayment.getUnscaledValue());
    out.writeCompressedInt(autoEnableMinimumPayment.getScale());
  }
}
