/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2026  AO Industries, Inc.
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
import com.aoapps.net.DomainLabel;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.dns.Record;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Represents one DKIM key with a unique selector for a {@link Domain}.
 *
 * @see  Domain
 *
 * @author  AO Industries, Inc.
 */
public final class DkimKey extends CachedObjectIntegerKey<DkimKey> {

  static final int COLUMN_ID = 0;
  static final int COLUMN_DOMAIN = 1;
  static final int COLUMN_SELECTOR = 2;
  static final int COLUMN_DNS_RECORD = 6;
  static final String COLUMN_DOMAIN_name = "domain";
  static final String COLUMN_SELECTOR_name = "selector";

  // Matches aoserv-master-db/aoindustries/email/DkimKey.Status-type.sql
  public enum Status {
    /**
     * A key that has been created but not yet used, could be waiting for DNS propagation time
     * or on reserve for quick deployment in case of security compromise.
     */
    NEW("new"),

    /**
     * The key used for current signing, only one may be active per {@link Domain}.
     */
    SIGNING("signing"),

    /**
     * A key left for some time after rotation for verifying older messages.
     */
    OLD("old"),

    /**
     * Old key remains until DNS is confirmed to be removed, after which it may be deleted completely.
     */
    REMOVAL("removal");

    private static final Status[] values = values();

    private static Status getFromDbValue(String dbValue) {
      if (dbValue == null) {
        return null;
      }
      for (Status value : values) {
        if (dbValue.equals(value.dbValue)) {
          return value;
        }
      }
      throw new IllegalArgumentException("Status not found from dbValue: " + dbValue);
    }

    private final String dbValue;

    private Status(String dbValue) {
      this.dbValue = dbValue;
    }

    public String getDbValue() {
      return dbValue;
    }
  }

  private int domain;
  private DomainLabel selector;
  private UnmodifiableTimestamp created;
  private Status status;
  private UnmodifiableTimestamp statusTime;
  private int dnsRecord;
  private UnmodifiableTimestamp dnsConfirmed;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  DkimKey#init(java.sql.ResultSet)
   * @see  DkimKey#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public DkimKey() {
    // Do nothing
  }

  @Override
  public String toStringImpl() throws IOException, SQLException {
    return "DKIM Key " + selector + "@" + getDomain().getDomain();
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return pkey;
      case COLUMN_DOMAIN:
        return domain;
      case COLUMN_SELECTOR:
        return selector;
      case 3:
        return created;
      case 4:
        return status.getDbValue();
      case 5:
        return statusTime;
      case COLUMN_DNS_RECORD:
        return dnsRecord == -1 ? null : dnsRecord;
      case 7:
        return dnsConfirmed;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.email_DkimKey;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      pkey = result.getInt(pos++);
      domain = result.getInt(pos++);
      selector = DomainLabel.valueOf(result.getString(pos++));
      created = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      status = Status.getFromDbValue(result.getString(pos++));
      statusTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      dnsRecord = result.getInt(pos++);
      if (result.wasNull()) {
        dnsRecord = -1;
      }
      dnsConfirmed = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      domain = in.readCompressedInt();
      selector = DomainLabel.valueOf(in.readUTF());
      created = SQLStreamables.readUnmodifiableTimestamp(in);
      status = in.readEnum(Status.class);
      statusTime = SQLStreamables.readUnmodifiableTimestamp(in);
      dnsRecord = in.readCompressedInt();
      dnsConfirmed = SQLStreamables.readNullUnmodifiableTimestamp(in);
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    out.writeCompressedInt(domain);
    out.writeUTF(selector.toString());
    SQLStreamables.writeTimestamp(created, out);
    out.writeEnum(status);
    SQLStreamables.writeTimestamp(statusTime, out);
    out.writeCompressedInt(dnsRecord);
    SQLStreamables.writeNullTimestamp(dnsConfirmed, out);
  }

  /**
   * The generated primary key.
   */
  public int getId() {
    return pkey;
  }

  /**
   * The email domain for this key.
   */
  public int getDomain_id() {
    return domain;
  }

  /**
   * The email domain for this key.
   */
  public Domain getDomain() throws SQLException, IOException {
    Domain obj = table.getConnector().getEmail().getDomain().get(domain);
    if (obj == null) {
      throw new SQLException("Unable to find email.Domain: " + domain);
    }
    return obj;
  }

  /**
   * The per-domain unique name.
   */
  public DomainLabel getSelector() {
    return selector;
  }

  /**
   * The time this key was added.
   */
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreated() {
    return created;
  }

  /**
   * The current status.
   * Only one may be {@link Status#SIGNING} per domain.
   */
  public Status getStatus() {
    return status;
  }

  /**
   * The time at last status change.
   */
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getStatusTime() {
    return statusTime;
  }

  /**
   * The DNS record associated with this key.
   * May be {@code null} when DNS hosted elsewhere.
   */
  public OptionalInt getDnsRecord_id() {
    return dnsRecord == -1 ? OptionalInt.empty() : OptionalInt.of(dnsRecord);
  }

  /**
   * The DNS record associated with this key.
   * May be {@link java.util.Optional#empty()} when DNS hosted elsewhere.
   */
  public Optional<Record> getDnsRecord() throws SQLException, IOException {
    if (dnsRecord == -1) {
      return Optional.empty();
    }
    Record obj = table.getConnector().getDns().getRecord().get(dnsRecord);
    if (obj == null) {
      throw new SQLException("Unable to find dns.Record: " + dnsRecord);
    }
    return Optional.of(obj);
  }

  /**
   * The time DNS TXT presence and correctness was confirmed.
   * Required for statuses {@link Status#SIGNING} and {@link Status#OLD}.
   */
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public Optional<UnmodifiableTimestamp> getDnsConfirmed() {
    return Optional.ofNullable(dnsConfirmed);
  }
}
