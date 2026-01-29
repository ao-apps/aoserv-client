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

package com.aoindustries.aoserv.client.ticket;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.reseller.Reseller;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class Assignment extends CachedObjectIntegerKey<Assignment> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_TICKET = 1;
  static final int COLUMN_RESELLER = 2;
  static final int COLUMN_ADMINISTRATOR = 3;
  static final String COLUMN_PKEY_name = "pkey";
  static final String COLUMN_TICKET_name = "ticket";
  static final String COLUMN_RESELLER_name = "reseller";
  static final String COLUMN_ADMINISTRATOR_name = "administrator";

  private int ticket;
  private Account.Name reseller;
  private User.Name administrator;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  Assignment#init(java.sql.ResultSet)
   * @see  Assignment#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Assignment() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_TICKET:
        return ticket;
      case COLUMN_RESELLER:
        return reseller;
      case COLUMN_ADMINISTRATOR:
        return administrator;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public Ticket getTicket() throws IOException, SQLException {
    Ticket t = table.getConnector().getTicket().getTicket().get(ticket);
    if (t == null) {
      throw new SQLException("Unable to find Ticket: " + ticket);
    }
    return t;
  }

  public Reseller getReseller() throws IOException, SQLException {
    Reseller r = table.getConnector().getReseller().getReseller().get(reseller);
    if (r == null) {
      throw new SQLException("Unable to find Reseller: " + reseller);
    }
    return r;
  }

  public Administrator getAdministrator() throws IOException, SQLException {
    Administrator obj = table.getConnector().getAccount().getAdministrator().get(administrator);
    if (obj == null) {
      throw new SQLException("Unable to find Administrator: " + administrator);
    }
    return obj;
    // Username un=table.getConnector().getUsernames().get(administrator);
    // if (un == null) {
    //   return null;
    // }
    // return un.getAdministrator();
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKET_ASSIGNMENTS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      ticket = result.getInt(2);
      reseller = Account.Name.valueOf(result.getString(3));
      administrator = User.Name.valueOf(result.getString(4));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      ticket = in.readCompressedInt();
      reseller = Account.Name.valueOf(in.readUTF()).intern();
      administrator = User.Name.valueOf(in.readUTF()).intern();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return ticket + "|" + pkey + '|' + reseller + '|' + administrator;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(ticket);
    out.writeUTF(reseller.toString());
    out.writeUTF(administrator.toString());
  }
}
