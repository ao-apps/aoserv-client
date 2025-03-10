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

package com.aoindustries.aoserv.client.billing;

import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.math.SafeMath;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.SQLUtility;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.Payment;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Each {@link Account} has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Account
 *
 * @author  AO Industries, Inc.
 */
public final class Transaction extends CachedObjectIntegerKey<Transaction> {

  static final int COLUMN_TRANSID = 1;
  static final int COLUMN_ACCOUNTING = 2;
  static final int COLUMN_SOURCE_ACCOUNTING = 3;
  static final int COLUMN_ADMINISTRATOR = 4;
  static final String COLUMN_TIME_name = "time";
  static final String COLUMN_TRANSID_name = "transid";
  static final String COLUMN_SOURCE_ACCOUNTING_name = "source_accounting";

  /**
   * Represents not being assigned for a field of the <code>int</code> type.
   */
  public static final int UNASSIGNED = -1;

  private UnmodifiableTimestamp time;
  private Account.Name accounting;
  private Account.Name sourceAccounting;
  private User.Name username;
  private String type;
  private String description;

  /**
   * The quantity in 1000th's of a unit.
   */
  private int quantity;

  private Money rate;

  private String paymentType;
  private String paymentInfo;
  private String processor;
  private int creditCardTransaction;

  /**
   * Payment confirmation.
   */
  public static final byte WAITING_CONFIRMATION = 0, CONFIRMED = 1, NOT_CONFIRMED = 2;

  /**
   * The text to display for different confirmation statuses.
   */
  private static final String[] paymentConfirmedLabels = {"Pending", "Confirmed", "Failed"};

  public static final int NUM_PAYMENT_CONFIRMATION_STATES = 3;

  static {
    assert paymentConfirmedLabels.length == NUM_PAYMENT_CONFIRMATION_STATES;
  }

  private byte paymentConfirmed;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public Transaction() {
    // Do nothing
  }

