/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Any email sent to a <code>BlackholeEmailAddress</code> is piped
 * directly to <code>/dev/null</code> - the bit bucket - the email
 * appears to have been delivered but is simply discarded.
 *
 * @see  Address#getBlackholeEmailAddress()
 *
 * @author  AO Industries, Inc.
 */
public final class BlackholeAddress extends CachedObjectIntegerKey<BlackholeAddress> implements Removable {

  static final int COLUMN_EMAIL_ADDRESS = 0;
  static final String COLUMN_EMAIL_ADDRESS_name = "email_address";

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  BlackholeAddress#init(java.sql.ResultSet)
   * @see  BlackholeAddress#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public BlackholeAddress() {
    // Do nothing
  }

  @Override
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_EMAIL_ADDRESS) {
      return pkey;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public Address getEmailAddress() throws SQLException, IOException {
    Address emailAddressObject = table.getConnector().getEmail().getAddress().get(pkey);
    if (emailAddressObject == null) {
      throw new SQLException("Unable to find EmailAddress: " + pkey);
    }
    return emailAddressObject;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BLACKHOLE_EMAIL_ADDRESSES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    pkey = result.getInt(1);
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    pkey = in.readCompressedInt();
  }

  @Override
  public List<CannotRemoveReason<?>> getCannotRemoveReasons() {
    return Collections.emptyList();
  }

  @Override
  public void remove() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REMOVE,
        Table.TableId.BLACKHOLE_EMAIL_ADDRESSES,
        pkey
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return getEmailAddress().toStringImpl();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
  }
}
