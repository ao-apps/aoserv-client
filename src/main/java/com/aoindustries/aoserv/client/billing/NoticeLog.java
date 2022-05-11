/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.net.Email;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.SQLUtility;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>NoticeLog</code> entry is created when a client has been
 * notified of either a failed credit card transaction or a past due
 * debt.
 *
 * @see  NoticeType
 *
 * @author  AO Industries, Inc.
 */
public final class NoticeLog extends CachedObjectIntegerKey<NoticeLog> {

  static final int COLUMN_PKEY = 0;
  static final int COLUMN_ACCOUNTING = 2;
  static final String COLUMN_PKEY_name = "pkey";
  static final String COLUMN_CREATE_TIME_name = "create_time";

  public static final int NO_TRANSACTION = -1;

  private UnmodifiableTimestamp createTime;
  private Account.Name accounting;
  private String billingContact;
  private Email billingEmail;
  private String noticeType;
  private int transid;

  /** Protocol compatibility. */
  private int balance;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public NoticeLog() {
    // Do nothing
  }

  public int getId() {
    return pkey;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getCreateTime() {
    return createTime;
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

  public String getBillingContact() {
    return billingContact;
  }

  public Email getBillingEmail() {
    return billingEmail;
  }

  public String getNoticeType_type() {
    return noticeType;
  }

  public NoticeType getNoticeType() throws SQLException, IOException {
    NoticeType obj = table.getConnector().getBilling().getNoticeType().get(noticeType);
    if (obj == null) {
      throw new SQLException("Unable to find NoticeType: " + noticeType);
    }
    return obj;
  }

  public Integer getTransaction_id() {
    return transid == NO_TRANSACTION ? null : transid;
  }

  public Transaction getTransaction() throws IOException, SQLException {
    if (transid == NO_TRANSACTION) {
      return null;
    }
    Transaction obj = table.getConnector().getBilling().getTransaction().get(transid);
    if (obj == null) {
      throw new SQLException("Unable to find Transaction: " + transid);
    }
    return obj;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_PKEY:
        return pkey;
      case 1:
        return createTime;
      case COLUMN_ACCOUNTING:
        return accounting;
      case 3:
        return billingContact;
      case 4:
        return billingEmail;
      case 5:
        return noticeType;
      case 6:
        return getTransaction_id();
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.NOTICE_LOG;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      pkey = result.getInt("id");
      createTime = UnmodifiableTimestamp.valueOf(result.getTimestamp("create_time"));
      accounting = Account.Name.valueOf(result.getString("accounting"));
      billingContact = result.getString("billing_contact");
      billingEmail = Email.valueOf(result.getString("billing_email"));
      noticeType = result.getString("notice_type");
      transid = result.getInt("transid");
      if (result.wasNull()) {
        transid = NO_TRANSACTION;
      }
      // Protocol compatibility
      balance = SQLUtility.parseDecimal2(result.getString("balance"));
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      pkey = in.readCompressedInt();
      createTime = SQLStreamables.readUnmodifiableTimestamp(in);
      accounting = Account.Name.valueOf(in.readUTF()).intern();
      billingContact = in.readUTF();
      billingEmail = Email.valueOf(in.readUTF());
      noticeType = in.readUTF().intern();
      transid = in.readCompressedInt();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return pkey + "|" + accounting + '|' + noticeType;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeCompressedInt(pkey);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(createTime.getTime());
    } else {
      SQLStreamables.writeTimestamp(createTime, out);
    }
    out.writeUTF(accounting.toString());
    out.writeUTF(billingContact);
    out.writeUTF(billingEmail.toString());
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeCompressedInt(balance);
    }
    out.writeUTF(noticeType);
    out.writeCompressedInt(transid);
  }

  public List<NoticeLogBalance> getBalances() throws IOException, SQLException {
    return table.getConnector().getBilling().getNoticeLogBalance().getNoticeLogBalances(this);
  }
}
