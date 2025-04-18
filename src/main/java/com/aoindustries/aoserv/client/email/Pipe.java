/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Incoming email addressed to an <code>EmailPipe</code> is piped
 * into a native process.  This process may then take any action
 * desired for mail delivery or handling.
 *
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class Pipe extends CachedObjectIntegerKey<Pipe> implements Removable, Disablable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_AO_SERVER = 1;
  static final int COLUMN_PACKAGE = 3;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_COMMAND_name = "command";

  private int aoServer;
  private String command;
  private Account.Name packageName;
  private int disableLog;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Pipe() {
    // Do nothing
  }

  public int addEmailAddress(Address address) throws IOException, SQLException {
    return table.getConnector().getEmail().getPipeAddress().addEmailPipeAddress(address, this);
  }

  @Override
  public boolean canDisable() {
    return disableLog == -1;
  }

  @Override
  public boolean canEnable() throws SQLException, IOException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getPackage().isDisabled();
    }
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.EMAIL_PIPES, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.EMAIL_PIPES, pkey);
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_AO_SERVER:
        return aoServer;
      case 2:
        return command;
      case COLUMN_PACKAGE:
        return packageName;
      case 4:
        return disableLog == -1 ? null : disableLog;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  @Override
  public DisableLog getDisableLog() throws SQLException, IOException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  public Package getPackage() throws IOException, SQLException {
    Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
    if (packageObject == null) {
      throw new SQLException("Unable to find Package: " + packageName);
    }
    return packageObject;
  }

  public String getCommand() {
    return command;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    Server ao = table.getConnector().getLinux().getServer().get(aoServer);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return ao;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_PIPES;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      aoServer = result.getInt(2);
      command = result.getString(3);
      packageName = Account.Name.valueOf(result.getString(4));
      disableLog = result.getInt(5);
      if (result.wasNull()) {
        disableLog = -1;
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      aoServer = in.readCompressedInt();
      command = in.readUTF();
      packageName = Account.Name.valueOf(in.readUTF()).intern();
      disableLog = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
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
        Table.TableId.EMAIL_PIPES,
        pkey
    );
  }

  @Override
  public String toStringImpl() {
    return aoServer + ":" + command;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(aoServer);
    out.writeUTF(command);
    out.writeUTF(packageName.toString());
    out.writeCompressedInt(disableLog);
  }
}
