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

import com.aoapps.hodgepodge.io.stream.Streamable;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServStreamable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Type;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * A <code>TransactionSearchCriteria</code> stores all the parameters
 * for a <code>Transaction</code> search.wraps all the different
 * ways in which the transaction table may be searched.
 *
 * @author  AO Industries, Inc.
 */
public final class TransactionSearchCriteria implements AOServStreamable {

  /**
   * Value representing any in a search.
   */
  public static final int ANY = -1;

  private UnmodifiableTimestamp after;
  private UnmodifiableTimestamp before;
  private int transid;
  private Account.Name account;
  private Account.Name sourceAccount;
  private User.Name administrator;
  private String type;
  private String description;
  private String paymentType;
  private String paymentInfo;
  private byte paymentConfirmed;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public TransactionSearchCriteria() {
    // Do nothing
  }

  public TransactionSearchCriteria(
      Timestamp after,
      Timestamp before,
      int transid,
      Account.Name account,
      Account.Name sourceAccount,
      User.Name administrator,
      String type,
      String description,
      String paymentType,
      String paymentInfo,
      byte paymentConfirmed
  ) {
    this.after = UnmodifiableTimestamp.valueOf(after);
    this.before = UnmodifiableTimestamp.valueOf(before);
    this.transid = transid;
    this.account = account;
    this.sourceAccount = sourceAccount;
    this.administrator = administrator;
    this.type = type;
    this.description = description;
    this.paymentType = paymentType;
    this.paymentInfo = paymentInfo;
    this.paymentConfirmed = paymentConfirmed;
  }

  public TransactionSearchCriteria(
      Timestamp after,
      Timestamp before,
      int transid,
      Account account,
      Account sourceAccount,
      Administrator administrator,
      TransactionType type,
      String description,
      PaymentType paymentType,
      String paymentInfo,
      byte paymentConfirmed
  ) {
    this(
        after,
        before,
        transid,
        account == null ? null : account.getName(),
        sourceAccount == null ? null : sourceAccount.getName(),
        administrator == null ? null : administrator.getUsername_userId(),
        type == null ? null : type.getName(),
        description,
        paymentType == null ? null : paymentType.getName(),
        paymentInfo,
        paymentConfirmed
    );
  }

  public static UnmodifiableTimestamp getDefaultAfter(long time) {
    GregorianCalendar gcal = new GregorianCalendar(Type.DATE_TIME_ZONE);
    gcal.setTimeInMillis(time);
    int year = gcal.get(Calendar.YEAR);
    int month = gcal.get(Calendar.MONTH);
    if (month == Calendar.JANUARY) {
      year--;
      month = Calendar.DECEMBER;
    } else {
      month--;
    }
    gcal.set(Calendar.YEAR, year);
    gcal.set(Calendar.MONTH, month);
    gcal.set(Calendar.DAY_OF_MONTH, 1);
    gcal.set(Calendar.HOUR_OF_DAY, 0);
    gcal.set(Calendar.MINUTE, 0);
    gcal.set(Calendar.SECOND, 0);
    gcal.set(Calendar.MILLISECOND, 0);
    return new UnmodifiableTimestamp(gcal.getTimeInMillis());
  }

  public TransactionSearchCriteria(Administrator administrator) throws IOException, SQLException {
    before = null;

    // The beginning of last month starts the default search
    after = getDefaultAfter(System.currentTimeMillis());

    transid = ANY;
    account = administrator == null ? null : administrator.getUsername().getPackage().getAccount_name();
    sourceAccount = null;
    this.administrator = null;
    type = null;
    description = null;
    paymentType = null;
    paymentInfo = null;
    paymentConfirmed = ANY;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getAfter() {
    return after;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getBefore() {
    return before;
  }

  public Account.Name getAccount() {
    return account;
  }

  public Account.Name getSourceAccount() {
    return sourceAccount;
  }

  public User.Name getAdministrator() {
    return administrator;
  }

  public String getDescription() {
    return description;
  }

  public byte getPaymentConfirmed() {
    return paymentConfirmed;
  }

  public String getPaymentInfo() {
    return paymentInfo;
  }

  public String getPaymentType() {
    return paymentType;
  }

  public String getType() {
    return type;
  }

  /**
   * @deprecated  Please use {@link TransactionTable#get(com.aoindustries.aoserv.client.billing.TransactionSearchCriteria)} directly.
   */
  @Deprecated
  public List<Transaction> getTransactions(AOServConnector connector) throws IOException, SQLException {
    return connector.getBilling().getTransaction().get(this);
  }

  public int getTransid() {
    return transid;
  }

  /**
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  public void read(StreamableInput in, String protocolVersion) throws IOException {
    read(in, AoservProtocol.Version.getVersion(protocolVersion));
  }

  // This will not be required once all clients are >= protocol 1.83.0
  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
        long l = in.readLong();
        after = l == ANY ? null : new UnmodifiableTimestamp(l);
        l = in.readLong();
        before = l == ANY ? null : new UnmodifiableTimestamp(l);
      } else {
        after = SQLStreamables.readNullUnmodifiableTimestamp(in);
        before = SQLStreamables.readNullUnmodifiableTimestamp(in);
      }
      transid = in.readCompressedInt();
      account = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
      sourceAccount = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
      administrator = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
      type = InternUtils.intern(in.readNullUTF());
      description = in.readNullUTF();
      paymentType = InternUtils.intern(in.readNullUTF());
      paymentInfo = in.readNullUTF();
      paymentConfirmed = in.readByte();
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
        in.readByte(); // sortFirst
        in.readByte(); // sortSecond
        in.readBoolean(); // sortDescending
      }
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  public void setAfter(Timestamp after) {
    this.after = UnmodifiableTimestamp.valueOf(after);
  }

  public void setBefore(Timestamp before) {
    this.before = UnmodifiableTimestamp.valueOf(before);
  }

  public void setAccount(Account.Name account) {
    this.account = account;
  }

  public void setSourceAccount(Account.Name sourceAccount) {
    this.sourceAccount = sourceAccount;
  }

  public void setAdministrator(User.Name administrator) {
    this.administrator = administrator;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPaymentConfirmed(byte paymentConfirmed) {
    this.paymentConfirmed = paymentConfirmed;
  }

  public void setPaymentInfo(String paymentInfo) {
    this.paymentInfo = paymentInfo;
  }

  public void setPaymentType(String paymentType) {
    this.paymentType = paymentType;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setTransid(int transid) {
    this.transid = transid;
  }

  /**
   * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
   *
   * @see  #write(com.aoapps.hodgepodge.io.stream.StreamableOutput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated
  @Override
  public void write(StreamableOutput out, String protocolVersion) throws IOException {
    write(out, AoservProtocol.Version.getVersion(protocolVersion));
  }

  // This will not be required once all clients are >= protocol 1.83.0
  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(after == null ? ANY : after.getTime());
      out.writeLong(before == null ? ANY : before.getTime());
    } else {
      SQLStreamables.writeNullTimestamp(after, out);
      SQLStreamables.writeNullTimestamp(before, out);
    }
    out.writeCompressedInt(transid);
    out.writeNullUTF(Objects.toString(account, null));
    out.writeNullUTF(Objects.toString(sourceAccount, null));
    out.writeNullUTF(Objects.toString(administrator, null));
    out.writeNullUTF(type);
    out.writeNullUTF(description);
    out.writeNullUTF(paymentType);
    out.writeNullUTF(paymentInfo);
    out.writeByte(paymentConfirmed);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeByte(0); // sortFirst
      out.writeByte(0); // sortSecond
      out.writeBoolean(true); // sortDescending
    }
  }
}
