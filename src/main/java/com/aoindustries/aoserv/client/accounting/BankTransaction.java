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

package com.aoindustries.aoserv.client.accounting;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.util.InternUtils;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.sql.SQLStreamables;
import com.aoapps.sql.SQLUtility;
import com.aoapps.sql.UnmodifiableTimestamp;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
public final class BankTransaction extends AoservObject<Integer, BankTransaction> implements SingleTableObject<Integer, BankTransaction> {

  static final int COLUMN_ID = 0;
  static final String COLUMN_ID_name = "id";
  static final String COLUMN_TIME_name = "time";

  private AoservTable<Integer, BankTransaction> table;
  private int id;
  private UnmodifiableTimestamp time;
  private String account;
  private String processor;
  private User.Name administrator;
  private String type;
  private String expenseCategory;
  private String description;
  private String checkNo;
  private int amount;
  private boolean confirmed;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  BankTransaction#init(java.sql.ResultSet)
   * @see  BankTransaction#read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated(forRemoval = true)
  public BankTransaction() {
    // Do nothing
  }

  @Override
  public boolean equals(Object obj) {
    return
        (obj instanceof BankTransaction)
            && ((BankTransaction) obj).id == id;
  }

  public com.aoindustries.aoserv.client.master.User getAdministrator() throws SQLException, IOException {
    com.aoindustries.aoserv.client.master.User obj = table.getConnector().getMaster().getUser().get(administrator);
    if (obj == null) {
      throw new SQLException("Unable to find MasterUser: " + administrator);
    }
    return obj;
  }

  public int getAmount() {
    return amount;
  }

  public BankAccount getBankAccount() throws SQLException, IOException {
    BankAccount bankAccountObject = table.getConnector().getAccounting().getBankAccount().get(account);
    if (bankAccountObject == null) {
      throw new SQLException("BankAccount not found: " + account);
    }
    return bankAccountObject;
  }

  public BankTransactionType getBankTransactionType() throws SQLException, IOException {
    BankTransactionType typeObject = table.getConnector().getAccounting().getBankTransactionType().get(type);
    if (typeObject == null) {
      throw new SQLException("BankTransactionType not found: " + type);
    }
    return typeObject;
  }

  public String getCheckNo() {
    return checkNo;
  }

  @Override
  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_ID:
        return id;
      case 1:
        return time;
      case 2:
        return account;
      case 3:
        return processor;
      case 4:
        return administrator;
      case 5:
        return type;
      case 6:
        return expenseCategory;
      case 7:
        return description;
      case 8:
        return checkNo;
      case 9:
        return amount;
      case 10:
        return confirmed;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDescription() {
    return description;
  }

  public ExpenseCategory getExpenseCategory() throws SQLException, IOException {
    if (expenseCategory == null) {
      return null;
    }
    ExpenseCategory cat = table.getConnector().getAccounting().getExpenseCategory().get(expenseCategory);
    if (cat == null) {
      throw new SQLException("ExpenseCategory not found: " + expenseCategory);
    }
    return cat;
  }

  public Processor getCreditCardProcessor() throws SQLException, IOException {
    if (processor == null) {
      return null;
    }
    Processor ccProcessor = table.getConnector().getPayment().getProcessor().get(processor);
    if (ccProcessor == null) {
      throw new SQLException("CreditCardProcessor not found: " + processor);
    }
    return ccProcessor;
  }

  @Override
  public Integer getKey() {
    return id;
  }

  @Override
  public AoservTable<Integer, BankTransaction> getTable() {
    return table;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.BANK_TRANSACTIONS;
  }

  public int getId() {
    return id;
  }

  @SuppressWarnings("ReturnOfDateField") // UnmodifiableTimestamp
  public UnmodifiableTimestamp getTime() {
    return time;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    try {
      int pos = 1;
      id = result.getInt(pos++);
      time = UnmodifiableTimestamp.valueOf(result.getTimestamp(pos++));
      account = result.getString(pos++);
      processor = result.getString(pos++);
      administrator = User.Name.valueOf(result.getString(pos++));
      type = result.getString(pos++);
      expenseCategory = result.getString(pos++);
      description = result.getString(pos++);
      checkNo = result.getString(pos++);
      amount = SQLUtility.parseDecimal2(result.getString(pos++));
      confirmed = result.getBoolean(pos++);
    } catch (ValidationException e) {
      throw new SQLException(e);
    }
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    try {
      id = in.readCompressedInt();
      time = SQLStreamables.readUnmodifiableTimestamp(in);
      account = in.readUTF().intern();
      processor = InternUtils.intern(in.readNullUTF());
      administrator = User.Name.valueOf(in.readUTF()).intern();
      type = in.readUTF().intern();
      expenseCategory = InternUtils.intern(in.readNullUTF());
      description = in.readUTF();
      checkNo = in.readNullUTF();
      amount = in.readCompressedInt();
      confirmed = in.readBoolean();
    } catch (ValidationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void setTable(AoservTable<Integer, BankTransaction> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table = table;
  }

  @Override
  public String toStringImpl() {
    return id + "|" + administrator + '|' + type + '|' + SQLUtility.formatDecimal2(amount);
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_17) <= 0) {
      out.writeLong(time.getTime());
      out.writeCompressedInt(id);
    } else {
      out.writeCompressedInt(id);
      if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
        out.writeLong(time.getTime());
      } else {
        SQLStreamables.writeTimestamp(time, out);
      }
    }
    out.writeUTF(account);
    if (protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29) < 0) {
      out.writeNullUTF(null);
    } else {
      out.writeNullUTF(processor);
    }
    out.writeUTF(administrator.toString());
    out.writeUTF(type);
    out.writeNullUTF(expenseCategory);
    out.writeUTF(description);
    out.writeNullUTF(checkNo);
    out.writeCompressedInt(amount);
    out.writeBoolean(confirmed);
  }
}