  /**
   * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
   */
  public void approved(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.TRANSACTION_APPROVED,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(creditCardTransaction);
            out.writeNullUTF(paymentInfo);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  /**
   * @deprecated  Please provide updated cardInfo via {@link #approved(int, java.lang.String)}.
   *
   * @see  #approved(int, java.lang.String)
   */
  @Deprecated
  public void approved(int creditCardTransaction) throws IOException, SQLException {
    approved(creditCardTransaction, null);
  }

  /**
   * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
   */
  public void declined(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.TRANSACTION_DECLINED,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(creditCardTransaction);
            out.writeNullUTF(paymentInfo);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  /**
   * @deprecated  Please provide updated cardInfo via {@link #declined(int, java.lang.String)}.
   *
   * @see  #declined(int, java.lang.String)
   */
  @Deprecated
  public void declined(int creditCardTransaction) throws IOException, SQLException {
    declined(creditCardTransaction, null);
  }

  /**
   * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
   */
  public void held(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
    table.getConnector().requestUpdate(
        true,
        AoservProtocol.CommandId.TRANSACTION_HELD,
        new AoservConnector.UpdateRequest() {
          private IntList invalidateList;

          @Override
          public void writeRequest(StreamableOutput out) throws IOException {
            out.writeCompressedInt(pkey);
            out.writeCompressedInt(creditCardTransaction);
            out.writeNullUTF(paymentInfo);
          }

          @Override
          public void readResponse(StreamableInput in) throws IOException, SQLException {
            int code = in.readByte();
            if (code == AoservProtocol.DONE) {
              invalidateList = AoservConnector.readInvalidateList(in);
            } else {
              AoservProtocol.checkResult(code, in);
              throw new IOException("Unexpected response code: " + code);
            }
          }

          @Override
          public void afterRelease() {
            table.getConnector().tablesUpdated(invalidateList);
          }
        }
    );
  }

  /**
   * @deprecated  Please provide updated cardInfo via {@link #held(int, java.lang.String)}.
   *
   * @see  #held(int, java.lang.String)
   */
  @Deprecated
  public void held(int creditCardTransaction) throws IOException, SQLException {
    held(creditCardTransaction, null);
  }

  /**
   * @deprecated  Please directly access via {@link #getPayment()}.
   *              Beware that {@link #getPayment()} might return {@code null}.
   *
   * @see  #getPayment()
   * @see  Payment#getAuthorizationApprovalCode()
   */
  @Deprecated
  public String getAprNum() throws SQLException, IOException {
    Payment cct = getPayment();
    return cct == null ? null : cct.getAuthorizationApprovalCode();
  }

  public Account.Name getAccount_name() {
    return accounting;
  }

  public Account getAccount() throws SQLException, IOException {
    Account business = table.getConnector().getAccount().getAccount().get(accounting);
    if (business == null) {
      throw new SQLException("Unable to find Account: " + accounting);
    }
    return business;
  }

  public Account.Name getSourceAccount_name() {
    return sourceAccounting;
  }

  public Account getSourceAccount() throws SQLException, IOException {
    Account business = table.getConnector().getAccount().getAccount().get(sourceAccounting);
    if (business == null) {
      throw new SQLException("Unable to find Account: " + sourceAccounting);
    }
    return business;
  }

  public User.Name getAdministrator_username() {
    return username;
  }

  public Administrator getAdministrator() throws SQLException, IOException {
    User un = table.getConnector().getAccount().getUser().get(username);
    // May be filtered
    if (un == null) {
      return null;
    }
    Administrator administrator = un.getAdministrator();
    if (administrator == null) {
      throw new SQLException("Unable to find Administrator: " + username);
    }
    return administrator;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case 0:
        return time;
      case COLUMN_TRANSID:
        return pkey;
      case COLUMN_ACCOUNTING:
        return accounting;
      case COLUMN_SOURCE_ACCOUNTING:
        return sourceAccounting;
      case COLUMN_ADMINISTRATOR:
        return username;
      case 5:
        return type;
      case 6:
        return description;
      case 7:
        return quantity;
      case 8:
        return rate;
      case 9:
        return paymentType;
      case 10:
        return paymentInfo;
      case 11:
        return processor;
      case 12:
        return getPayment_id();
      case 13:
        return paymentConfirmed == CONFIRMED ? "Y" : paymentConfirmed == NOT_CONFIRMED ? "N" : "W";
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDescription() {
    return description;
  }

  public String getProcessor_providerId() {
    return processor;
  }

  public Processor getProcessor() throws SQLException, IOException {
    if (processor == null) {
      return null;
    }
    Processor creditCardProcessor = table.getConnector().getPayment().getProcessor().get(processor);
    if (creditCardProcessor == null) {
      throw new SQLException("Unable to find CreditCardProcessor: " + processor);
    }
    return creditCardProcessor;
  }

  public Integer getPayment_id() {
    return creditCardTransaction == -1 ? null : creditCardTransaction;
  }

  public Payment getPayment() throws SQLException, IOException {
    if (creditCardTransaction == -1) {
      return null;
    }
    Payment cct = table.getConnector().getPayment().getPayment().get(creditCardTransaction);
    if (cct == null) {
      throw new SQLException("Unable to find CreditCardTransaction: " + creditCardTransaction);
    }
    return cct;
  }

  public byte getPaymentConfirmed() {
    return paymentConfirmed;
  }

  public static String getPaymentConfirmedLabel(int index) {
    return paymentConfirmedLabels[index];
  }

  public String getPaymentInfo() {
    return paymentInfo;
  }

  public String getPaymentType_name() {
    return paymentType;
  }

  public PaymentType getPaymentType() throws SQLException, IOException {
    if (this.paymentType == null) {
      return null;
    }
    PaymentType paymentType = table.getConnector().getPayment().getPaymentType().get(this.paymentType);
    if (paymentType == null) {
      throw new SQLException("Unable to find PaymentType: " + this.paymentType);
    }
    return paymentType;
  }

  /**
   * Gets the effective amount of quantity * rate.
   */
  public Money getAmount() {
    return rate.multiply(BigDecimal.valueOf(quantity, 3), RoundingMode.HALF_UP);
  }

  public int getQuantity() {
    return quantity;
  }

  public Money getRate() {
    return rate;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.TRANSACTIONS;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  public int getTransid() {
    return pkey;
  }

  public String getType_name() {
    return type;
  }

  public TransactionType getType() throws SQLException, IOException {
    TransactionType tt = table.getConnector().getBilling().getTransactionType().get(type);
    if (tt == null) {
      throw new SQLException("Unable to find TransactionType: " + type);
    }
    return tt;
  }

  @Override
  @SuppressWarnings("ConvertToStringSwitch")
  public void init(ResultSet result) throws SQLException {
    try {
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp("time"));
      pkey = result.getInt("transid");
      accounting = Account.Name.valueOf(result.getString("accounting"));
      sourceAccounting = Account.Name.valueOf(result.getString("source_accounting"));
      username = User.Name.valueOf(result.getString("username"));
      type = result.getString("type");
      description = result.getString("description");
      quantity = SQLUtility.parseDecimal3(result.getString("quantity"));
      rate = MoneyUtil.getMoney(result, "rate.currency", "rate.value");
      paymentType = result.getString("payment_type");
      paymentInfo = result.getString("payment_info");
      processor = result.getString("processor");
      creditCardTransaction = result.getInt("credit_card_transaction");
      if (result.wasNull()) {
        creditCardTransaction = -1;
      }
      String typeString = result.getString("payment_confirmed");
      if ("Y".equals(typeString)) {
        paymentConfirmed = CONFIRMED;
      } else if ("N".equals(typeString)) {
        paymentConfirmed = NOT_CONFIRMED;
      } else if ("W".equals(typeString)) {
        paymentConfirmed = WAITING_CONFIRMATION;
      } else {
        throw new SQLException("Unknown payment_confirmed '" + typeString + "' for transid=" + pkey);
      }
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      pkey = in.readCompressedInt();
      accounting = Account.Name.valueOf(in.readCompressedUTF()).intern();
      sourceAccounting = Account.Name.valueOf(in.readCompressedUTF()).intern();
      username = User.Name.valueOf(in.readCompressedUTF()).intern();
      type = in.readCompressedUTF().intern();
      description = in.readCompressedUTF();
      quantity = in.readCompressedInt();
      rate = MoneyUtil.readMoney(in);
      paymentType = InternUtils.intern(in.readNullUTF());
      paymentInfo = in.readNullUTF();
      processor = InternUtils.intern(in.readNullUTF());
      creditCardTransaction = in.readCompressedInt();
      paymentConfirmed = in.readByte();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public String toStringImpl() {
    return
        pkey
            + "|"
            + accounting
            + '|'
            + sourceAccounting
            + '|'
            + type
            + '|'
            + SQLUtility.formatDecimal3(quantity)
            + '×'
            + rate
            + '|'
            + (
            paymentConfirmed == CONFIRMED ? 'Y'
                : paymentConfirmed == NOT_CONFIRMED ? 'N'
                : 'W'
        );
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      out.writeLong(time.getTime());
    } else {
      SQLStreamables.writeTimestamp(time, out);
    }
    out.writeCompressedInt(pkey);
    out.writeCompressedUTF(accounting.toString(), 0);
    out.writeCompressedUTF(sourceAccounting.toString(), 1);
    out.writeCompressedUTF(username.toString(), 2);
    out.writeCompressedUTF(type, 3);
    out.writeCompressedUTF(description, 4);
    out.writeCompressedInt(quantity);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
      if (rate != null && rate.getCurrency() == Currency.USD && rate.getScale() == 2) {
        out.writeCompressedInt(SafeMath.castInt(rate.getUnscaledValue()));
      } else {
        out.writeCompressedInt(-1);
      }
    } else {
      MoneyUtil.writeMoney(rate, out);
    }
    out.writeNullUTF(paymentType);
    out.writeNullUTF(paymentInfo);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29) < 0) {
      out.writeNullUTF(null);
    } else {
      out.writeNullUTF(processor);
      out.writeCompressedInt(creditCardTransaction);
    }
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_128) < 0) {
      out.writeCompressedInt(-1);
    } else if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29) < 0) {
      out.writeNullUTF(null);
    }
    out.writeByte(paymentConfirmed);
  }
}
