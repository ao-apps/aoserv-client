/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2009-2013, 2016, 2017, 2018, 2019, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.CachedObjectAccountNameKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.ticket.Assignment;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A reseller may handle support tickets..
 *
 * @see  Account
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
public final class Reseller extends CachedObjectAccountNameKey<Reseller> {

  static final int COLUMN_ACCOUNTING = 0;
  static final String COLUMN_ACCOUNTING_name = "accounting";

  private boolean ticketAutoEscalate;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Reseller() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ACCOUNTING:
        return pkey;
      case 1:
        return ticketAutoEscalate;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Account.Name getBrand_business_accounting() {
    return pkey;
  }

  public Brand getBrand() throws SQLException, IOException {
    Brand br = table.getConnector().getReseller().getBrand().get(pkey);
    if (br == null) {
      throw new SQLException("Unable to find Brand: " + pkey);
    }
    return br;
  }

  public boolean getTicketAutoEscalate() {
    return ticketAutoEscalate;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.RESELLERS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = Account.Name.valueOf(result.getString(pos++));
      ticketAutoEscalate = result.getBoolean(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = Account.Name.valueOf(in.readUTF()).intern();
      ticketAutoEscalate = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey.toUpperCase());
    out.writeBoolean(ticketAutoEscalate);
  }

  public List<Assignment> getTicketAssignments() throws IOException, SQLException {
    return table.getConnector().getTicket().getAssignment().getTicketAssignments(this);
  }

  /**
   * Gets the immediate parent of this reseller or {@code null} if none available.
   */
  public Reseller getParent() throws IOException, SQLException {
    Account bu = getBrand().getAccount();
    if (bu == null) {
      return null;
    }
    Account parent = bu.getParent();
    while (parent != null) {
      Brand parentBrand = parent.getBrand();
      if (parentBrand != null) {
        Reseller parentReseller = parentBrand.getReseller();
        if (parentReseller != null) {
          return parentReseller;
        }
      }
    }
    return null;
  }

  /**
   * The children of the reseller are any resellers that have their closest parent
   * account (that is a reseller) equal to this one.
   */
  public List<Reseller> getChildResellers() throws IOException, SQLException {
    List<Reseller> children = new ArrayList<>();
    for (Reseller reseller : table.getConnector().getReseller().getReseller().getRows()) {
      if (!reseller.equals(this) && this.equals(reseller.getParent())) {
        children.add(reseller);
      }
    }
    return children;
  }
}
