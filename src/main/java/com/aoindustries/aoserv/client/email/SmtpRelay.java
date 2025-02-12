/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
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
import com.aoapps.net.HostAddress;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
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
 * When a user successfully logs into either the POP3 or
 * IMAP mail systems, access from their host is
 * granted to the SMTP server via a <code>EmailSmtpRelay</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class SmtpRelay extends CachedObjectIntegerKey<SmtpRelay> implements Removable, Disablable {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_PACKAGE = 1;
  static final String COLUMN_AO_SERVER_name = "ao_server";
  static final String COLUMN_HOST_name = "host";
  static final String COLUMN_PACKAGE_name = "package";

  /**
   * Keep the SMTP relay history for three months (92 days).
   */
  public static final int HISTORY_DAYS = 92;

  private Account.Name packageName;
  private int aoServer;
  private HostAddress host;
  private String type;
  private UnmodifiableTimestamp created;
  private UnmodifiableTimestamp lastRefreshed;
  private int refreshCount;
  private UnmodifiableTimestamp expiration;
  private int disableLog;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public SmtpRelay() {
    // Do nothing
  }

  public int addSpamEmailMessage(String message) throws IOException, SQLException {
    return table.getConnector().getEmail().getSpamMessage().addSpamEmailMessage(this, message);
  }

  @Override
  public boolean canDisable() {
    return disableLog == -1;
  }

  @Override
  public boolean canEnable() throws IOException, SQLException {
    DisableLog dl = getDisableLog();
    if (dl == null) {
      return false;
    } else {
      return dl.canEnable() && !getPackage().isDisabled();
    }
  }

  @Override
  public void disable(DisableLog dl) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.DISABLE, Table.TableId.EMAIL_SMTP_RELAYS, dl.getPkey(), pkey);
  }

  @Override
  public void enable() throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(true, AoservProtocol.CommandId.ENABLE, Table.TableId.EMAIL_SMTP_RELAYS, pkey);
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case COLUMN_PACKAGE:
        return packageName;
      case 2:
        return aoServer == -1 ? null : aoServer;
      case 3:
        return host;
      case 4:
        return type;
      case 5:
        return created;
      case 6:
        return lastRefreshed;
      case 7:
        return refreshCount;
      case 8:
        return expiration;
      case 9:
        return disableLog == -1 ? null : disableLog;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  @Override
  public boolean isDisabled() {
    return disableLog != -1;
  }

  @Override
  public DisableLog getDisableLog() throws IOException, SQLException {
    if (disableLog == -1) {
      return null;
    }
    DisableLog obj = table.getConnector().getAccount().getDisableLog().get(disableLog);
    if (obj == null) {
      throw new SQLException("Unable to find DisableLog: " + disableLog);
    }
    return obj;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getExpiration() {
    return expiration;
  }

  public HostAddress getHost() {
    return host;
  }

  public SmtpRelayType getType() throws SQLException, IOException {
    SmtpRelayType esrt = table.getConnector().getEmail().getSmtpRelayType().get(type);
    if (esrt == null) {
      throw new SQLException("Unable to find EmailSmtpRelayType: " + type);
    }
    return esrt;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getLastRefreshed() {
    return lastRefreshed;
  }

  public Account.Name getPackage_name() {
    return packageName;
  }

  public Package getPackage() throws IOException, SQLException {
    // May be filtered
    return table.getConnector().getBilling().getPackage().get(packageName);
  }

  public int getRefreshCount() {
    return refreshCount;
  }

  public Integer getLinuxServer_host_id() {
    return (aoServer == -1) ? null : aoServer;
  }

  public Server getLinuxServer() throws SQLException, IOException {
    if (aoServer == -1) {
      return null;
    }
    Server ao = table.getConnector().getLinux().getServer().get(aoServer);
    if (ao == null) {
      throw new SQLException("Unable to find linux.Server: " + aoServer);
    }
    return ao;
  }

  public List<SpamMessage> getSpamEmailMessages() throws IOException, SQLException {
    return table.getConnector().getEmail().getSpamMessage().getSpamEmailMessages(this);
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.EMAIL_SMTP_RELAYS;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt(1);
      packageName = Account.Name.valueOf(result.getString(2));
      aoServer = result.getInt(3);
      if (result.wasNull()) {
        aoServer = -1;
      }
      host = HostAddress.valueOf(result.getString(4));
      type = result.getString(5);
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(6));
      lastRefreshed = UnmodifiableTimestamp.valueOf(result.getTimestamp(7));
      refreshCount = result.getInt(8);
      expiration = UnmodifiableTimestamp.valueOf(result.getTimestamp(9));
      disableLog = result.getInt(10);
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
      packageName = Account.Name.valueOf(in.readUTF()).intern();
      aoServer = in.readCompressedInt();
      host = HostAddress.valueOf(in.readUTF());
      type = in.readUTF().intern();
      created = SQLStreamables.readUnmodifiableTimestamp(in);
      lastRefreshed = SQLStreamables.readUnmodifiableTimestamp(in);
      refreshCount = in.readCompressedInt();
      expiration = SQLStreamables.readNullUnmodifiableTimestamp(in);
      disableLog = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public void refresh(long minDuration) throws IOException, SQLException {
    table.getConnector().requestUpdateInvalidating(
        true,
        AoservProtocol.CommandId.REFRESH_EMAIL_SMTP_RELAY,
        pkey,
        minDuration
    );
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
        Table.TableId.EMAIL_SMTP_RELAYS,
        pkey
    );
  }

  @Override
  public String toStringImpl() throws SQLException, IOException {
    return packageName + " " + getType().getVerb() + " from " + host + " to " + getLinuxServer().getHostname();
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeUTF(packageName.toString());
    out.writeCompressedInt(aoServer);
    out.writeUTF(host.toString());
    out.writeUTF(type);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(created.getTime());
    } else {
      SQLStreamables.writeTimestamp(created, out);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(lastRefreshed.getTime());
    } else {
      SQLStreamables.writeTimestamp(lastRefreshed, out);
    }
    out.writeCompressedInt(refreshCount);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(expiration == null ? -1 : expiration.getTime());
    } else {
      SQLStreamables.writeNullTimestamp(expiration, out);
    }
    out.writeCompressedInt(disableLog);
  }
}
