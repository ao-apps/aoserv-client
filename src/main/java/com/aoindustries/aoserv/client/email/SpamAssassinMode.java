/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2005-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An <code>EmailSpamAssassinIntegrationMode</code> is a simple wrapper for the types
 * of SpamAssassin integration modes.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class SpamAssassinMode extends GlobalObjectStringKey<SpamAssassinMode> {

  static final int COLUMN_NAME = 0;
  static final String COLUMN_SORT_ORDER_name = "sort_order";

  public static final String NONE = "none";
  public static final String POP3 = "pop3";
  public static final String IMAP = "imap";

  public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE = NONE;

  private String display;
  private int sortOrder;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SpamAssassinMode() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return pkey;
      case 1:
        return display;
      case 2:
        return sortOrder;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getName() {
    return pkey;
  }

  public String getDisplay() {
    return display;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
  }

  @Override
  public void init(ResultSet results) throws SQLException {
    pkey = results.getString(1);
    display = results.getString(2);
    sortOrder = results.getInt(3);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    display = in.readUTF();
    sortOrder = in.readCompressedInt();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    out.writeUTF(display);
    out.writeCompressedInt(sortOrder);
  }
}
