/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.account;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * When a resource or resources are disabled, the reason and time is logged.
 *
 * @author  AO Industries, Inc.
 */
public final class DisableLog extends CachedObjectIntegerKey<DisableLog> {

  static final int COLUMN_PKEY = 0;
  static final String COLUMN_TIME_name = "time";
  static final String COLUMN_ACCOUNTING_name = "accounting";
  static final String COLUMN_PKEY_name = "pkey";

  private UnmodifiableTimestamp time;
  private Account.Name accounting;
  private User.Name disabledBy;
  private String disableReason;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  DisableLog#init(java.sql.ResultSet)
   * @see  DisableLog#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DisableLog() {
    // Do nothing
  }

  /**
   * Determines if the current <code>AoservConnector</code> can enable
   * things disabled by this <code>DisableLog</code>.
   */
  public boolean canEnable() throws SQLException, IOException {
    Administrator disabledBy = getDisabledBy();
    return disabledBy != null && table
        .getConnector()
        .getCurrentAdministrator()
        .getUsername()
        .getPackage()
        .getAccount()
        .isAccountOrParentOf(
            disabledBy
                .getUsername()
                .getPackage()
                .getAccount()
        );
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    if (i == COLUMN_PKEY) {
      return pkey;
    }
    if (i == 1) {
      return time;
    }
    if (i == 2) {
      return accounting;
    }
    if (i == 3) {
      return disabledBy;
    }
    if (i == 4) {
      return disableReason;
    }
    throw new IllegalArgumentException("Invalid index: " + i);
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  public Account getAccount() throws SQLException, IOException {
    Account obj = table.getConnector().getAccount().getAccount().get(accounting);
    if (obj == null) {
      throw new SQLException("Unable to find Account: " + accounting);
    }
    return obj;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  public User.Name getDisabledByUsername() {
    return disabledBy;
  }

  public Administrator getDisabledBy() throws IOException, SQLException {
    // May be filtered
    return table.getConnector().getAccount().getAdministrator().get(disabledBy);
  }

  public String getDisableReason() {
    return disableReason;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.DISABLE_LOG;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp(2));
      accounting = Account.Name.valueOf(result.getString(3));
      disabledBy = User.Name.valueOf(result.getString(4));
      disableReason = result.getString(5);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      accounting = Account.Name.valueOf(in.readUTF()).intern();
      disabledBy = User.Name.valueOf(in.readUTF()).intern();
      disableReason = in.readNullUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(time.getTime());
    } else {
      SQLStreamables.writeTimestamp(time, out);
    }
    out.writeUTF(accounting.toString());
    out.writeUTF(disabledBy.toString());
    out.writeNullUTF(disableReason);
  }
}
