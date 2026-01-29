/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2007-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2024, 2025  AO Industries, Inc.
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
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Logs the whois history for each registrable domain.
 *
 * @author  AO Industries, Inc.
 */
public final class WhoisHistory extends CachedObjectIntegerKey<WhoisHistory> {

  static final int COLUMN_id = 0;
  static final int COLUMN_output = 4;
  static final int COLUMN_error = 5;
  static final String COLUMN_registrableDomain_name = "registrableDomain";
  static final String COLUMN_time_name = "time";

  private DomainName registrableDomain;
  private UnmodifiableTimestamp time;
  private Integer exitStatus;

  /**
   * Note: these are loaded in a separate call to the master as-needed to conserve heap space, and it is null to begin with.
   */
  private static class OutputLock {
    // Empty lock class to help heap profile
  }

  private final OutputLock outputLock = new OutputLock();
  private String output;
  private String error;

  // Protocol conversion
  private Account.Name accounting;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  WhoisHistory#init(java.sql.ResultSet)
   * @see  WhoisHistory#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public WhoisHistory() {
    // Do nothing
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) throws IOException, SQLException {
    switch (i) {
      case COLUMN_id:
        return pkey;
      case 1:
        return registrableDomain;
      case 2:
        return time;
      case 3:
        return exitStatus;
      case COLUMN_output:
        return getOutput();
      case COLUMN_error:
        return getError();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public int getId() {
    return pkey;
  }

  /**
   * Gets the registrable domain that was queried in the whois system.
   */
  public DomainName getRegistrableDomain() {
    return registrableDomain;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  public Integer getExitStatus() {
    return exitStatus;
  }

  /**
   * Loads output and error when first needed.
   */
  private void loadOutput() throws IOException, SQLException {
    assert Thread.holdsLock(outputLock);
    if (error == null) {
      table.getConnector().requestResult(
          true,
          AoservProtocol.CommandId.GET_WHOIS_HISTORY_WHOIS_OUTPUT,
          new AoservConnector.ResultRequest<>() {
            @Override
            public void writeRequest(StreamableOutput out) throws IOException {
              out.writeCompressedInt(pkey);
            }

            @Override
            public void readResponse(StreamableInput in) throws IOException, SQLException {
              int code = in.readByte();
              if (code == AoservProtocol.DONE) {
                output = in.readUTF();
                error = in.readUTF();
              } else {
                AoservProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: " + code);
              }
            }

            @Override
            public Void afterRelease() {
              return null;
            }
          }
      );
    }
  }

  /**
   * Gets the whois output from the database.  The first access to this, or {@link WhoisHistory#getError()}, for a specific object instance
   * will query the master server for the information and then cache the results.  This is done
   * to conserve heap space while still yielding high performance through the caching of the rest of the fields.
   *
   * <p>From an outside point of view, the object is still immutable and will yield constant return
   * values per instance.</p>
   */
  public String getOutput() throws IOException, SQLException {
    synchronized (outputLock) {
      loadOutput();
      return output;
    }
  }

  /**
   * Gets the whois error from the database.  The first access to this, or {@link WhoisHistory#getOutput()}, for a specific object instance
   * will query the master server for the information and then cache the results.  This is done
   * to conserve heap space while still yielding high performance through the caching of the rest of the fields.
   *
   * <p>From an outside point of view, the object is still immutable and will yield constant return
   * values per instance.</p>
   */
  public String getError() throws IOException, SQLException {
    synchronized (outputLock) {
      loadOutput();
      return error;
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.WhoisHistory;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      registrableDomain = DomainName.valueOf(result.getString(pos++));
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      exitStatus = result.getInt(pos++);
      if (result.wasNull()) {
        exitStatus = null;
      }

      // Note: these are loaded in a separate call to the master as-needed to conserve heap space:
      // output = result.getString(pos++);
      // error = result.getString(pos++);

      // Protocol conversion
      accounting = Account.Name.valueOf(result.getString(pos++));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      registrableDomain = DomainName.valueOf(in.readUTF()).intern();
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      exitStatus = in.readNullInteger();
      // Note: these are loaded in a separate call to the master as-needed to conserve heap space:
      // output = in.readUTF();
      // error = in.readUTF();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_19) >= 0) {
      out.writeUTF(registrableDomain.toString());
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(time.getTime());
    } else {
      SQLStreamables.writeTimestamp(time, out);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_19) < 0) {
      out.writeUTF(accounting.toString());
      // Was "zone" type with trailing period
      out.writeUTF(registrableDomain.toString() + ".");
    } else {
      out.writeNullInteger(exitStatus);
    }

    // Note: these are loaded in a separate call to the master as-needed to conserve heap space:
    // out.writeUTF(output);
    // out.writeUTF(error);
  }

  @Override
  public String toStringImpl() {
    return pkey + "|" + registrableDomain + "|" + getTime();
  }

  /**
   * @see  WhoisHistoryAccountTable#getWhoisHistoryAccounts(com.aoindustries.aoserv.client.billing.WhoisHistory)
   */
  public List<WhoisHistoryAccount> getAccounts() throws IOException, SQLException {
    return table.getConnector().getBilling().getWhoisHistoryAccount().getWhoisHistoryAccounts(this);
  }
}
