/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.i18n.Resources;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
public final class Status extends GlobalObjectStringKey<Status> implements Comparable<Status> {

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, Status.class);

  static final int COLUMN_STATUS = 0;
  static final int COLUMN_SORT_ORDER = 1;
  static final String COLUMN_STATUS_name = "status";
  static final String COLUMN_SORT_ORDER_name = "sort_order";

  /**
   * The different ticket statuses.
   */
  public static final String
      JUNK = "junk",
      DELETED = "deleted",
      CLOSED = "closed",
      BOUNCED = "bounced",
      HOLD = "hold",
      OPEN = "open";

  private short sortOrder;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public Status() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_STATUS) {
      return pkey;
    }
    if (i == 1) {
      return sortOrder;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public short getSortOrder() {
    return sortOrder;
  }

  public String getStatus() {
    return pkey;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TICKET_STATI;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getString(1);
    sortOrder = result.getShort(2);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readUTF().intern();
    sortOrder = in.readShort();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_43) <= 0) {
      out.writeUTF(pkey);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_44) >= 0) {
      out.writeShort(sortOrder);
    }
  }

  @Override
  public String toStringImpl() {
    return RESOURCES.getMessage(pkey + ".toString");
  }

  /**
   * Localized description.
   */
  public String getDescription() {
    return RESOURCES.getMessage(pkey + ".description");
  }

  @Override
  public int compareTo(Status o) {
    short sortOrder1 = sortOrder;
    short sortOrder2 = o.sortOrder;
    if (sortOrder1 < sortOrder2) {
      return -1;
    }
    if (sortOrder1 > sortOrder2) {
      return 1;
    }
    return 0;
  }
}
